package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private final Map<Long, Item> itemMap = new HashMap<>();
    private final ItemMapper itemMapper = new ItemMapper();
    private final UserRepository userRepository;

    private Long currentId = Long.valueOf(0);

    @Override
    public List<Item> findByUserId(long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public List<Item> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemMap.values().stream()
                    .filter(x -> x.isAvailable() && x.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Item findByItemIdAndUserId(long itemId, long userId) {
        if (items.containsKey(userId)) {
            for (Item item : items.get(userId)) {
                if (item.getId() == itemId) {
                    return item;
                }
            }
            throw new NotFoundException(String.format("item c таким id-%d не существует", itemId));
        } else {
            throw new NotFoundException(String.format("item у пользователя с таким id-%d не существует", userId));
        }
    }

    @Override
    public Item findByItemId(long itemId) {
        if (itemMap.containsKey(itemId)) {
            return itemMap.get(itemId);
        } else {
            throw new NotFoundException(String.format("item c таким id-%d не существует", itemId));
        }
    }

    @Override
    public Item create(ItemDto itemDto, Long userId) {
        User user = userRepository.get(userId); //проверка на наличие пользователя
        Item item = ItemMapper.toItem(itemDto, getId(), null, null);
        items.compute(userId, (creatorId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(ItemDto itemDto, Long userId, Long itemId) {
        Item item = findByItemIdAndUserId(itemId, userId);
        if (item != null) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
        }
        return item;
    }

    private long getId() {
        return ++currentId;
    }
}
