package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository {

    Item getById(Long itemId);

    Item create(Item item);

    List<Item> getAll();

    Item updateItem(Item item, long userId);

    List<Item> search(String text);

    boolean validateDescriptionUniq(String description);

    boolean validateAvailableUniq(boolean available);

    boolean validateNameUniq(String name);
}
