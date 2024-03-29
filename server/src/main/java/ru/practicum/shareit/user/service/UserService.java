package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto getUser(Long userId);

    void delete(Long userId);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long userId);
}
