package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImpInitTest {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    @Test
    void getItems() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        User user1 = new User(null, "User1", "User1@gmail.com");
        userRepository.save(user1);

        User user2 = new User(null, "User2", "User2@gmail.com");
        userRepository.save(user2);

        User user3 = new User(null, "User3", "User3@gmail.com");
        userRepository.save(user3);

        ItemDto itemDto1 = new ItemDto(null, "item1", "description1", true, null, null);
        ItemDto itemDto2 = new ItemDto(null, "item2", "description2", true, null, null);
        itemService.create(itemDto1, user1.getId());
        itemService.create(itemDto2, user1.getId());

        Booking lastBooking = new Booking(null, formatter.parse("07-01-2013"), formatter.parse("07-02-2013"), itemRepository.findById(1L).get(), user2, BookingStatus.APPROVED);
        Booking nextBooking = new Booking(null, formatter.parse("07-01-2050"), formatter.parse("07-01-2051"), itemRepository.findById(1L).get(), user3, BookingStatus.APPROVED);

        bookingRepository.save(lastBooking);
        bookingRepository.save(nextBooking);

        List<ItemsDto> itemsDtos = itemService.getItems(user1.getId(), 0, 20);
        assertThat(itemsDtos.size(), equalTo(2));
        assertThat(itemsDtos.get(0).getName(), equalTo(itemDto1.getName()));
        assertThat(itemsDtos.get(0).getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(itemsDtos.get(0).getLastBooking().getBookerId(), equalTo(user2.getId()));
        assertThat(itemsDtos.get(0).getNextBooking().getBookerId(), equalTo(user3.getId()));
        assertThat(itemsDtos.get(1).getName(), equalTo(itemDto2.getName()));
        assertThat(itemsDtos.get(1).getDescription(), equalTo(itemDto2.getDescription()));
    }
}