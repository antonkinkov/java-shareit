package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.*;

public class ItemRepositoryImpl implements ItemRepository {

    private long id = 1;
    private final Map<Long, Item> itemRepository = new HashMap<>();
    private final Map<Long, List<Long>> itemRepositoryByOwner = new HashMap<>();

    public Map<Long, Item> getItemRepository() {
        return itemRepository;
    }

    @Override
    public Item getById(Long itemId) {
        return null;
    }

    @Override
    public Item create(Long userId, Item item) {
        return null;
    }

    @Override
    public Collection<Item> getAll() {
        return null;
    }

    @Override
    public Item updateName(Item item, Long itemId) {
        return null;
    }

    @Override
    public Item updateDescription(Item item, Long itemId) {
        return null;
    }

    @Override
    public Item updateAvailable(Item item, Long itemId) {
        return null;
    }

    @Override
    public Item updateItem(Item itemDto, long itemId) {
        return null;
    }

    @Override
    public Collection<Item> search(String text) {
        return null;
    }

    public Map<Long, List<Long>> getItemRepositoryByOwner() {
        return itemRepositoryByOwner;
    }


}
