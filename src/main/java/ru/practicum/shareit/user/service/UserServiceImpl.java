package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        log.info("Список всех пользователей успешно отправлен");
        List<UserDto> users = new ArrayList<>();

        for (User user : userRepository.findAll()) {
            users.add(UserMapper.toUserDto(user));
        }
        return users;
    }

    @Override
    public UserDto getById(long userId) {
        log.info("Получение пользователя по id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден: " + userId));

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден: " + userDto.getId()));

        user.setName(Objects.requireNonNullElse(userDto.getName(), user.getName()));
        user.setEmail(Objects.requireNonNullElse(userDto.getEmail(), user.getEmail()));

        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден: " + userId));
        log.info("Удаление пользователя по id: {}", userId);
        userRepository.deleteById(user.getId());
    }

    /*
                ОТ ВЕТКИ CONTROLLERS ->

    private void validateByUser(UserDto userDto) {
        if (userDto.getName().isBlank() || userDto.getName() == null) {
            log.info("Отсутствует поле name у пользователя = {}", userDto.getName());
            throw new ValidationException("Отсутствует поле name у пользователя");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            log.info("Отсутствует поле email у пользователя = {}", userDto.getName());
            throw new ValidationException("Отсутствует поле email у пользователя");
        }
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            log.info("Пользователь неверно ввел email = {}", userDto.getEmail());
            throw new ValidationException("Неверный формат email");
        }
        validateEmailUniq(userDto.getEmail());
    }

    private void validateFoundForUser(long userId) {
        if (userRepository.getById(userId) == null) {
            log.info("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateEmailUniq(String email) {
        if (!userRepository.deleteAllById(email)) {
            log.info("Пользователь с таким email = {} уже существует", email);
            throw new ErrorException("Пользователь с таким email уже существует");
        }
    }
     */
}