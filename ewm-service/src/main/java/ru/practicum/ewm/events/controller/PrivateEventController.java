package ru.practicum.ewm.events.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.EventService;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventDto;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Valid
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable long userId,
                               @Valid @RequestBody NewEventDto newEventDto) {
        EventFullDto eventFullDto = eventService.create(userId, newEventDto);
        log.info("Новое событие добавлено {}", eventFullDto);
        return eventFullDto;
    }

    @GetMapping
    public List<EventShortDto> get(@PathVariable long userId,
                                   @RequestParam(defaultValue = "0") Integer from,
                                   @RequestParam(defaultValue = "10") Integer size) {
        List<EventShortDto> eventList = eventService.get(userId, from, size);
        log.info("Список событий, добавленный пользователем {}, eventList {}", userId, eventList);

        return eventList;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable long userId,
                                @PathVariable long eventId) {
        EventFullDto event = eventService.getById(userId, eventId);
        log.info("Получение полной информации о событии добавленном текущим пользователем");
        return event;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable long userId,
                               @PathVariable long eventId,
                               @Valid @RequestBody UpdateEventDto updateEvent) {
        EventFullDto event = eventService.updateByUser(userId, eventId, updateEvent);
        log.info("Меропритие {} изменено ", event.getTitle());
        return event;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestForEvent(@PathVariable long userId,
                                                            @PathVariable long eventId) {
        List<ParticipationRequestDto> requestDtoList = eventService.getRequestForEvent(userId, eventId);
        log.info("Список заявок на участие для мероприятия с Id {}", eventId);
        return requestDtoList;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult processingRequest(@PathVariable long userId,
                                                            @PathVariable long eventId,
                                                            @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
        EventRequestStatusUpdateResult result = eventService.processingRequest(userId, eventId, statusUpdateRequest);
        log.info("Статусы заявок на мероприятие изменены");
        return result;
    }
}
