package ru.practicum.shareit.request.model;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toRequestItemDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester() != null ? itemRequest.getRequester().getId() : null,
                itemRequest.getCreated()
        );
    }

    public static ItemRequestWithItemDto toRequestItemWithItemDto(ItemRequest itemRequest, List<Item> itemList) {
        List<ItemDto> itemInRequests = new ArrayList<>();
        for (Item item : itemList) {
            itemInRequests.add(new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable(), item.getRequest().getId(), null));
        }
        return new ItemRequestWithItemDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemInRequests
        );
    }

    public static ItemRequest toRequestItem(ItemRequestDto itemRequestDto, Long id, User requester, Date created) {
        return new ItemRequest(id, itemRequestDto.getDescription(), requester, created);
    }

}
