package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>  {

    @Query(value = "SELECT b.* " +
            "FROM bookings AS b " +
            "WHERE b.booker_id = ?1 " +
            "AND b.item_id = ?2 " +
            "AND b.start_data < ?3 " +
            "AND b.status = 'APPROVED' " +
            "nativeQuery = true")
    List<Booking> findByItemAndBooking(Long userId, Long itemId, LocalDateTime now);

}
