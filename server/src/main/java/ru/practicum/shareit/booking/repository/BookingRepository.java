package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.Date;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByStatusAndBookerId(BookingStatus bookingStatus, Long bookerId, Pageable pageable);

    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfter(Long bookerId, Date end, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, Date end, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(Long bookerId, Date end, Date start, Pageable pageable);

    Page<Booking> findByStatusAndItemOwnerId(BookingStatus bookingStatus, Long ownerId, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, Date end, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, Date end, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndIsAfterAndStartIsBefore(Long ownerId, Date end, Date start, Pageable pageable);

    Page<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findByItemIdAndBookerIdAndStatusAndStartIsBefore(Long itemId, Long bookerId, BookingStatus status, Date start);

    Booking findTopByItemIdAndStartIsAfterAndStatus(Long itemId, Date date, BookingStatus bookingStatus, Sort sort);

    Booking findTopByItemIdAndStartIsBeforeAndStatus(Long itemId, Date date, BookingStatus bookingStatus, Sort sort);
}
