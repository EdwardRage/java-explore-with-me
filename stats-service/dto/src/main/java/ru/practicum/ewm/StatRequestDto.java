package ru.practicum.ewm;

import lombok.Builder;
import lombok.Data;

@Data
public class StatRequestDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
