package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntTest {

    private final UserService userService;
    private final UserRepository userRepository;

    @Test
    void getUsers() {
        User user1 = new User(null, "User1", "User1@gmail.com");
        User user2 = new User(null, "User2", "User2@gmail.com");

        userService.create(user1);
        userService.create(user2);

        List<UserDto> usersInBase = userService.getUsers();
        assertThat(usersInBase.size(), equalTo(2));
        assertThat(usersInBase.get(0).getName(), equalTo(user1.getName()));
        assertThat(usersInBase.get(0).getEmail(), equalTo(user1.getEmail()));
        assertThat(usersInBase.get(1).getName(), equalTo(user2.getName()));
        assertThat(usersInBase.get(1).getEmail(), equalTo(user2.getEmail()));
    }

}