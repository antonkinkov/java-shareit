package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;


import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private long id = 1;
    private final Map<Long, Item> itemRepository = new HashMap<>();
    private final Map<Long, List<Long>> itemRepositoryByOwner = new HashMap<>();
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item getById(Long itemId) {
        return itemRepository.get(itemId);
    }
    @Override
    public Item create(Long userId, Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> getAll() {
        return items.values();
    }

    @Override
    public Collection<Item> search(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable())
                .collect(Collectors.toList());
    }
    @Override
    public Item updateName(Item item, Long itemId) {
        return items.put(item.getId(), item);
    }

    @Override
    public Item updateDescription(Item item, Long itemId) {
        itemRepository.get(itemId).setDescription(item.getDescription());
        return itemRepository.get(itemId);
    }

    @Override
    public Item updateAvailable(Item item, Long itemId) {
        itemRepository.get(itemId).setAvailable(item.getAvailable());
        return itemRepository.get(itemId);
    }

    @Override
    public Item updateItem(Item itemDto, long itemId) {
        itemRepository.get(itemId).setName(itemDto.getName());
        return itemRepository.get(itemId);
    }

    public Map<Long, List<Long>> getItemRepositoryByOwner() {
        return itemRepositoryByOwner;
    }
    public Map<Long, Item> getItemRepository() {
        return itemRepository;
    }

}
