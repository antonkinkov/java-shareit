package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestBody BookingShortDto bookingDto,
                                             @RequestHeader(header) Long userId) {
        log.info("Получен запрос на добавление отзыва от пользователя с id = {}", userId);
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                             @RequestHeader(header) Long userId,
                             @RequestParam Boolean approved) {
        log.info("Получен запрос на обновление статуса бронирования id " + bookingId + ", пользователем id: " + userId);
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(header) Long userId,
                              @PathVariable Long bookingId) {
        log.info("Получен запрос на получение информации о бронировании: {}", bookingId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader(header) Long userId,
                                         @RequestParam(name = "state",
                                                 required = false,
                                                 defaultValue = "ALL") BookingState state,
                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получение списка всех бронирований текущего пользователяс id = {}", userId);
        return bookingService.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(name = "state",
                                                  required = false,
                                                  defaultValue = "ALL") BookingState state,
                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получение списка всех бронирований текущего владельца id = {}", userId);
        return bookingService.getAllByOwner(userId, state, from, size);
    }
}
