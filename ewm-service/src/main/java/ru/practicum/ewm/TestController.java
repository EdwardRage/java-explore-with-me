package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class TestController {
    private final ClientService statsClient;

    /*@GetMapping("/events/{eventsId}")
    public String getEventsById(@PathVariable long eventsId) {
        statsClient.addStats("main", "/events/" + eventsId, "192.168.1.1", LocalDateTime.now());
        return "работает";
    }*/
}
