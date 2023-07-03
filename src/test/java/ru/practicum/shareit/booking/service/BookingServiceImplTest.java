package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    BookingRepository mockBookingRepository;
    @Mock
    UserRepository mockUserRepository;
    @Mock
    ItemRepository mockItemRepository;
    BookingService bookingService;
    User user1;
    User user2;
    Item item;
    Booking booking;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");


    @BeforeEach
    void setUp() throws ParseException {
        bookingService = new BookingServiceImpl(mockBookingRepository, mockUserRepository, mockItemRepository);
        user1 = new User(1L, "user1", "user1@email.ru");
        user2 = new User(2L, "user2", "user2@email.ru");
        item = new Item(1L, "item1", user1, null, "description", true);
        booking = new Booking(1L, formatter.parse("07-01-2013"), formatter.parse("07-01-2014"), item, user2, BookingStatus.APPROVED);
    }

    @Test
    void findById() {
        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        when(mockUserRepository.findById(2L))
                .thenReturn(Optional.of(user2));

        when(mockUserRepository.findById(3L))
                .thenReturn(Optional.of(new User(3L, "user3", "user3@email.ru")));

        when(mockBookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.findById(1L, 1L);
        Assertions.assertEquals(booking.getId(), bookingDto.getId());
        Assertions.assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        Assertions.assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());

        bookingDto = bookingService.findById(1L, 2L);
        Assertions.assertEquals(booking.getId(), bookingDto.getId());
        Assertions.assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        Assertions.assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findById(1L, 3L));

        Assertions.assertEquals("Only booker or owner has an access to booking", exception.getMessage());
    }

    @Test
    void findByState() throws ParseException {
        Booking booking2 = new Booking(2L, formatter.parse("07-01-2015"), formatter.parse("07-01-2016"), item, user2, BookingStatus.REJECTED);
        Booking booking3 = new Booking(3L, formatter.parse("07-01-2020"), formatter.parse("07-01-2024"), item, user2, BookingStatus.APPROVED);
        Booking booking4 = new Booking(4L, formatter.parse("07-01-2025"), formatter.parse("07-01-2027"), item, user2, BookingStatus.APPROVED);

        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user2));

        when(mockBookingRepository.findByBookerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking, booking2, booking3, booking4)));

        when(mockBookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking3)));

        when(mockBookingRepository.findByBookerIdAndEndIsBefore(Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking3)));

        when(mockBookingRepository.findByBookerIdAndStartIsAfter(Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking3)));

        List<BookingDto> bookingDtos = bookingService.findByState("ALL", user2.getId(), 0, 20);
        Assertions.assertEquals(4, bookingDtos.size());

        bookingDtos = bookingService.findByState("CURRENT", user2.getId(), 0, 20);
        Assertions.assertEquals(1, bookingDtos.size());

        bookingDtos = bookingService.findByState("PAST", user2.getId(), 0, 20);
        Assertions.assertEquals(1, bookingDtos.size());

        bookingDtos = bookingService.findByState("FUTURE", user2.getId(), 0, 20);
        Assertions.assertEquals(1, bookingDtos.size());

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findByState("CURRENT", user2.getId(), -1, 20));

        Assertions.assertEquals("Not correct page parameters", exception.getMessage());

        exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findByState("CURR232ENT", user2.getId(), 0, 20));

        Assertions.assertEquals("Unknown state: CURR232ENT", exception.getMessage());
    }

    @Test
    void findByStateAndOwner() throws ParseException {
        Booking booking2 = new Booking(2L, formatter.parse("07-01-2015"), formatter.parse("07-01-2016"), item, user2, BookingStatus.REJECTED);
        Booking booking3 = new Booking(3L, formatter.parse("07-01-2020"), formatter.parse("07-01-2024"), item, user2, BookingStatus.APPROVED);
        Booking booking4 = new Booking(4L, formatter.parse("07-01-2025"), formatter.parse("07-01-2027"), item, user2, BookingStatus.APPROVED);

        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user1));

        when(mockBookingRepository.findByItemOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking, booking2, booking3, booking4)));

        when(mockBookingRepository.findByItemOwnerIdAndEndIsAfterAndStartIsBefore(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking3)));

        when(mockBookingRepository.findByItemOwnerIdAndEndIsBefore(Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking3)));

        when(mockBookingRepository.findByItemOwnerIdAndStartIsAfter(Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking3)));

        List<BookingDto> bookingDtos = bookingService.findByStateAndOwner("ALL", user1.getId(), 0, 20);
        Assertions.assertEquals(4, bookingDtos.size());

        bookingDtos = bookingService.findByStateAndOwner("CURRENT", user1.getId(), 0, 20);
        Assertions.assertEquals(1, bookingDtos.size());

        bookingDtos = bookingService.findByStateAndOwner("PAST", user1.getId(), 0, 20);
        Assertions.assertEquals(1, bookingDtos.size());

        bookingDtos = bookingService.findByStateAndOwner("FUTURE", user1.getId(), 0, 20);
        Assertions.assertEquals(1, bookingDtos.size());

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findByStateAndOwner("CURRENT", user1.getId(), -1, 20));

        Assertions.assertEquals("Not correct page parameters", exception.getMessage());

        exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findByStateAndOwner("CURR232ENT", user1.getId(), 0, 20));

        Assertions.assertEquals("Unknown state: CURR232ENT", exception.getMessage());
    }

    @Test
    void save() throws ParseException {
        Booking booking2 = new Booking(2L, formatter.parse("07-01-2015"), formatter.parse("07-01-2016"), item, user2, BookingStatus.WAITING);
        BookingReqDto bookingReqDto = new BookingReqDto("2015-01-07T00:00:00", "2016-01-07T00:00:00", item.getId());
        Item item1 = new Item(2L, "item1", user1, null, "description", false);

        when(mockUserRepository.findById(2L))
                .thenReturn(Optional.of(user2));

        when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(mockItemRepository.findById(2L))
                .thenReturn(Optional.of(item1));

        when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking2);

        DataErrorException exception = Assertions.assertThrows(
                DataErrorException.class,
                () -> bookingService.save(bookingReqDto, user2.getId()));

        Assertions.assertEquals("End/start date is before current date", exception.getMessage());

        BookingReqDto bookingReqDto1 = new BookingReqDto("2025-01-07T00:00:00", "2025-01-07T00:00:00", item.getId());

        exception = Assertions.assertThrows(
                DataErrorException.class,
                () -> bookingService.save(bookingReqDto1, user2.getId()));

        Assertions.assertEquals("End date is equal/before start date", exception.getMessage());

        BookingReqDto bookingReqDto2 = new BookingReqDto("2024-01-07T00:00:00", "2025-01-07T00:00:00", item1.getId());

        exception = Assertions.assertThrows(
                DataErrorException.class,
                () -> bookingService.save(bookingReqDto2, user2.getId()));

        Assertions.assertEquals("Item is not available", exception.getMessage());

        BookingReqDto bookingReqDto3 = new BookingReqDto("2024-01-07T00:00:00", "2025-01-07T00:00:00", item.getId());

        NotFoundException nFException = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.save(bookingReqDto3, user1.getId()));

        Assertions.assertEquals("owner can't book himself items", nFException.getMessage());

        BookingDto bookingDto = bookingService.save(bookingReqDto3, user2.getId());
        Assertions.assertEquals(item.getId(), bookingDto.getItem().getId());
    }

    @Test
    void updateState() throws ParseException {
        Booking booking2 = new Booking(2L, formatter.parse("07-01-2015"), formatter.parse("07-01-2016"), item, user2, BookingStatus.APPROVED);

        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        when(mockUserRepository.findById(2L))
                .thenReturn(Optional.of(user2));

        when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking2));

        when(mockBookingRepository.save(Mockito.any()))
                .thenReturn(booking2);

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateState(2L, true, user2.getId()));

        Assertions.assertEquals("Only owner can change booking status", exception.getMessage());

        DataErrorException dRException = Assertions.assertThrows(
                DataErrorException.class,
                () -> bookingService.updateState(2L, true, user1.getId()));

        Assertions.assertEquals("Only waiting status might be changed", dRException.getMessage());

        booking2.setStatus(BookingStatus.WAITING);

        BookingDto bookingDto = bookingService.updateState(2L, true, user1.getId());

        Assertions.assertNotNull(bookingDto);
    }
}