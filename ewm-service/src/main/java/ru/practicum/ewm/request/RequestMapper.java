package ru.practicum.ewm.request;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RequestMapper {

    public Request toRequest(User user, Event event) {
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setUser(user);
        request.setEvent(event);
        request.setStatus(RequestStatus.PENDING);
        return request;
    }

    public ParticipationRequestDto toDto(Request request) {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(request.getId());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setRequester(request.getUser().getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setStatus(request.getStatus());
        return requestDto;
    }

    public EventRequestStatusUpdateResult toRequestResult(List<ParticipationRequestDto> requestList) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        for (ParticipationRequestDto request : requestList) {
            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                //result.getConfirmedRequests().add(request);
                result.addConfirmedRequest(request);
            } else if (request.getStatus().equals(RequestStatus.REJECTED)) {
                //result.getRejectedRequests().add(request);
                result.addRejectedRequest(request);
            }
        }
        return result;
    }
}
