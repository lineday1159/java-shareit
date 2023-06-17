package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User save(User user);

    User update(User user, Long userId);

    User get(Long userId);

    void delete(Long userId);
}
