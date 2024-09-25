package ru.practicum.ewm.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody UserDto userDto) {
        User user = userService.create(userDto);
        log.info("Добавлен новый пользователь ==> {}", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId) {
        userService.delete(userId);
        log.info("Пользователь с id {} удален", userId);
    }

    @GetMapping
    public List<User> get(@RequestParam(required = false) List<Long> ids,
                          @RequestParam(defaultValue = "0") Integer from,
                          @RequestParam(defaultValue = "10") Integer size) {
        List<User> users = userService.get(ids, from, size);
        log.info("Получение информации о пользователях {}", users);
        return users;
    }
}
