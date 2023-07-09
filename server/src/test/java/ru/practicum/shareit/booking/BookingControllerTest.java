package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    @Test
    void getBooking() throws Exception {
        UserDto user1 = new UserDto(1L, "User1", "User1@gmail.com");
        ItemDto item = new ItemDto(1L, "item1", "description", true, null, null);
        BookingDto bookingDto = new BookingDto(1L, "2024-01-07T00:00:00", "2025-01-07T00:00:00", item, user1, BookingStatus.REJECTED);

        when(bookingService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    void getBookingWithExc() throws Exception {

        when(bookingService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Only booker or owner has an access to booking"));

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingListByState() throws Exception {
        UserDto user1 = new UserDto(1L, "User1", "User1@gmail.com");
        ItemDto item = new ItemDto(1L, "item1", "description", true, null, null);
        BookingDto bookingDto = new BookingDto(1L, "2024-01-07T00:00:00", "2025-01-07T00:00:00", item, user1, BookingStatus.REJECTED);

        when(bookingService.findByState(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    void getBookingListByStateAndOwner() throws Exception {
        UserDto user1 = new UserDto(1L, "User1", "User1@gmail.com");
        ItemDto item = new ItemDto(1L, "item1", "description", true, null, null);
        BookingDto bookingDto = new BookingDto(1L, "2024-01-07T00:00:00", "2025-01-07T00:00:00", item, user1, BookingStatus.REJECTED);

        when(bookingService.findByStateAndOwner(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    void postBooking() throws Exception {
        UserDto user1 = new UserDto(1L, "User1", "User1@gmail.com");
        ItemDto item = new ItemDto(1L, "item1", "description", true, null, null);
        BookingDto bookingDto = new BookingDto(1L, "2024-01-07T00:00:00", "2025-01-07T00:00:00", item, user1, BookingStatus.REJECTED);

        when(bookingService.save(Mockito.any(), Mockito.anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    void patchBooking() throws Exception {
        UserDto user1 = new UserDto(1L, "User1", "User1@gmail.com");
        ItemDto item = new ItemDto(1L, "item1", "description", true, null, null);
        BookingDto bookingDto = new BookingDto(1L, "2024-01-07T00:00:00", "2025-01-07T00:00:00", item, user1, BookingStatus.REJECTED);

        when(bookingService.updateState(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }
}