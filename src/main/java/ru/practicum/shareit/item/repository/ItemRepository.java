package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ItemRepository {
    public Map<Long, List<Long>> getItemRepositoryByOwner();
    public Map<Long, Item> getItemRepository();

    Item getById(Long itemId);

    Item create(Long userId, Item item);

    Collection<Item> getAll();

    Item updateName(Item item, Long itemId);

    Item updateDescription(Item item, Long itemId);

    Item updateAvailable(Item item, Long itemId);

    Item updateItem(Item itemDto, long itemId);

    Collection<Item> search(String text);
}
