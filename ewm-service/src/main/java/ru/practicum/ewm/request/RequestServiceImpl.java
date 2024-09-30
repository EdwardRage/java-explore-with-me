package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.exception.CustomException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto create(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие с id = " + eventId + " не найдено"));
        checkRepeatRequest(user);
        if (event.getInitiator().getId() != user.getId() && event.getState().equals(EventState.PUBLISHED)) {

            if (event.getConfirmedRequest() >= event.getParticipantLimit() && event.getParticipantLimit() > 0) {
                throw new CustomException("Достигнуто максимальное количество участников мероприятия");
            }
            Request request = requestMapper.toRequest(user, event);
            if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
                request.setStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequest(event.getConfirmedRequest() + 1);
                eventRepository.save(event); //обновил количество одобренных заявок
            }
            requestRepository.save(request);
            return requestMapper.toDto(request);
        }
        throw new CustomException("Ошибка в подаче заявки");
    }

    @Override
    public List<ParticipationRequestDto> get(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        return requestRepository.findAllByRequesterId(user.getId()).stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto cansel(long userId, long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Request request = requestRepository.findRequestById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на участие с id = " + requestId + " не найден"));
        if (request.getUser().getId() == user.getId()) {
            if (!request.getStatus().equals(RequestStatus.CONFIRMED)) {
                request.setStatus(RequestStatus.CANCELED);
                requestRepository.save(request);
            } else {
                Event event = request.getEvent();
                event.setConfirmedRequest(event.getConfirmedRequest() - 1); //из-за отказа уменьшил количество одобренных заявок
                eventRepository.save(event);
                request.setStatus(RequestStatus.CANCELED);
                requestRepository.save(request);
            }

            return requestMapper.toDto(request);
        }
        throw new CustomException("Ошибка в отмене запроса на мероприятие");
    }

    private void checkRepeatRequest(User user) {
        Request request = requestRepository.findRequestByUser(user);
        if (request != null)  {
            throw new CustomException("Нельзя добавить повторный запрос");
        }
    }
}
