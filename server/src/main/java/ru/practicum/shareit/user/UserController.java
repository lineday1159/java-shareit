package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("GET request to get userList");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.info("GET request to get user by id - {}", userId);
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("POST request to add user");
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable long userId) {
        log.info("PATCH request to update user by id - {}", userId);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("DELETE request to delete user by id - {}", userId);
        userService.delete(userId);
    }
}
