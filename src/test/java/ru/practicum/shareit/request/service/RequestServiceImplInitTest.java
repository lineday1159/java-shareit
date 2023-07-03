package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Date;
import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplInitTest {

    private final RequestService requestService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Test
    void getItemsWithPagination() {
        User user1 = new User(null, "User1", "User1@gmail.com");
        userRepository.save(user1);
        User user2 = new User(null, "User2", "User2@gmail.com");
        userRepository.save(user2);
        ItemRequest itemRequest1 = new ItemRequest(null, "itemRequest1", user1, new Date());
        requestRepository.save(itemRequest1);
        ItemRequest itemRequest2 = new ItemRequest(null, "itemRequest2", user1, new Date());
        requestRepository.save(itemRequest2);
        ItemRequest itemRequest3 = new ItemRequest(null, "itemRequest3", user2, new Date());
        requestRepository.save(itemRequest3);
        Item item = new Item(null, "item1", user1, itemRequest1, "description", true);
        itemRepository.save(item);

        List<ItemRequestWithItemDto> itemRequestWithItemDtos = requestService.getItemsWithPagination(user2.getId(), 0, 20);
        Assertions.assertEquals(2, itemRequestWithItemDtos.size());
        Assertions.assertEquals(1, itemRequestWithItemDtos.get(0).getItems().size());
    }
}