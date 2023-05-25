package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper = new ItemMapper();

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemRepository.findByUserId(userId)
                .stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchItem(text)
                .stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        return ItemMapper.toItemDto(itemRepository.findByItemId(itemId));
    }

    @Override
    public ItemDto create(ItemDto item, Long userId) {
        return ItemMapper.toItemDto(itemRepository.create(ItemMapper.toItem(item, null, null, null), userId));
    }

    @Override
    public ItemDto update(ItemDto item, Long userId, Long itemId) {
        return ItemMapper.toItemDto(itemRepository.update(ItemMapper.toItem(item, itemId, null, null), userId, itemId));
    }
}
