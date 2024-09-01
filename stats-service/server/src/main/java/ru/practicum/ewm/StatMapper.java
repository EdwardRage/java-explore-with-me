package ru.practicum.ewm;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StatMapper {

    public StatModel mapToEndpointHit(StatRequestDto hitDto, LocalDateTime dateTime) {
        StatModel statModel = new StatModel();
        statModel.setApp(hitDto.getApp());
        statModel.setUri(hitDto.getUri());
        statModel.setIp(hitDto.getIp());
        statModel.setTimestamp(dateTime);
        return statModel;
    }

    public StatResponseDto mapToEndpointHitResponse(StatModel statModel) {
        StatResponseDto hitDto = new StatResponseDto();
        hitDto.setApp(statModel.getApp());
        hitDto.setUri(statModel.getUri());
        //hitDto.setHits(hits);
        return hitDto;
    }
}
