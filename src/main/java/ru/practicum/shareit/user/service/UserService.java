package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    UserDto getCreateUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUserById(long userId);

}

