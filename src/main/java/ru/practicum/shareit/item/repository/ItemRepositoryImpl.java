package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;


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
    public Item create(Item item) {
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
    public boolean validateDescriptionUniq(String description) {
        List<String> descriptions = items.values()
                .stream()
                .map(Item::getDescription)
                .filter(userDescription -> userDescription.equals(description))
                .collect(Collectors.toList());
        return descriptions.isEmpty();
    }

    @Override
    public boolean validateAvailableUniq(boolean available) {
        List<Boolean> availables = items.values()
                .stream()
                .map(Item::getAvailable)
                .filter(userAvailable -> userAvailable.equals(available))
                .collect(Collectors.toList());
        return availables.isEmpty();
    }


   @Override
    public boolean validateNameUniq(String name) {
        List<String> names = items.values()
                .stream()
                .map(Item::getName)
                .filter(userName -> userName.equals(name))
                .collect(Collectors.toList());
        return names.isEmpty();
    }
    @Override
    public Item updateItem(Item item, long userId) {
        items.put(userId, item);
        log.info("Поле name у пользователя с id = {} обновлено", userId);
        return item;
    }
}
