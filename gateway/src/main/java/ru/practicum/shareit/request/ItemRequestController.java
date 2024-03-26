package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient requestClient;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public Object create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                         @RequestHeader(header) Long userId) {
        log.info("Получен запрос от пользователя с id = {}", userId);
        return requestClient.create(itemRequestDto, userId);
    }

    @GetMapping
    public Object getAllByUser(@RequestHeader(header) Long userId) {
        log.info("Получен запрос на поиск всех созданных запросов для пользователя с id = {}", userId);
        return requestClient.getAllByUser(userId);
    }

    @GetMapping("/all")
    public Object getAll(@RequestHeader(header) long userId,
                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                         @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получение списока созданных запросов для пользователя с id = {}", userId);
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public Object getById(@RequestHeader(header) long userId,
                          @PathVariable("requestId") long requestId) {
        log.info("Получение запроса по идентификатору = {}", requestId);
        return requestClient.getRequestById(userId, requestId);
    }
}
