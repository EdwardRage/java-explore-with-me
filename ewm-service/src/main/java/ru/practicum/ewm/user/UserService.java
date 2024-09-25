package ru.practicum.ewm.user;

import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {

    User create(UserDto userDto);

    List<User> get(List<Long> ids, Integer from, Integer size);

    void delete(long userId);

    User getById(long userId);
}
