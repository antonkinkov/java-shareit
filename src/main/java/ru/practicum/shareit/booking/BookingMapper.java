package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


@UtilityClass
public class BookingMapper {

    public static BookingDto toBookingDto (Booking booking) {
        return BookingDto
                .builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }

    public static Booking toBooking(BookingShortDto dto) {

        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .build();
    }

}

