package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        log.info("Получен запрос на поиск вещи с id = {}", itemId);
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение списка вещей владельца с id = {}", userId);
        return itemService.getAll(userId);
    }

    @PostMapping()
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи для пользователя с id = {}", userId);
        return itemService.create(userId, itemDto);
    }


    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        log.info("Получен запрос на получение обновления вещи с id = {}", itemId);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text) {
        log.info("Получен запрос на поиск вещи в аренду для пользователя с id = {}");
        return itemService.search(text);
    }
}