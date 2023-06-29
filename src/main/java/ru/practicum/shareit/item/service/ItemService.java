package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsDto;

import java.util.List;

public interface ItemService {

    List<ItemsDto> getItems(Long userId);

    List<ItemDto> searchItems(String text);

    ItemsDto getItem(Long itemId, Long userId);

    ItemDto create(ItemDto item, Long userId);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);

    ItemDto update(ItemDto item, Long userId, Long itemId);
}
