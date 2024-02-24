package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingState.UNSUPPORTED_STATUS;


@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDto create(BookingShortDto bookingDto, Long userId) {
        log.info("Создание бронирования {} пользователем с id {}.", bookingDto, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена с id: " + bookingDto.getItemId()));

        if (!item.getAvailable()) {
            throw new BadRequestException("Предмет недоступен для бронирования.");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new BadRequestException("Не указана дата бронирования");
        }

        if (item.getOwner().equals(user)) {
            throw new NotFoundException("Владелец не может бронировать свои вещи");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())
                || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new BadRequestException("Неверные параметры для времени");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено, id: " + bookingId));

        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Бронирование не подтверждено: у пользователя с id " + userId +
                    " не найдено бронирование с id " + bookingId);
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException("Бронирование уже подтверждено или отклонено");
        }

        booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено, id: " + bookingId));

        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
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

        List<Booking> bookingDtos = new ArrayList<>();

        switch (state) {
            case ALL:
                bookingDtos.addAll(bookingRepository.findAllByBooker(user, sort));
                break;
            case CURRENT:
                bookingDtos.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartAsc(user,
                        LocalDateTime.now(), LocalDateTime.now()));
                break;
            case PAST:
                bookingDtos.addAll(bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), sort));
                break;
            case FUTURE:
                bookingDtos.addAll(bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), sort));
                break;
            case WAITING:
                bookingDtos.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.WAITING, sort));
                break;
            case REJECTED:
                bookingDtos.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.REJECTED, sort));
                break;
            default:
                throw new BadRequestException("Unknown state: " + UNSUPPORTED_STATUS);
        }

        return bookingDtos.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, BookingState state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        List<Booking> bookingDtos = new ArrayList<>();

        switch (state) {
            case ALL:
                bookingDtos.addAll(bookingRepository.findAllByItemOwner(user, sort));
                break;
            case CURRENT:
                bookingDtos.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartAsc(user,
                        LocalDateTime.now(), LocalDateTime.now()));
                break;
            case PAST:
                bookingDtos.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), sort));
                break;
            case FUTURE:
                bookingDtos.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sort));
                break;
            case WAITING:
                bookingDtos.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.WAITING, sort));
                break;
            case REJECTED:
                bookingDtos.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.REJECTED, sort));
                break;
            default:
                throw new BadRequestException("Unknown state: " + UNSUPPORTED_STATUS);
        }

        return bookingDtos.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

}