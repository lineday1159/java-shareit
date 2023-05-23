package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
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
}
