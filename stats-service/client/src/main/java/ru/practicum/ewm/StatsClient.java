package ru.practicum.ewm;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsClient {
    private final RestTemplate restTemplate;
    private final String statsServiceUrl;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String statsServiceUrl) {
        this.statsServiceUrl = statsServiceUrl;
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        restTemplate = new RestTemplate(factory);
    }

    public void addStats(HttpServletRequest request) {
        StatRequestDto requestDto = new StatRequestDto();
        requestDto.setIp(request.getRemoteAddr());
        requestDto.setApp("ewm-main-service");
        requestDto.setUri(request.getRequestURI());
        requestDto.setTimestamp(LocalDateTime.now().toString());

        restTemplate.postForObject(statsServiceUrl + "/hit", requestDto, String.class);
    }

    public List<StatResponseDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        String statsServiceUrlFull = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .toUriString();
        ResponseEntity<List<StatResponseDto>> response = restTemplate.exchange(statsServiceUrl + statsServiceUrlFull,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<StatResponseDto>>() {
                });
        return response.getBody();
    }
}
