package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto create(BookingShortDto bookingDto, Long userId) {
        log.info("Создание бронирования {} пользователем с id {}.", bookingDto, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена с id: " + bookingDto.getItemId()));

        if (!item.getAvailable()) {
            throw new NotFoundException("Предмет недоступен для бронирования.");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new NotFoundException("Не указана дата бронирования");
        }

        if (item.getOwner().equals(user)) {
            throw new NotFoundException("Владелец не может бронировать свои вещи");
        }

        validateCheckTime(bookingDto.getStart(), bookingDto.getEnd());

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, Long userId, Boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new NotFoundException("Бронирование не найдено, id: " + bookingId));

        if(!userId.equals(booking.getItem().getOwner().getId())){
            throw new NotFoundException("Бронирование не подтверждено: у пользователя с id " + userId +
                    " не найдено бронирование с id " + bookingId);
        }

        if(!booking.getStatus().equals(BookingStatus.WAITING)){
            throw new BadRequestException("Бронирование уже подтверждено или отлонено");
        }

        booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new NotFoundException("Бронирование не найдено, id: " + bookingId));

        if(!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())){
            throw new NotFoundException("Информацию о бронировании может смотреть только " +
                    "автор бронирования или хозяин вещи");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByUser(Long userId, BookingState state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        List<BookingDto> bookingDtoList = new ArrayList<>();

        switch (state) {
            case ALL:
                return bookingDtoList(bookingRepository.findAllByBooker(user.getId()));
            case CURRENT:
                return bookingDtoList(bookingRepository
                        .findAllCurrentByBooker(userId, LocalDateTime.now()));
            case PAST:
                return bookingDtoList(bookingRepository
                        .findAllPastByBooker(userId, LocalDateTime.now()));
            case FUTURE:
                return bookingDtoList(bookingRepository
                        .findAllFutureByBooker(userId, LocalDateTime.now()));
            case WAITING:
                return bookingDtoList(bookingRepository
                        .findAllWaitingByBooker(userId, LocalDateTime.now()));
            case REJECTED:
                return bookingDtoList(bookingRepository
                        .findAllRejectedByBooker(userId));
            default:
                throw new NotFoundException("Ошибка в параметре запроса");
        }
    }

//    @Override
//    public List<BookingDto> getAllByOwner(Long userId, BookingState state, List<Booking> bookings) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));
//
//        List<BookingDto> bookingDtoList = new ArrayList<>();
//
//        for (Booking booking : bookings) {
//            bookingDtoList.add(bookingMapper.toBookingDto(booking));
//        }
//
//        switch (state) {
//            case ALL:
//                return bookingDtoList(bookingRepository.findAllByOwner(user.getId()));
//            case CURRENT:
//                return bookingDtoList(bookingRepository
//                        .findAllCurrentByOwner(userId, LocalDateTime.now()));
//            case PAST:
//                return bookingDtoList(bookingRepository
//                        .findAllPastByOwner(userId, LocalDateTime.now()));
//            case FUTURE:
//                return bookingDtoList(bookingRepository
//                        .findAllFutureByOwner(userId, LocalDateTime.now()));
//            case WAITING:
//                return bookingDtoList(bookingRepository
//                        .findAllWaitingByOwner(userId, LocalDateTime.now()));
//            case REJECTED:
//                return bookingDtoList(bookingRepository
//                        .findAllRejectedByOwner(userId));
//            default:
//                throw new NotFoundException("Ошибка в параметре запроса");
//        }
//    }
//
//    @Override
//    public List<BookingDto> getAllByBooker(Long userId, BookingState state, List<Booking> bookings) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));
//
//        List<BookingDto> bookingDtoList = new ArrayList<>();
//
//        for (Booking booking : bookings) {
//            bookingDtoList.add(bookingMapper.toBookingDto(booking));
//        }
//
//        switch (state) {
//            case ALL:
//                return bookingDtoList(bookingRepository.findAllByBooker(user.getId()));
//            case CURRENT:
//                return bookingDtoList(bookingRepository
//                        .findAllCurrentByBooker(userId, LocalDateTime.now()));
//            case PAST:
//                return bookingDtoList(bookingRepository
//                        .findAllPastByBooker(userId, LocalDateTime.now()));
//            case FUTURE:
//                return bookingDtoList(bookingRepository
//                        .findAllFutureByBooker(userId, LocalDateTime.now()));
//            case WAITING:
//                return bookingDtoList(bookingRepository
//                        .findAllWaitingByBooker(userId, LocalDateTime.now()));
//            case REJECTED:
//                return bookingDtoList(bookingRepository
//                        .findAllRejectedByBooker(userId));
//            default:
//                throw new NotFoundException("Ошибка в параметре запроса");
//        }
//    }
//

    private void validateCheckTime(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new NotFoundException("Неверные параметры для времени");
        }
    }
}