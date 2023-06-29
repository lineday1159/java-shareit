package ru.practicum.shareit.mapper;

import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item, List<Comment> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                comments
        );
    }

    public static Item toItem(ItemDto itemDto, Long id, User owner, ItemRequest itemRequest) {
        return new Item(
                id,
                itemDto.getName(),
                owner,
                itemRequest,
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    public static ItemsDto toItemsDto(Item item, List<CommentDto> comments, BookingInItemDto lastBooking, BookingInItemDto nextBooking) {
        return new ItemsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking,
                comments
        );
    }
}
