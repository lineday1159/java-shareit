package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;


    @Test
    void getUsers() throws Exception {
        UserDto userDto = new UserDto(1L, "test", "test@gmail.com");

        when(userService.getUsers())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));

    }

    @Test
    void getUser() throws Exception {
        UserDto userDto = new UserDto(1L, "test", "test@gmail.com");

        when(userService.getUser(Mockito.anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

    }

    @Test
    void getUserWithExc() throws Exception {
        UserDto userDto = new UserDto(1L, "test", "test@gmail.com");

        when(userService.getUser(Mockito.anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void create() throws Exception {
        UserDto userDto = new UserDto(1L, "test", "test@gmail.com");
        User user = new User(null, "test", "test@gmail.com");

        when(userService.create(Mockito.any(User.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

    }

    @Test
    void createWithNullEmail() throws Exception {
        UserDto userDto = new UserDto(1L, "test", null);
        User user = new User(null, "test", null);

        when(userService.create(Mockito.any(User.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void update() throws Exception {
        UserDto userDto = new UserDto(1L, "test", "test@gmail.com");

        when(userService.update(Mockito.any(UserDto.class), Mockito.anyLong()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateWithExc() throws Exception {
        UserDto userDto = new UserDto(1L, "test", "test@gmail.com");

        when(userService.update(userDto, 1L))
                .thenThrow(new NotFoundException("User not found"));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser() throws Exception {
        UserDto userDto = new UserDto(1L, "test", "test@gmail.com");

        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}