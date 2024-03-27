package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId,
                                                                          BookingStatus status, LocalDateTime end);

    List<Booking> findAllByBookerId(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusEquals(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerId(Long userId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusEquals(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemIdInAndStartBeforeOrderByItemIdAscStartAsc(List<Long> itemIds, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long id, LocalDateTime start);

    List<Booking> findAllByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime now);
}
