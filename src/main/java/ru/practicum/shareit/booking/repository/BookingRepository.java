package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId,
                                                                          BookingStatus status, LocalDateTime end);

    List<Booking> findAllByBooker(User user, Sort sort);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartAsc(User user, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerAndStatusEquals(User user, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwner(User owner, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartAsc(User owner, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIdInAndStartBeforeOrderByItemIdAscStartAsc(List<Long> itemIds, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long id, LocalDateTime start);

    List<Booking> findAllByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime now);
}
