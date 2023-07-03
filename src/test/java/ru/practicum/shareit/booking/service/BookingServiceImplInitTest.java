package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplInitTest {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    @Test
    void findByState() throws ParseException {
        User user1 = new User(null, "User1", "User1@gmail.com");
        userRepository.save(user1);
        Item item = new Item(null, "item1", user1, null, "description", true);
        itemRepository.save(item);
        User user2 = new User(null, "User2", "User2@gmail.com");
        userRepository.save(user2);

        Booking booking1 = new Booking(null, formatter.parse("07-01-2015"), formatter.parse("07-01-2016"), item, user2, BookingStatus.REJECTED);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(null, formatter.parse("07-01-2020"), formatter.parse("07-01-2024"), item, user2, BookingStatus.APPROVED);
        bookingRepository.save(booking2);
        Booking booking3 = new Booking(null, formatter.parse("07-01-2025"), formatter.parse("07-01-2027"), item, user2, BookingStatus.APPROVED);
        bookingRepository.save(booking3);

        List<BookingDto> bookingDtos = bookingService.findByState("ALL", user2.getId(), 0, 20);
        Assertions.assertEquals(3, bookingDtos.size());

        List<BookingDto> bookingDtos1 = bookingService.findByState("PAST", user2.getId(), 0, 20);
        Assertions.assertEquals(1, bookingDtos1.size(), "Check PAST size");
        Assertions.assertEquals(1, bookingDtos1.get(0).getId(), "Check PAST booking id");

        List<BookingDto> bookingDtos2 = bookingService.findByState("FUTURE", user2.getId(), 0, 20);
        Assertions.assertEquals(1, bookingDtos2.size(), "Check FUTURE size");
        Assertions.assertEquals(3, bookingDtos2.get(0).getId(), "Check FUTURE booking id");

        List<BookingDto> bookingDtos3 = bookingService.findByState("CURRENT", user2.getId(), 0, 20);
        Assertions.assertEquals(1, bookingDtos3.size(), "Check CURRENT size");
        Assertions.assertEquals(2, bookingDtos3.get(0).getId(), "Check CURRENT booking id");
    }
}