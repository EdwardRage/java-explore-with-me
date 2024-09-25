package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateCompilationRequest {
    private List<Integer> events = new ArrayList<>();

    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;
}
