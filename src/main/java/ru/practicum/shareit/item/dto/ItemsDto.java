package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInItemDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private BookingInItemDto lastBooking;
    private BookingInItemDto nextBooking;
    private List<CommentDto> comments;

}
