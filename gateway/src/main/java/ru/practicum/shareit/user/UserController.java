package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;
    private final String header = "X-Sharer-User-Id";

    @GetMapping
    public Object getAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public Object getById(@PathVariable Long userId) {
        log.info("Получен запрос на получение пользователя");
        return userClient.getById(userId);
    }

    @PostMapping
    public Object create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public Object update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление пользователя с id = {}", userId);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public Object deleteById(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        return userClient.deleteUser(userId);
    }
}
