package ru.practicum.ewm.events.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.EventService;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.UpdateEventDto;
import ru.practicum.ewm.events.model.EventState;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable long eventId,
                               @Valid @RequestBody UpdateEventDto updateEvent) {
        EventFullDto event = eventService.updateByAdmin(eventId, updateEvent);
        log.info("Мероприятие {} изменено ", event.getTitle());
        return event;
    }

    @GetMapping
    public List<EventFullDto> get(@RequestParam(required = false) List<Long> users,
                                  @RequestParam(required = false) List<EventState> states,
                                  @RequestParam(required = false) List<Long> categories,
                                  @RequestParam(required = false) String rangeStart,
                                  @RequestParam(required = false) String rangeEnd,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        List<EventFullDto> listEvent = eventService.getByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Поиск событий админом");
        return listEvent;
    }
}
