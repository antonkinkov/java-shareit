package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(header) Long userId) {
        log.info("Получен запрос от пользователя с id = {}", userId);
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(header) Long userId) {
        log.info("Получен запрос на поиск всех созданных запросов для пользователя с id = {}", userId);
        return itemRequestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(header) long userId,
                                       @RequestParam(name = "from", defaultValue = "0", required = false) int from,
                                       @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        log.info("Получение списока созданных запросов для пользователя с id = {}", userId);
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(header) long userId,
                                  @PathVariable("requestId") long requestId) {
        log.info("Получение запроса по идентификатору = {}", requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }
}
