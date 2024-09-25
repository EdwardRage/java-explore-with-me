package ru.practicum.ewm.events;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.LocationDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class EventMapper {

    public Event mapToEvent(NewEventDto newEvent, User user, Category category) {
        Event event = new Event();

        event.setAnnotation(newEvent.getAnnotation());

        event.setCategory(category);

        event.setDescription(newEvent.getDescription());

        event.setEventDate(newEvent.getEventDate());

        event.setPaid(newEvent.getPaid());

        event.setParticipantLimit(newEvent.getParticipantLimit());

        event.setRequestModeration(newEvent.getRequestModeration());

        event.setTitle(newEvent.getTitle());

        event.setInitiator(user);

        event.setState(EventState.PENDING);

        event.setCreatedOn(LocalDateTime.now());

        event.setLat(newEvent.getLocation().getLat());

        event.setLon(newEvent.getLocation().getLon());

        return event;
    }

    public EventFullDto mapToEventFullDto(Event event, Long views) {
        EventFullDto eventFull = new EventFullDto();

        eventFull.setId(event.getId());
        eventFull.setAnnotation(event.getAnnotation());
        eventFull.setCategory(event.getCategory());
        eventFull.setConfirmedRequests(event.getConfirmedRequest());
        eventFull.setCreatedOn(event.getCreatedOn());
        eventFull.setDescription(event.getDescription());
        eventFull.setEventDate(event.getEventDate());
        eventFull.setPaid(event.isPaid());
        eventFull.setParticipantLimit(event.getParticipantLimit());
        eventFull.setRequestModeration(event.isRequestModeration());
        eventFull.setState(event.getState().toString());
        eventFull.setTitle(event.getTitle());
        eventFull.setViews(views);

        eventFull.setLocation(toLocation(event.getLon(), event.getLat()));
        return eventFull;
    }

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFull = new EventFullDto();

        eventFull.setId(event.getId());
        eventFull.setAnnotation(event.getAnnotation());
        eventFull.setCategory(event.getCategory());
        eventFull.setConfirmedRequests(event.getConfirmedRequest());
        eventFull.setCreatedOn(event.getCreatedOn());
        eventFull.setDescription(event.getDescription());
        eventFull.setEventDate(event.getEventDate());
        eventFull.setInitiator(toUserShortDto(event.getInitiator()));
        eventFull.setPaid(event.isPaid());
        eventFull.setParticipantLimit(event.getParticipantLimit());
        eventFull.setRequestModeration(event.isRequestModeration());
        eventFull.setState(event.getState().toString());
        eventFull.setTitle(event.getTitle());

        eventFull.setLocation(toLocation(event.getLon(), event.getLat()));
        return eventFull;
    }

    public EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShort = new EventShortDto();
        eventShort.setId(event.getId());
        eventShort.setAnnotation(event.getAnnotation());
        eventShort.setCategory(event.getCategory());
        eventShort.setEventDate(event.getEventDate());
        eventShort.setInitiator(toUserShortDto(event.getInitiator()));
        eventShort.setPaid(event.isPaid());
        eventShort.setTitle(event.getTitle());
        return eventShort;
    }

    private UserShortDto toUserShortDto(User user) {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());
        return userShortDto;
    }

    private LocationDto toLocation(BigDecimal lon, BigDecimal lat) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLat(lat);
        locationDto.setLon(lon);
        return locationDto;
    }

}
