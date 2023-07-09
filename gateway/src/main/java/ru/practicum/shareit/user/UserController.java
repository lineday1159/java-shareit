package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class UserController {
    @Autowired
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("GET request to get userList");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("GET request to get user by id - {}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("POST request to add user");
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto,
                                         @PathVariable long userId) {
        log.info("PATCH request to update user by id - {}", userId);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("DELETE request to delete user by id - {}", userId);
        userClient.deleteUser(userId);
    }
}
