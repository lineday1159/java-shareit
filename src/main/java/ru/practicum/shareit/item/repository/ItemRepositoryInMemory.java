package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private final Map<Long, Item> itemMap = new HashMap<>();
    private final UserRepository userRepository;

    private Long currentId = 0L;

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
                    .filter(x -> x.getAvailable() && x.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase()))
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
    public Item create(Item item, Long userId) {
        User user = userRepository.get(userId); //проверка на наличие пользователя
        item.setId(getId());
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
    public Item update(Item itemNew, Long userId, Long itemId) {
        Item itemCurrent = findByItemIdAndUserId(itemId, userId);
        if (itemCurrent != null) {
            if (itemNew.getName() != null) {
                itemCurrent.setName(itemNew.getName());
            }
            if (itemNew.getDescription() != null) {
                itemCurrent.setDescription(itemNew.getDescription());
            }
            if (itemNew.getAvailable() != null) {
                itemCurrent.setAvailable(itemNew.getAvailable());
            }
        }
        return itemCurrent;
    }

    private long getId() {
        return ++currentId;
    }
}
