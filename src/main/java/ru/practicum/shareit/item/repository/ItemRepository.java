package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ItemRepository {

    Item getById(Long itemId);

    Item create(Long userId, Item item);

    List<Item> getAll();

    Item updateItem(Item item, long userId);
    Collection<Item> search(String text);
}
