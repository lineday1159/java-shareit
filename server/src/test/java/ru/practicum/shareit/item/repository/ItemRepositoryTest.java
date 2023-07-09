package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;


@DataJpaTest
@Transactional
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void search() {
        User user1 = new User(null, "User1", "User1@gmail.com");
        user1 = userRepository.save(user1);

        Item item1 = new Item(null, "item1", user1, null, "test", true);
        itemRepository.save(item1);
        Item item2 = new Item(null, "item2", user1, null, " TEST assda", true);
        itemRepository.save(item2);
        Item item3 = new Item(null, "item3", user1, null, "description1", false);
        itemRepository.save(item3);
        Item item4 = new Item(null, "item4", user1, null, "description1", true);
        itemRepository.save(item4);
        Item item5 = new Item(null, "item5", user1, null, "asddTeST asdad", true);
        itemRepository.save(item5);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
        List<Item> itemList = itemRepository.search("test", pageable).toList();
        Assertions.assertEquals(3, itemList.size());
        Assertions.assertEquals(item1.getDescription(), itemList.get(0).getDescription());
        Assertions.assertEquals(item2.getDescription(), itemList.get(1).getDescription());
        Assertions.assertEquals(item5.getDescription(), itemList.get(2).getDescription());
    }
}