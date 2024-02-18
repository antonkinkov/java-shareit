package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
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
    public UserDto getById(Long userId) {
        log.info("Получение пользователя по id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден: " + userId));

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new BadRequestException("Пользователь с такой почтой уже существует");
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден: " + userDto.getId()));

        user.setName(Objects.requireNonNullElse(userDto.getName(), user.getName()));
        user.setEmail(Objects.requireNonNullElse(userDto.getEmail(), user.getEmail()));

        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден: " + userId));
        log.info("Удаление пользователя по id: {}", userId);
        userRepository.deleteById(user.getId());
    }

}