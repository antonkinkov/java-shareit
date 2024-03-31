package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("Получен запрос на получение пользователя");
        return userService.getById(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление пользователя с id = {}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Boolean> deleteById(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        userService.delete(userId);
        return ResponseEntity.ok(true);
    }

}
