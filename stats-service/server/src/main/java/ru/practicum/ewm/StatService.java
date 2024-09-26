package ru.practicum.ewm;

import java.util.List;

public interface StatService {
    StatResponseDto create(StatRequestDto hitRequestDto);

    List<StatResponseDto> get(String start, String end, String[] uri, Boolean unique);

    List<StatResponseDto> getStat(String start, String end, String[] uri, Boolean unique);
}
