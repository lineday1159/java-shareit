package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.util.Date;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private Long id;
    private Date start;
    private Date end;
    private Long item;
    private Long booker;
    private BookingStatus status;

}
