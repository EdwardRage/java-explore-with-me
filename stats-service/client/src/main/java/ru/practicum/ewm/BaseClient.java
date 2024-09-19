package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@AllArgsConstructor
public class BaseClient {
    private final RestTemplate restTemplate;

    protected <T> ResponseEntity<Object> post(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, body);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, body);
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    /*protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }*/

    //дописать метод get
    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body);

        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = restTemplate.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = restTemplate.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statsServerResponse);
    }

    private <T> ResponseEntity<StatResponseDto> makeAndSendRequest2(HttpMethod method, String path, @Nullable Map<String, Object> parameters, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body);

        ResponseEntity<StatResponseDto> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = restTemplate.exchange(path, method, requestEntity, StatResponseDto.class, parameters);
            } else {
                statsServerResponse = restTemplate.exchange(path, method, requestEntity, StatResponseDto.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
        return prepareGatewayResponse2(statsServerResponse);
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    private static ResponseEntity<StatResponseDto> prepareGatewayResponse2(ResponseEntity<StatResponseDto> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    protected <T> ResponseEntity<StatResponseDto> post2(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest2(HttpMethod.POST, path, parameters, body);
    }

    protected <T> ResponseEntity<StatResponseDto> post2(String path, T body) {
        return post2(path, null, body);
    }

    protected ResponseEntity<StatResponseDto> get2(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest2(HttpMethod.GET, path, parameters, null);
    }
}

