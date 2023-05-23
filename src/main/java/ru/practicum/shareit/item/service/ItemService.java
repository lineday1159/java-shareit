package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> getItems(Long userId);

    List<Item> searchItems(String text);

    Item getItem(Long itemId, Long userId);

    Item create(ItemDto item, Long userId);

    Item update(ItemDto item, Long userId, Long itemId);
}
