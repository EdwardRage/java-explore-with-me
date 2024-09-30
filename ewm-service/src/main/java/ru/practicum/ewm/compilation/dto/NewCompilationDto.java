package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NewCompilationDto {
    private List<Integer> events;

    @Builder.Default
    private boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}
