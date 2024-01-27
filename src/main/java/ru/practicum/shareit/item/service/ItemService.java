package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto getById(Long itemId);

    List<ItemDto> getAll(Long userId);

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    Collection<Item> search(String text);


}
