package ru.practicum.shareit.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j

public class BookingMapper {

    private static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
    private static final DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    public static BookingDto bookingToBookingDto(Booking booking) {
        UserDto userDto = UserMapper.toUserDto(booking.getBooker());
        ItemDto itemDto = ItemMapper.toItemDto(booking.getItem(), null);
        return new BookingDto(booking.getId(), dateFormatter.format(booking.getStart()), dateFormatter.format(booking.getEnd()), itemDto, userDto, booking.getStatus());
    }

    public static BookingInItemDto bookingToBookingInItemDto(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            return new BookingInItemDto(booking.getId(), booking.getBooker().getId());
        }
    }

    public static List<BookingDto> bookingToBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(bookingToBookingDto(booking));
        }
        return dtos;
    }

    public static Booking bookingReqDtoToBooking(BookingReqDto bookingDto, Item item, User user, BookingStatus status) throws ParseException {
        return new Booking(null, dateFormatter.parse(bookingDto.getStart()), dateFormatter.parse(bookingDto.getEnd()), item, user, status);
    }

    public static Booking bookingDtoToBooking(BookingDto bookingDto, Item item, User user) throws ParseException {
        return new Booking(null, dateFormatter.parse(bookingDto.getStart()), dateFormatter.parse(bookingDto.getEnd()), item, user, bookingDto.getStatus());
    }

}
