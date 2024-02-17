package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingDto bookingDto;

    @Override
    public ItemDto getById(Long itemId) {
        validateFoundForItem(itemId);
        return toItemDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        log.info("Список всех вещей успешно отправлен");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));
        List<Item> items = itemRepository.findAllByOwnerId(user.getId());

        if (items.size() != 0) {
            for (Item item : items) {

            }
        }

        return null;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = validateFindForUser(userId);
        validateByItem(itemDto);
        return toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, user)));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {

        Item item = validateFoundForItem(itemId);

        Item itemUp = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Вещь с таким id не найдена: " + itemDto.getId()));

        item.setName(Objects.requireNonNullElse(itemDto.getName(), item.getName()));
        item.setDescription(Objects.requireNonNullElse(itemDto.getDescription(), item.getDescription()));
        item.setAvailable(Objects.requireNonNullElse(itemDto.getAvailable(), item.getAvailable()));

        itemRepository.save(itemUp);
        return toItemDto(itemUp);
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> items = new ArrayList<>();
        if (text.isBlank()) {
            return items;
        }
        for (Item item : itemRepository.search(text)) {
            items.add(itemMapper.toItemDto(item));
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

    private Item validateFoundForItem(long itemId) {
        Item item = itemRepository.getById(itemId);
        if (item == null) {
            log.info("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException("Вещь с таким id не найдена");
        }
        return item;
    }

    private User validateFindForUser(long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            log.info("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        return user;
    }
/*
                ОТ ВЕТКИ CONTROLLERS ->

    private void validateDescriptionUniq(String description) {
        if (!itemRepository.validateDescriptionUniq(description)) {
            log.info("Вещь с таким description = {} уже существует", description);
            throw new ErrorException("Вещь с таким email уже существует");
        }
    }

    private void validateAvailableUniq(Boolean available) {
        if (!itemRepository.validateAvailableUniq(available)) {
            log.info("Вещь с таким available = {} уже существует", available);
            throw new ErrorException("Вещь с таким available уже существует");
        }
    }

    private void validateNameUniq(String name) {
        if (!itemRepository.validateNameUniq(name)) {
            log.info("Вещь с таким available = {} уже существует", name);
            throw new ErrorException("Вещь с таким available уже существует");
        }
    }
*/


    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {

        User user = userRepository.findById(commentDto.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден: " + commentDto.getAuthorId()));

        Item item = itemRepository.findById(commentDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена: " + commentDto.getItemId()));

        List<Booking> bookings = bookingRepository
                .findByItemAndBooking(user.getId(), item.getId(), LocalDateTime.now());

        if (bookings.size() == 0) {
            throw new NotFoundException("У данного пользователя нет бронирований этой вещи");
        }

        Comment comment = itemMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);

        return itemMapper.toCommentDto(commentRepository.save(comment));
    }
}
