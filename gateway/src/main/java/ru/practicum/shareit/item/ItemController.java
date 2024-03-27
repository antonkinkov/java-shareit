package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final String header = "X-Sharer-User-Id";

    @GetMapping
    public Object getAll(@RequestHeader(header) Long userId) {
        log.info("Получен запрос на получение списка вещей владельца с id = {}", userId);
        return itemClient.getAll(userId);
    }

    @PostMapping()
    public Object create(@RequestHeader(header) Long userId,
                         @RequestBody @Valid ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи для пользователя с id = {}", userId);
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Object update(@RequestHeader(header) Long userId,
                         @RequestBody ItemDto itemDto,
                         @PathVariable Long itemId) {
        log.info("Получен запрос на получение обновления вещи с id = {}", itemId);
        return itemClient.update(itemId, itemDto, userId);
    }

    @GetMapping("/search")
    public Object search(@RequestParam String text) {
        log.info("Получен запрос на поиск вещи в аренду ");
        return itemClient.search(text);
    }

    @PostMapping("{itemId}/comment")
    public Object createComment(@Valid @RequestBody CommentDto commentDto,
                                @PathVariable Long itemId,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление отзыва от пользователя с id = {}", userId);
        commentDto.setCreated(LocalDateTime.now());
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public Object getById(@PathVariable Long itemId,
                          @RequestHeader(header) Long userId) {
        log.info("Получен запрос на поиск вещи с id = {}", itemId);
        return itemClient.getById(itemId, userId);
    }
}
