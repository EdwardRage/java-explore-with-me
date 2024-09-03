package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class StatController {
    private final StatService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatResponseDto create(@RequestBody StatRequestDto requestDto) {
        StatResponseDto responseDto = service.create(requestDto);
        log.info("Просмотр мероприятия {} добавлен в статистику", responseDto);
        return responseDto;
    }

    @GetMapping("/stats")
    public List<StatResponseDto> getStats(@RequestParam String start,
                                          @RequestParam String end,
                                          @RequestParam(required = false) String[] uris,
                                          @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получение статистики для ==> ");
        return service.get(start, end, uris, unique);
    }
}
