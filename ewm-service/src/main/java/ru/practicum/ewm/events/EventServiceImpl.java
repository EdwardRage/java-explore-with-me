package ru.practicum.ewm.events;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatResponseDto;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.model.StateAction;
import ru.practicum.ewm.exception.BadRequestCustomException;
import ru.practicum.ewm.exception.CustomException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final EventMapper mapperTest;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public EventFullDto create(long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не авторизован"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категории с id = " + newEventDto.getCategory() + " не существует"));

        checkEventDate(newEventDto.getEventDate());

        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        Event event = mapperTest.mapToEvent(newEventDto, user, category);

        event = eventRepository.save(event);

        return mapperTest.mapToEventFullDto(event, 0L);
    }

    @Override
    public List<EventShortDto> get(long userId, int from, int size) {
        return eventRepository.findAllByInitiatorId(userId).stream()
                .skip(from)
                .limit(size)
                .map(mapperTest::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto getById(long userId, long eventId) {
        Event event = eventRepository.findAllByInitiatorIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Событие с eventId = " + eventId + " не найдено"));
        long views = getViews(event);

        return  mapperTest.mapToEventFullDto(event, views);
    }

    @Override
    public EventFullDto updateByUser(long userId, long eventId, UpdateEventDto updateEvent) {
        Event oldEvent = eventRepository.findAllByInitiatorIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Событие с eventId = " + eventId + " не найдено"));

        if (oldEvent.getState().equals(EventState.PENDING) || oldEvent.getState().equals(EventState.CANCELED)) {
            updateEvent(oldEvent, updateEvent);
            if (updateEvent.getStateAction() != null && !updateEvent.getStateAction().isEmpty()) {
                if (updateEvent.getStateAction().equals(StateAction.CANCEL_REVIEW.toString())) {
                    oldEvent.setState(EventState.CANCELED);
                }
                if (updateEvent.getStateAction().equals(StateAction.SEND_TO_REVIEW.toString())) {
                    oldEvent.setState(EventState.PENDING);
                }
            }
            eventRepository.save(oldEvent);
            return mapperTest.mapToEventFullDto(oldEvent, 0L);
        }

        throw new CustomException("Нельзя внести изменения в данное мероприятия");
    }

    @Transactional
    @Override
    public EventFullDto updateByAdmin(long eventId, UpdateEventDto updateEvent) {
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с eventId = " + eventId + " не найдено"));
        if (oldEvent.getState().equals(EventState.PENDING)) {
            updateEvent(oldEvent, updateEvent);
            if (updateEvent.getStateAction() != null && !updateEvent.getStateAction().isEmpty()) {
                if (updateEvent.getStateAction().equals(StateAction.PUBLISH_EVENT.toString())) {
                    oldEvent.setState(EventState.PUBLISHED);
                    oldEvent.setPublishedOn(LocalDateTime.now());
                } else {
                    oldEvent.setState(EventState.CANCELED);
                }
            }
            eventRepository.save(oldEvent);

            long views = getViews(oldEvent);

            return  mapperTest.mapToEventFullDto(oldEvent, views);
        }

        throw new CustomException("Нельзя внести изменения в данное мероприятия");
    }

    @Override
    public List<EventFullDto> getByAdmin(List<Long> users,
                                         List<EventState> states,
                                         List<Long> categories,
                                         String rangeStart,
                                         String rangeEnd,
                                         Integer from,
                                         Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        root.fetch("category", JoinType.INNER);
        root.fetch("initiator", JoinType.INNER);

        Predicate predicates = criteriaBuilder.conjunction();

        if (users != null && !users.isEmpty()) {
            predicates = criteriaBuilder.and(predicates, root.get("initiator").get("id").in(users));
        }
        if (states != null && !states.isEmpty()) {
            predicates = criteriaBuilder.and(predicates, root.get("state").in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            predicates = criteriaBuilder.and(predicates, root.get("category").get("id").in(categories));
        }

        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime start = encodeTime(rangeStart);
            LocalDateTime end = encodeTime(rangeEnd);

            if (start.isAfter(end)) {
                throw new BadRequestCustomException("Start должен быть раньше, чем end");
            }
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.between(root.get("eventDate"), start, end));
        }

        criteriaQuery.where(predicates);

        return entityManager.createQuery(criteriaQuery).getResultList().stream()
                .map(mapperTest::toEventFullDto)
                .skip(from)
                .limit(size)
                .toList();
    }

    @Override
    public List<ParticipationRequestDto> getRequestForEvent(long userId, long eventId) {
        Event event = eventRepository.findAllByInitiatorIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Событие с eventId = " + eventId + " не найдено"));
        return requestRepository.findRequestByEventId(event.getId()).stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult processingRequest(long userId, long eventId,
                                                            EventRequestStatusUpdateRequest statusUpdateRequest) {
        Event event = eventRepository.findAllByInitiatorIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Событие с eventId = " + eventId + " не найдено"));
        if (event.isRequestModeration() && event.getParticipantLimit() > 0) {

            List<Request> requestList = requestRepository.findRequests(statusUpdateRequest.getRequestIds(), event.getId());

            for (Request request : requestList) {

                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new CustomException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
                }
                if (statusUpdateRequest.getStatus().equals(EventRequestStatusUpdateRequest.RequestUpdateStatus.CONFIRMED)) {
                    if (event.getConfirmedRequest() >= event.getParticipantLimit()) {
                        throw new CustomException("Достигнут лимит по зявкам на мероприятие");
                    }
                    request.setStatus(RequestStatus.CONFIRMED); //статусы изменены
                    requestRepository.save(request);

                    event.setConfirmedRequest(event.getConfirmedRequest() + 1);
                    eventRepository.save(event);
                } else if (statusUpdateRequest.getStatus().equals(EventRequestStatusUpdateRequest.RequestUpdateStatus.REJECTED)) {

                    request.setStatus(RequestStatus.REJECTED); //статусы изменены
                    requestRepository.save(request);
                }
            }

            List<ParticipationRequestDto> requestDtos = requestList.stream()
                    .map(requestMapper::toDto)
                    .toList();
            return requestMapper.toRequestResult(requestDtos);
        }
        return null;
    }

    @Override
    public List<EventShortDto> getWithFilter2(String text, List<Long> categories, Boolean paid,
                                      String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                      String sort, Integer from, Integer size,
                                      HttpServletRequest request) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        root.fetch("category", JoinType.INNER);
        root.fetch("initiator", JoinType.INNER);

        Predicate predicates = criteriaBuilder.conjunction();

        if (text != null && !text.isEmpty()) {
            //String textDecode = URLDecoder.decode(text, StandardCharsets.UTF_8);
            String textDecode = java.net.URLDecoder.decode(text, StandardCharsets.UTF_8);
            predicates = criteriaBuilder.or(predicates, criteriaBuilder.or(
                    criteriaBuilder.like(root.get("title"), "%" + textDecode.toLowerCase() + "%"),
                    criteriaBuilder.like(root.get("description"), "%" + textDecode.toLowerCase() + "%")
            ));
        }

        if (categories != null && !categories.isEmpty()) {
            predicates = criteriaBuilder.and(predicates, root.get("category").get("id").in(categories));
        }

        if (paid != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("paid"), paid));
        }

        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime start = encodeTime(rangeStart);
            LocalDateTime end = encodeTime(rangeEnd);

            if (start.isAfter(end)) {
                throw new BadRequestCustomException("Start должен быть раньше, чем end");
            }
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.between(root.get("eventDate"), start, end));
        }

        if (onlyAvailable != null && onlyAvailable) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.greaterThan(root.get("participantLimit"), 0));
        }

        if ("EVENT_DATE".equalsIgnoreCase(sort)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("eventDate")));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("eventDate")));
        }

        criteriaQuery.where(predicates);

        addStats(request);

        return entityManager.createQuery(criteriaQuery).getResultList().stream()
                .map(mapperTest::toEventShortDto)
                .skip(from)
                .limit(size)
                .toList();
    }

    @Override
    public EventFullDto getEventById(long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с eventId = " + eventId + " не найдено"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            addStats(request);
            long views = getViews(event);

            return mapperTest.mapToEventFullDto(event, views);
        }
        throw new NotFoundException("Мероприятие должно быть опубликовано");
    }

    private void updateEvent(Event event, UpdateEventDto updateEvent) {
        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id = " + updateEvent.getCategory() + " не найдена"));
            event.setCategory(category);
        }
        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestCustomException("дата и время события не может быть раньше, чем через два часа " +
                        "от текущего момента");
            }
            event.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getLocation() != null) {
            event.setLat(updateEvent.getLocation().getLat());
            event.setLon(updateEvent.getLocation().getLon());
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }
    }

    private LocalDateTime encodeTime(String timeStr) {
        String time = timeStr.replace(" ", "T");
        LocalDateTime dateTime = LocalDateTime.parse(time);
        return dateTime;
    }

    private void addStats(HttpServletRequest request) {
        statsClient.addStats(request);
    }

    private void checkEventDate(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestCustomException("дата и время события не может быть раньше, чем через два часа " +
                    "от текущего момента");
        }
    }

    private long getViews(Event event) {
        List<String> uris = List.of("/events/" + event.getId());
        List<StatResponseDto> responseDtoList = statsClient.getStats(LocalDateTime.now().minusYears(13).toString(),
                LocalDateTime.now().plusYears(13).toString(),
                uris, true);
        if (responseDtoList == null || responseDtoList.isEmpty()) {
            return 0;
        }
        return responseDtoList.getFirst().getHits();
    }
}
