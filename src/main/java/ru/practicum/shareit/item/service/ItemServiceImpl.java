package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public class ItemServiceImpl implements ItemService{
    @Override
    public ItemDto getItemByOwner(Long id) {
        return null;
    }

    @Override
    public ItemDto updateItem(Long id, ItemDto itemDto, Long userId) {
        return null;
    }

    @Override
    public ItemDto getViewingItem(long itemId) {
        return null;
    }

    @Override
    public ItemDto getViewingUsersForItem(long id) {
        return null;
    }

    @Override
    public List<ItemDto> getItemForBooker(String text, long id) {
        return null;
    }
}
