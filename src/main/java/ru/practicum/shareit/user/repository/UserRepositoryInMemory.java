package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.Exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class UserRepositoryInMemory implements UserRepository {
    private final HashMap<Long, User> userMapId = new HashMap<>();
    private final HashMap<Long, String> userMapEmail = new HashMap<>();
    private Long currentId = Long.valueOf(0);

    @Override
    public List<User> findAll() {
        return userMapId.values().stream().collect(Collectors.toList());
    }

    @Override
    public User save(User user) {
        if (userMapEmail.containsValue(user.getEmail())) {
            throw new ValidationException(String.format("пользователь с таким email-%s уже существует", user.getEmail()));
        } else {
            Long userId = getId();
            user.setId(userId);
            userMapId.put(userId, user);
            userMapEmail.put(userId, user.getEmail());
            return user;
        }
    }

    @Override
    public User update(User user, Long userId) {
        if (!userMapId.containsKey(userId)) {
            throw new NotFoundException(String.format("пользователь с таким id-%d не существует", userId));
        } else {
            User currentUser = userMapId.get(userId);
            if (user.getEmail() != null) {
                if (!userMapEmail.get(userId).equals(user.getEmail()) && userMapEmail.containsValue(user.getEmail())) {
                    throw new ValidationException(String.format("пользователь с таким email-%s уже существует", user.getEmail()));
                } else {
                    currentUser.setEmail(user.getEmail());
                    userMapEmail.put(userId, user.getEmail());
                }
            }
            if (user.getName() != null) {
                currentUser.setName(user.getName());
            }
            return currentUser;
        }
    }

    @Override
    public User get(Long userId) {
        if (!userMapId.containsKey(userId)) {
            throw new NotFoundException(String.format("пользователь с таким id-%d не существует", userId));
        } else {
            return userMapId.get(userId);
        }
    }

    @Override
    public void delete(Long userId) {
        userMapId.remove(userId);
        userMapEmail.remove(userId);
    }

    private Long getId() {
        return ++currentId;
    }
}
