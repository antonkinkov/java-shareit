package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.UserMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public List<UserDto> getAllUsers() {
        log.info("Список всех пользователей успешно отправлен");
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.getAll()) {
            users.add(toUserDto(user));
        }
        return users;
    }

    @Override
    public UserDto getUserById(long userId) {
        validateFoundForUser(userId);
        return UserMapper.toUserDto(userRepository.getUserById(userId));
    }

    @Override
    public UserDto getCreateUser(UserDto userDto) {
        validateByUser(userDto);
        return UserMapper.toUserDto(userRepository.getCreateUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        validateFoundForUser(userId);
        if (userDto.getEmail() != null && userDto.getName() != null) {
            return UserMapper.toUserDto(userRepository.updateUser(userId, UserMapper.toUser(userDto)));
        }
        if (userDto.getEmail() != null) {
            validateForExistEmail(userId, userDto);
            return UserMapper.toUserDto(userRepository.updateUserEmail(userId, UserMapper.toUser(userDto)));
        }
        return UserMapper.toUserDto(userRepository.updateUserName(userId, UserMapper.toUser(userDto)));
    }

    @Override
    public boolean deleteUserById(long userId) {
        validateFoundForUser(userId);
        return userRepository.deleteUserById(userId);
    }

    private void validateByUser(UserDto userDto) {
        if (userDto.getName().isBlank() || userDto.getName() == null) {
            log.info("Отсутствует поле name у пользователя = {}", userDto.getName());
            throw new ValidationException("Отсутствует поле name у пользователя");
        }
        if (userDto.getEmail().isBlank() || userDto.getEmail() == null) {
            log.info("Отсутствует поле email у пользователя = {}", userDto.getName());
            throw new ValidationException("Отсутствует поле email у пользователя");
        }
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            log.info("Пользователь неверно ввел email = {}", userDto.getEmail());
            throw new ValidationException("Неверный формат email");
        }
        if (userRepository.getUserEmailRepository().containsKey(userDto.getEmail())) {
            log.info("Пользователь с таким email = {} уже существует", userDto.getEmail());
            throw new ErrorException("Пользователь с таким email уже существует");
        }
    }
    private void validateForExistEmail(long userId, UserDto userDto) {
        if (userRepository.getUserEmailRepository().containsKey(userDto.getEmail())) {
            if (userRepository.getUserEmailRepository().get(userDto.getEmail()).getId() != userId)
                throw new ErrorException("Пользователь с таким email уже существует");
        }
    }

    private void validateFoundForUser(long userId) {
        if (!userRepository.getUserRepository().containsKey(userId)) {
            log.info("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
    }
}