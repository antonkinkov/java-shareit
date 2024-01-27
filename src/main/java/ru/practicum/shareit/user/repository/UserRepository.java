package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.Future;

public interface UserRepository {

    Collection<User> findAll();

    Map<Object, Object> getUserRepository();

    User getUserById(long userId);

    User getCreateUser(User user);

    void deleteUserById(long userId);

    User updateUser(long userId, User user);

    User updateUserFieldEmail(long userId, User user);

    User updateUserFieldName(long userId, User user);

    Map<Object, Object> getUserEmailRepository();
}
