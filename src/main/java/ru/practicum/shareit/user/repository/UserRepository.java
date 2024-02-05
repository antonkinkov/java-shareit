package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface UserRepository {

    List<User> getAll();

    User getUserById(long userId);

    User getCreateUser(User user);

    boolean deleteUserById(long userId);

    User updateUser(long userId, User user);

    boolean validateEmailUniq(String email);

}
