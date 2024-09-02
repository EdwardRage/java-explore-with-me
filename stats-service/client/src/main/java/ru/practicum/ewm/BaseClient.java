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
//@Service
public class BaseClient {
    private final RestTemplate restTemplate;


    protected ResponseEntity<Object> post(String path, StatRequestDto body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

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
}

