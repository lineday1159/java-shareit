package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository mockUserRepository;

    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Test
    void getUserNotFound() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUser(1L));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateUserNotFound() {

        UserDto userDto = new UserDto(1L, "test", "test@gmail.com");

        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.update(userDto, 1L));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateUserWithNullName() {

        UserDto userDto = new UserDto(1L, null, "testNew@gmail.com");
        User userInBase = new User(1L, "userName", "test@gmail.com");
        User userToSave = new User(1L, "userName", "testNew@gmail.com");

        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userInBase));
        Mockito
                .when(mockUserRepository.save(Mockito.any(User.class)))
                .thenReturn(userToSave);

        userService.update(userDto, 1L);

        Mockito.verify(mockUserRepository, Mockito.times(1))
                .save(userToSave);
    }

    @Test
    void updateUserWithNullEmail() {

        UserDto userDto = new UserDto(1L, "newUserName", null);
        User userInBase = new User(1L, "userName", "test@gmail.com");
        User userToSave = new User(1L, "newUserName", "test@gmail.com");

        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userInBase));
        Mockito
                .when(mockUserRepository.save(Mockito.any(User.class)))
                .thenReturn(userToSave);

        userService.update(userDto, 1L);

        Mockito.verify(mockUserRepository, Mockito.times(1))
                .save(userToSave);
    }

}