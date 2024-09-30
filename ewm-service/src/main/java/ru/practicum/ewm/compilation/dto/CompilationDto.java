package ru.practicum.ewm.compilation.dto;

import lombok.Data;
import ru.practicum.ewm.events.dto.EventShortDto;

import java.util.HashSet;
import java.util.Set;

@Data
public class CompilationDto {

    private Long id;

    private Set<EventShortDto> events = new HashSet<>();

    private Boolean pinned;

    private String title;
}
