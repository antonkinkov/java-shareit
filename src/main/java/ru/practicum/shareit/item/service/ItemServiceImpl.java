package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemRepositoryImpl itemRepository;
    private final UserRepositoryImpl userRepository;


    @Override
    public ItemDto getById(Long itemId) {
        validateFindItem(itemId);
        return ItemMapper.toItemDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        List<ItemDto> ownerItems = new ArrayList<>();
        for (Item item : itemRepository.getAll()) {
            if (item.getOwner() != null && userId.equals(item.getOwner().getId())) {
                ownerItems.add(ItemMapper.toItemDto(item));
            }
        }
        log.info("Полный писок вещей владельца с id {} ", userId);
        return ownerItems;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        validateFindUser(userId);
        validateByItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.create(userId, ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        validateByItem(itemDto);
        validateFindItem(itemId);
        validateFindUser(userId);
        validateFindOwner(userId, itemId);

        if (itemDto.getDescription() != null && itemDto.getName() != null) {
            return ItemMapper.toItemDto(itemRepository.updateItem(ItemMapper.toItem(itemDto), itemId));
        }
        if (itemDto.getDescription() != null) {
            return ItemMapper.toItemDto(itemRepository.updateDescription(ItemMapper.toItem(itemDto), itemId));
        }
        if (itemDto.getName() != null) {
            return ItemMapper.toItemDto(itemRepository.updateName(ItemMapper.toItem(itemDto), itemId));
        }
        if (itemDto.getAvailable() != null) {
            return ItemMapper.toItemDto(itemRepository.updateAvailable(ItemMapper.toItem(itemDto), itemId));
        }
        return itemDto;
    }

    @Override
    public Collection<Item> search(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<Item> items = itemRepository.search(text);
        return items;
    }

    private void validateByItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
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

    private void validateFindItem(long itemId) {
        if (!itemRepository.getItemRepository().containsKey(itemId)) {
            log.info("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException("Вещь с таким id не найдена");
        }
    }

    private void validateFindUser(long userId) {
        if (!userRepository.getUserRepository().containsKey(userId)) {
            log.info("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }

    private void validateFindOwner(long userId, long itemId) {
        if (!itemRepository.getItemRepositoryByOwner().containsKey(userId)) {
            throw new NotFoundException("У пользователя с id = " + userId + " нет вещи с id = " + itemId);
        }
        if (!itemRepository.getItemRepositoryByOwner().get(userId).contains(itemId)) {
            throw new NotFoundException("У пользователя с id = " + userId + " нет вещи с id = " + itemId);
        }
    }
}
