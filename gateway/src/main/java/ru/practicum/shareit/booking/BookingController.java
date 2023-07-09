package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.DataErrorException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    @Autowired
    private final BookingClient bookingClient;
    private final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
    private final DateFormat dateFormatter = new SimpleDateFormat(dateFormat);


    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) throws ParseException {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        Date dateStart = dateFormatter.parse(requestDto.getStart());
        Date dateEnd = dateFormatter.parse(requestDto.getEnd());
        if (dateStart.before(new Date()) || dateEnd.before(new Date())) {
            throw new DataErrorException("End/start date is before current date");
        } else if (dateEnd.equals(dateStart) || dateEnd.before(dateStart)) {
            throw new DataErrorException("End date is equal/before start date");
        }
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable long bookingId,
                                               @RequestParam boolean approved) {
        log.info("Patching booking={}, userid={}", bookingId, userId);
        return bookingClient.updateState(userId, bookingId, approved);
    }
}
