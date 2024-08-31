package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository hitRepository;
    private final StatMapper mapper;

    @Override
    public StatResponseDto create(StatRequestDto hitRequestDto) {

        LocalDateTime dateTime = encodeTime(hitRequestDto.getTimestamp());
        StatModel statModel = mapper.mapToEndpointHit(hitRequestDto, dateTime);
        hitRepository.save(statModel);
        StatModel statsFull = hitRepository.save(statModel);
        return mapper.mapToEndpointHitResponse(statsFull);
    }

    private LocalDateTime encodeTime(String timeStr) {
        String time = timeStr.replace(" ", "T");
        LocalDateTime dateTime = LocalDateTime.parse(time);
        return dateTime;
    }

    @Override
    public List<StatResponseDto> get(String start, String end, String[] uri, Boolean unique) {
        LocalDateTime startTime = encodeTime(start);
        LocalDateTime endTime = encodeTime(end);
        List<String> urisList = Arrays.asList(uri);
        if (unique) {
            return hitRepository.getStats3WithUniqueIp(startTime, endTime, urisList);
        }
        return hitRepository.getStats3(startTime, endTime, urisList);
    }
}
