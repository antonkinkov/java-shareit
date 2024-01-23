package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
        public List<UserDto> getAllUsers() {
            log.info("Получен запрос на получение всех пользователей");
            return userService.getAllUsers();
    }

    @GetMapping("/{id}")
        public UserDto getUserById (@PathVariable Long userId) {
            log.info("Получен запрос на получение пользователя");
            return userService.getUserById(userId);
    }

    @PostMapping
        public UserDto createUser (@RequestBody UserDto userDto) {
            log.info("Получен запрос на добавление пользователя");
            return userService.getCreateUser(userDto);
    }

    @PatchMapping("/{id}")
        public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
            log.info("Получен запрос на обновление пользователя с id = {}", userId);
            return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
        public void deleteUserById (@PathVariable long userId) {
            log.info("Получен запрос на удаление пользователя с id = {}", userId);
            userService.deleteUserById(userId);
    }
}
