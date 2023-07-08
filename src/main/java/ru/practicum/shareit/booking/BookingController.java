package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.text.ParseException;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    @Autowired
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long bookingId) {
        log.info("GET request to get booking - {}, by user - {}", bookingId, userId);
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getBookingListByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("GET request to get bookingList with state - {}, by user - {}", state, userId);
        return bookingService.findByState(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingListByStateAndOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                          @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("GET request to get bookingList with state - {}, by owner - {}", state, userId);
        return bookingService.findByStateAndOwner(state, userId, from, size);
    }

    @PostMapping()
    public BookingDto postBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody @Valid BookingReqDto bookingDto) throws ParseException {
        log.info("POST request to add booking by user - {}", userId);
        return bookingService.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long bookingId,
                                   @RequestParam boolean approved) {
        log.info("PATCH request to update booking by user - {}", userId);
        return bookingService.updateState(bookingId, approved, userId);
    }
}
