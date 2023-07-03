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
    List<Booking> findByStatusAndBookerId(BookingStatus bookingStatus, Long bookerId, Sort sort);

    Page<Booking> findByStatusAndBookerId(BookingStatus bookingStatus, Long bookerId, Pageable pageable);

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, Date end, Sort sort);

    Page<Booking> findByBookerIdAndStartIsAfter(Long bookerId, Date end, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, Date end, Sort sort);

    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, Date end, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(Long bookerId, Date end, Date start, Sort sort);

    Page<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(Long bookerId, Date end, Date start, Pageable pageable);

    List<Booking> findByStatusAndItemOwnerId(BookingStatus bookingStatus, Long ownerId, Sort sort);

    Page<Booking> findByStatusAndItemOwnerId(BookingStatus bookingStatus, Long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, Date end, Sort sort);

    Page<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, Date end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, Date end, Sort sort);

    Page<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, Date end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndIsAfterAndStartIsBefore(Long ownerId, Date end, Date start, Sort sort);

    Page<Booking> findByItemOwnerIdAndEndIsAfterAndStartIsBefore(Long ownerId, Date end, Date start, Pageable pageable);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    Page<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findByItemIdAndBookerIdAndStatusAndStartIsBefore(Long itemId, Long bookerId, BookingStatus status, Date start);

    Booking findTopByItemIdAndStartIsAfterAndStatus(Long itemId, Date date, BookingStatus bookingStatus, Sort sort);

    Booking findTopByItemIdAndStartIsBeforeAndStatus(Long itemId, Date date, BookingStatus bookingStatus, Sort sort);
}
