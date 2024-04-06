package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String header = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId,
                           @RequestHeader(header) Long userId) {
        log.info("Получен запрос на поиск вещи с id = {}", itemId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(header) Long userId) {
        log.info("Получен запрос на получение списка вещей владельца с id = {}", userId);
        return itemService.getAll(userId);
    }

    @PostMapping()
    public ItemDto create(@RequestHeader(header) Long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи для пользователя с id = {}", userId);
        return itemService.create(itemDto, userId);
    }


    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(header) Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        log.info("Получен запрос на получение обновления вещи с id = {}", itemId);
        return itemService.update(itemId, itemDto, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Получен запрос на поиск вещи в аренду для пользователя ");
        return itemService.search(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление отзыва от пользователя с id = {}", userId);
        commentDto.setCreated(LocalDateTime.now());
        return itemService.createComment(userId, itemId, commentDto);
    }
}