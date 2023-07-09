package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;

import java.text.ParseException;
import java.util.List;

public interface BookingService {
    BookingDto findById(Long id, Long userId);

    List<BookingDto> findByState(String status, Long userId, int from, int size);

    List<BookingDto> findByStateAndOwner(String status, Long userId, int from, int size);

    BookingDto save(BookingReqDto bookingDto, Long userId) throws ParseException;

    BookingDto updateState(Long bookingId, Boolean approved, Long userId);
}
