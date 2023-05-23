package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<Item> getItems(Long userId) {
        return itemRepository.findByUserId(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.searchItem(text);
    }

    @Override
    public Item getItem(Long itemId, Long userId) {
        return itemRepository.findByItemId(itemId);
    }

    @Override
    public Item create(ItemDto item, Long userId) {
        return itemRepository.create(item, userId);
    }

    @Override
    public Item update(ItemDto item, Long userId, Long itemId) {
        return itemRepository.update(item, userId, itemId);
    }
}
