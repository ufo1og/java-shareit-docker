package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto read(long id);

    UserDto update(long id, UserDto userDto);

    UserDto delete(long id);

    List<UserDto> readAll();
}
