package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUser(Long userId);

    void delete(Long userId);

    User create(User user);

    User update(User user, Long userId);
}
