package ru.practicum.ewm.request.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Integer> requestIds;

    private RequestUpdateStatus status;

    public enum RequestUpdateStatus {
        CONFIRMED, REJECTED
    }
}

