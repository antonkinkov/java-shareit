package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.*;

public interface UserRepository {


    User updateUserEmail(long userId, User user);

    User updateUserName(long userId, User user);

    Map<Long, User> getUserRepository();

    Collection<User> getAll();

    User getUserById(long userId);

    User getCreateUser(User user);

    boolean deleteUserById(long userId);

    User updateUser(long userId, User user);

    Map<String, User> getUserEmailRepository();
}
