package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>  {

    @Query(value = "SELECT b.* " +
            "FROM bookings AS b " +
            "WHERE b.booker_id = ?1 " +
            "AND b.item_id = ?2 " +
            "AND b.start_data < ?3 " +
            "AND b.status = 'APPROVED'",
            nativeQuery = true)
    List<Booking> findByItemAndBooking(Long userId, Long itemId, LocalDateTime now);


    List<Booking> findAllByBooker(User user, Sort sort);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerAndStatusEquals(User user, BookingStatus status, Sort sort);


    List<Booking> findAllByItemOwner(User owner, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIdInAndStartBeforeOrderByItemIdAscStartAsc(List<Long> itemIds, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long id, LocalDateTime start);
}
