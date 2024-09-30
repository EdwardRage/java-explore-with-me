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
    @ResponseBody
    public List<StatResponseDto> getStats(@RequestParam(required = false) String start,
                                          @RequestParam(required = false) String end,
                                          @RequestParam(required = false) String[] uris,
                                          @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получение статистики для ==> {}", uris);
        /*StatsParam statsParam = StatsParam.builder()
                .start(start)
                .end(end)
                .uris(uris)
                .unique(unique)
                .build();*/
        return service.getStat(start, end, uris, unique);
        //return service.get(start, end, uris, unique);
    }
}
