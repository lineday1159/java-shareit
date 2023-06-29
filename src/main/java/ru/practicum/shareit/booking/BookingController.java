package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long bookingId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getBookingListByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findByState(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingListByStateAndOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findByStateAndOwner(state, userId);
    }

    @PostMapping()
    public BookingDto postBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody @Valid BookingReqDto bookingDto) throws ParseException {
        return bookingService.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long bookingId,
                                   @RequestParam boolean approved) {
        return bookingService.updateState(bookingId, approved, userId);
    }
}
