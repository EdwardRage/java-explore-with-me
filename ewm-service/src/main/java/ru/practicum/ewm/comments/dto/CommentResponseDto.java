package ru.practicum.ewm.comments.dto;

import lombok.Data;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.user.dto.UserShortDto;

@Data
public class CommentResponseDto {
    private long id;
    private UserShortDto user;
    private EventShortDto event;
    private String text;
}
