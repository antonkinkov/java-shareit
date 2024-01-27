package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository{

    @Override
    public Collection<User> findAll() {
        return null;
    }

    @Override
    public Map<Object, Object> getUserRepository() {
        return null;
    }

    @Override
    public User getUserById(long userId) {
        return null;
    }


    @Override
    public User getCreateUser(User user) {
        return null;
    }

    @Override
    public void deleteUserById(long userId) {

    }

    @Override
    public User updateUser(long userId, User user) {
        return null;
    }

    @Override
    public User updateUserFieldEmail(long userId, User user) {
        return null;
    }

    @Override
    public User updateUserFieldName(long userId, User user) {
        return null;
    }

    @Override
    public Map<Object, Object> getUserEmailRepository() {
        return null;
    }
}
