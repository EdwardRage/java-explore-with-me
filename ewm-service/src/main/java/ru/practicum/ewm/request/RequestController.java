package ru.practicum.ewm.request;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable @NotNull long userId,
                                          @RequestParam long eventId) {
        ParticipationRequestDto requestDto = requestService.create(userId, eventId);
        log.info("Заявка на участие в меропритии добавлена");
        return requestDto;
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> get(@PathVariable @NotNull long userId) {
        List<ParticipationRequestDto> requestDtoList = requestService.get(userId);
        log.info("Список заявок текущего пользователя");
        return requestDtoList;
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable @NotNull long userId,
                                          @PathVariable @NotNull long requestId) {
        ParticipationRequestDto requestDto = requestService.cansel(userId, requestId);
        log.info("Заявка с id {} отменена", requestId);
        return requestDto;
    }
}
