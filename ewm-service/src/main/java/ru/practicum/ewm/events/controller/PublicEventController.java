package ru.practicum.ewm.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.EventService;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> get(@RequestParam(required = false) String text,
                                   @RequestParam(required = false) List<Long> categories,
                                   @RequestParam(required = false) Boolean paid,
                                   @RequestParam(required = false) String rangeStart,
                                   @RequestParam(required = false) String rangeEnd,
                                   @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                   @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                   @RequestParam(defaultValue = "10") @Positive Integer size,
                                   HttpServletRequest request) {

        List<EventShortDto> eventList = eventService.getWithFilter2(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request);
        log.info("Поиск с фильтрацией: text = {}, categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, onlyAvailable = {} " +
                "sort = {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        return eventList;
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable long id, HttpServletRequest request) {
        EventFullDto eventFullDto = eventService.getEventById(id, request);
        log.info("Получение подробной информации о событии с id = {}", id);
        return eventFullDto;
    }
}
