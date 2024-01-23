package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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

    @PostMapping
        public ItemDto getItemByOwner (@RequestHeader("X-Sharer-User-Id") Long id) {
            log.info("Получен запрос на получение списка вещей владельца с id = {}", id);
            return itemService.getItemByOwner(id);
    }

    @PatchMapping("/{itemId}")
        public ItemDto updateItem (@RequestHeader("X-Sharer-User-Id") Long id,
                                   @RequestBody ItemDto itemDto,
                                   @PathVariable Long userId) {
            log.info("Получен запрос на получение обновления вещи с id = {}", id);
            return itemService.updateItem(id,itemDto,userId);
    }

    @GetMapping("/{itemId}")
        public ItemDto getViewingItem(@PathVariable long itemId) {
            log.info("Получен запрос на просмотр о конкретной вещи с id = {}", itemId);
            return itemService.getViewingItem(itemId);
    }

    @GetMapping
        public ItemDto getViewingUsersForItem (@RequestHeader("X-Sharer-User-Id") long id) {
            log.info("Получен запрос на просмотр владельцем списка всех его вещей c id = {}", id);
            return itemService.getViewingUsersForItem(id);
    }

    @GetMapping("/search")
        public List<ItemDto> search (@RequestParam String text,
                                     @RequestHeader("X-Sharer-User-Id") long id) {
            log.info("Получен запрос на поиск вещи в аренду для пользователя с id = {}", id);
            return itemService.getItemForBooker(text, id);
    }
}
