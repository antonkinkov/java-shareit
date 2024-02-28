package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена: " + itemId));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList()));

        if (item.getOwner().getId().equals(userId)) {
            List<Booking> lastBookings = bookingRepository.findAllByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now());
            if (!lastBookings.isEmpty()) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBookings.get(0)));
            }

            if (itemDto.getLastBooking() != null) {
                List<Booking> nextBookings = bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now());
                if (!nextBookings.isEmpty()) {
                    itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBookings.get(0)));
                }

            }
        }


        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(Long userId) {

        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        List<ItemDto> itemDtos = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        List<Long> itemIds = itemDtos.stream().map(ItemDto::getId).collect(Collectors.toList());

        List<Booking> lastBookings = bookingRepository
                .findAllByItemIdInAndStartBeforeOrderByItemIdAscStartAsc(itemIds, LocalDateTime.now());

        Map<Long, List<Booking>> itemToBooking = lastBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        itemDtos.forEach(itemDto -> {
            List<Booking> bookings = itemToBooking.get(itemDto.getId());
            if (bookings != null && !bookings.isEmpty()) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(bookings.get(0)));

                List<Booking> nextBooking = bookingRepository
                        .findAllByItemIdAndStartAfterOrderByStartAsc(itemDto.getId(), bookings.get(0).getStart());
                if (!nextBooking.isEmpty()) {
                    itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking.get(0)));
                }
            }
            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId()).stream()
                    .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        });

        return itemDtos;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {

        validateByItem(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));
        return toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, user)));
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена: " + itemId));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Невозможно обновить вещь");
        }

        item.setName(Objects.requireNonNullElse(itemDto.getName(), item.getName()));
        item.setDescription(Objects.requireNonNullElse(itemDto.getDescription(), item.getDescription()));
        item.setAvailable(Objects.requireNonNullElse(itemDto.getAvailable(), item.getAvailable()));

        return toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> items = new ArrayList<>();
        if (text.isBlank()) {
            return items;
        }
        for (Item item : itemRepository.search(text)) {
            items.add(ItemMapper.toItemDto(item));
        }
        return items;
    }


    private void validateByItem(ItemDto itemDto) {
        if (itemDto.getName().isBlank() || itemDto.getName() == null) {
            log.info("Отсутствует поле name");
            throw new ValidationException("Отсутствует поле name");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.info("Отсутствует поле description");
            throw new ValidationException("Отсутствует поле description");
        }
        if (itemDto.getAvailable() == null) {
            log.info("Отсутствует поле available");
            throw new ValidationException("Отсутствует поле available");
        }
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден: " + commentDto.getAuthorId()));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена: " + commentDto.getItemId()));

        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(user.getId(), item.getId(),
                        BookingStatus.APPROVED, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new BadRequestException("У данного пользователя нет бронирований этой вещи");
        }

        Comment comment = ItemMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);

        return ItemMapper.toCommentDto(commentRepository.save(comment));
    }
}
