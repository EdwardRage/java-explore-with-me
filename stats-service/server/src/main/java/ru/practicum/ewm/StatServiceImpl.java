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
        if (uri == null || uri.length == 0) {
            return hitRepository.getStatsWithoutUri(startTime, endTime);
        }
        List<String> urisList = Arrays.asList(uri);
        if (unique) {
            return hitRepository.getStatsWithUniqueIp(startTime, endTime, urisList);
        }
        return hitRepository.getStats(startTime, endTime, urisList);
    }
}
