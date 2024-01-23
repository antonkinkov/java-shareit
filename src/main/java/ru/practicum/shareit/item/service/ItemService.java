package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemByOwner(Long id);

    ItemDto updateItem(Long id, ItemDto itemDto, Long userId);

    ItemDto getViewingItem(long itemId);

    ItemDto getViewingUsersForItem(long id);

    List<ItemDto> getItemForBooker(String text, long id);
}
