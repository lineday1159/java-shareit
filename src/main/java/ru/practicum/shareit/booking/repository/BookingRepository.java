package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.Date;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatusAndBookerId(BookingStatus bookingStatus, Long bookerId, Sort sort);

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, Date end, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, Date end, Sort sort);

    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(Long bookerId, Date end, Date start, Sort sort);

    List<Booking> findByStatusAndItemOwnerId(BookingStatus bookingStatus, Long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, Date end, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, Date end, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsAfterAndStartIsBefore(Long ownerId, Date end, Date start, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemIdAndBookerIdAndStatusAndStartIsBefore(Long itemId, Long bookerId, BookingStatus status, Date start);

    Booking findTopByItemIdAndStartIsAfterAndStatus(Long itemId, Date date, BookingStatus bookingStatus, Sort sort);

    Booking findTopByItemIdAndStartIsBeforeAndStatus(Long itemId, Date date, BookingStatus bookingStatus, Sort sort);
}
