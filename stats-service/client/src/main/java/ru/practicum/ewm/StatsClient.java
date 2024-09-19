package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;


@Service
public class StatsClient extends BaseClient {

    //private final RestTemplate restTemplate;
    private static final String API_PREFIX = "/hit";

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl /*+ API_PREFIX*/))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }


    public ResponseEntity<Object> addStats(String app, String uri, String ip, String timestamp) {
        return post("/hit", mapToEndpointHitRequestDto(app, uri, ip, timestamp));
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return get("/stats?start={start}&end={end}&uris={uris}?unique={unique}", parameters);
    }

    public ResponseEntity<StatResponseDto> addStats2(String app, String uri, String ip, String timestamp) {
        return post2("/hit", mapToEndpointHitRequestDto(app, uri, ip, timestamp));
    }

    public ResponseEntity<StatResponseDto> getStats2(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return get2("/stats?start={start}&end={end}&uris={uris}?unique={unique}", parameters);
    }

    /*Метод, который собирает app, uri, ip в полноценную модель данных для сервера*/
    private StatRequestDto mapToEndpointHitRequestDto(String app, String uri, String ip, String timestamp) {
        StatRequestDto requestDto = new StatRequestDto();
        requestDto.setApp(app);
        requestDto.setIp(ip);
        requestDto.setUri(uri);
        requestDto.setTimestamp(timestamp);
        return requestDto;
    }
}