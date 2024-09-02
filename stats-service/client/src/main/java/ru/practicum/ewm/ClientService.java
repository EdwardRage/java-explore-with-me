package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;


@Service
public class ClientService extends BaseClient {
    private static final String API_PREFIX = "/hit";

    @Autowired
    public ClientService(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addStats(String app, String uri, String ip, String timestamp) {
        return post("", mapToEndpointHitRequestDto(app, uri, ip, timestamp));
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
