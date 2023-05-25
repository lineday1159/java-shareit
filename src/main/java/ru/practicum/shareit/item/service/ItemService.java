package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getItems(Long userId);

    List<ItemDto> searchItems(String text);

    ItemDto getItem(Long itemId, Long userId);

    ItemDto create(ItemDto item, Long userId);

    ItemDto update(ItemDto item, Long userId, Long itemId);
}
