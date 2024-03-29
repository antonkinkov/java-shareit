package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingShortDto bookingDto, Long userId);

    BookingDto update(Long bookingId, Long userId, Boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getAllByUser(Long userId, BookingState state, int from, int size);

    List<BookingDto> getAllByOwner(Long userId, BookingState state, int from, int size);
}
