package ru.practicum.ewm.comments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewCommentParams {
    private long userId;
    private long eventId;
    @NotBlank
    private String text;
}
