package ru.practicum.ewm.events.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationDto {
    private BigDecimal lat;
    private BigDecimal lon;
}
