package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository {

    List<Item> findByUserId(long userId);

    List<Item> searchItem(String text);

    Item findByItemId(long itemId);

    Item findByItemIdAndUserId(long itemId, long userId);

    Item create(Item item, Long userId);

    Item update(Item item, Long userId, Long itemId);

}
