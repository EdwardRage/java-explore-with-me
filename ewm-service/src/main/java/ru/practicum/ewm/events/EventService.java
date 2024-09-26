package ru.practicum.ewm.events;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventDto;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {

    EventFullDto create(long userId, NewEventDto newEventDto);

    List<EventShortDto> get(long userId, int from, int size);

    EventFullDto getById(long userId, long eventId);

    EventFullDto updateByUser(long userId, long eventId, UpdateEventDto updateEvent);

    EventFullDto updateByAdmin(long eventId, UpdateEventDto updateEvent);

    List<EventFullDto> getByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                  String rangeState, String rangeEnd, Integer from, Integer size);

    List<ParticipationRequestDto> getRequestForEvent(long userId, long eventId);

    EventRequestStatusUpdateResult processingRequest(long userId, long eventId,
                                                     EventRequestStatusUpdateRequest statusUpdateRequest);

    List<EventShortDto> getWithFilter2(String text, List<Long> categories, Boolean paid, String rangeStart,
                                      String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                      HttpServletRequest request);

    EventFullDto getEventById(long eventId, HttpServletRequest request);
}
