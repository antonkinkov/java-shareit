package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemRepositoryImpl implements ItemRepository {

    private long id = 1;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item getById(Long itemId) {
        return items.get(itemId);
    }
    @Override
    public Item create(Long userId, Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
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
    public Item updateItem(Item item, long userId) {
        items.put(userId, item);
        log.info("Поле name у пользователя с id = {} обновлено", userId);
        return items.get(userId);
    }


}
