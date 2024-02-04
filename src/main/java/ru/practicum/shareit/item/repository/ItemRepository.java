package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository {

    Item getById(Long itemId);

    Item create(Item item);

    List<Item> getAll();

    Item updateItem(Item item, long userId);

    Collection<Item> search(String text);

    boolean validateDescriptionUniq(String description);

    boolean validateAvailableUniq(boolean available);

    boolean validateNameUniq(String name);
}
