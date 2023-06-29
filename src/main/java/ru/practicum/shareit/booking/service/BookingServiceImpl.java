package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;

    private final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
    private final DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    @Transactional(readOnly = true)
    @Override
    public BookingDto findById(Long id, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only booker or owner has an access to booking");
        }
        return BookingMapper.bookingToBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findByState(String state, Long userId) {
        List<Booking> bookings;
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Sort sort = Sort.by("end").descending();
        log.info(new Date().toString());
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerId(userId, sort);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(userId, new Date(), new Date(), sort);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, new Date(), sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, new Date(), sort);
                break;
            default:
                try {
                    BookingStatus status = BookingStatus.valueOf(state);
                    bookings = bookingRepository.findByStatusAndBookerId(status, userId, sort);
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Unknown state: " + state);
                }
                break;
        }
        log.info(bookings.toString());
        return BookingMapper.bookingToBookingDto(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findByStateAndOwner(String state, Long userId) {
        List<Booking> bookings;
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Sort sort = Sort.by("end").descending();
        log.info(new Date().toString());
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerId(userId, sort);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndEndIsAfterAndStartIsBefore(userId, new Date(), new Date(), sort);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, new Date(), sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, new Date(), sort);
                break;
            default:
                try {
                    BookingStatus status = BookingStatus.valueOf(state);
                    bookings = bookingRepository.findByStatusAndItemOwnerId(status, userId, sort);
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Unknown state: " + state);
                }
                break;
        }
        return BookingMapper.bookingToBookingDto(bookings);
    }

    @Transactional
    @Override
    public BookingDto save(BookingReqDto bookingDto, Long userId) throws ParseException {
        Date dateStart = dateFormatter.parse(bookingDto.getStart());
        Date dateEnd = dateFormatter.parse(bookingDto.getEnd());
        if (dateStart.before(new Date()) || dateEnd.before(new Date())) {
            throw new DataErrorException("End/start date is before current date");
        } else if (dateEnd.equals(dateStart) || dateEnd.before(dateStart)) {
            throw new DataErrorException("End date is equal/before start date");
        }
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getIsAvailable()) {
            throw new DataErrorException("Item is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("owner can't book himself items");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Booking booking = BookingMapper.bookingReqDtoToBooking(bookingDto, item, user, BookingStatus.WAITING);
        return BookingMapper.bookingToBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto updateState(Long bookingId, Boolean approved, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            throw new NotFoundException("Only owner can change booking status");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new DataErrorException("Only waiting status might be changed");
        }
        booking.setStatus(bookingStatus);
        return BookingMapper.bookingToBookingDto(bookingRepository.save(booking));
    }

}
