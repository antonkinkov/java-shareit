package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public ItemDto getById(Long itemId) {
        validateFoundForItem(itemId);
        return toItemDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        log.info("Список всех вещей успешно отправлен");
        List<ItemDto> items = new ArrayList<>();
        for (Item item : itemRepository.getAll()) {
            if (item.getOwner().getId().equals(userId)) {
                items.add(toItemDto(item));
            }
        }
        return items;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = validateFindForUser(userId);
        validateByItem(itemDto);
        return toItemDto(itemRepository.create(ItemMapper.toItem(itemDto, user)));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {

        Item item = validateFoundForItem(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Невозможно обновить вещь с таким id");
        }

        item.setName(Objects.requireNonNullElse(itemDto.getName(), item.getName()));
        item.setDescription(Objects.requireNonNullElse(itemDto.getDescription(), item.getDescription()));
        item.setAvailable(Objects.requireNonNullElse(itemDto.getAvailable(), item.getAvailable()));

        return toItemDto(itemRepository.updateItem(item, itemId));

    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.search(text);
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
        User user = userRepository.getUserById(userId);
        if (user == null) {
            log.info("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        return user;
    }

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
}
