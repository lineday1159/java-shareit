package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    @Test
    void getItems() throws Exception {
        ItemsDto itemsDto = new ItemsDto(1L, "test", "test@gmail.com", true, null, null, null, null);

        when(itemService.getItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemsDto));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemsDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemsDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemsDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemsDto.getAvailable())));
    }

    @Test
    void getItem() throws Exception {
        ItemsDto itemsDto = new ItemsDto(1L, "test", "test@gmail.com", true, null, null, null, null);

        when(itemService.getItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemsDto);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemsDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemsDto.getName())))
                .andExpect(jsonPath("$.description", is(itemsDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemsDto.getAvailable())));
    }

    @Test
    void getItemWithExc() throws Exception {
        when(itemService.getItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Item not found"));

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchItem() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test", "test@gmail.com", true, null, null);

        when(itemService.searchItems(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void create() throws Exception {
        ItemDto itemDto = new ItemDto(null, "test", "test@gmail.com", true, 1L, null);
        ItemDto newItemDto = new ItemDto(1L, "test", "test@gmail.com", true, 1L, null);

        when(itemService.create(Mockito.any(), Mockito.anyLong()))
                .thenReturn(newItemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(newItemDto.getName())))
                .andExpect(jsonPath("$.description", is(newItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(newItemDto.getAvailable())));
    }

    @Test
    void createWithExc() throws Exception {
        ItemDto itemDto = new ItemDto(null, "test", "test@gmail.com", true, 1L, null);

        when(itemService.create(Mockito.any(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void update() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test", "test@gmail.com", true, 1L, null);
        ItemDto newItemDto = new ItemDto(1L, "testNew", "testNew@gmail.com", true, 1L, null);

        when(itemService.update(Mockito.any(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(newItemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(newItemDto.getName())))
                .andExpect(jsonPath("$.description", is(newItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(newItemDto.getAvailable())));
    }

    @Test
    void updateWithExc() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test", "test@gmail.com", true, 1L, null);

        when(itemService.update(Mockito.any(), Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Item not found"));

        mvc.perform(patch("/items/991")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = new CommentDto(null, "test", "testName", new Date());
        CommentDto newCommentDto = new CommentDto(1L, "test", "testName", new Date());

        when(itemService.createComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(newCommentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(newCommentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(newCommentDto.getAuthorName())));
    }

    @Test
    void createCommentWithExc() throws Exception {
        CommentDto commentDto = new CommentDto(null, "test", "testName", new Date());

        when(itemService.createComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenThrow(new NotFoundException("Item not found"));

        mvc.perform(post("/items/12313/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }
}