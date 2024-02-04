package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean validateEmailUniq(String email) {
        List<String> emails = users.values()
                .stream()
                .map(User::getEmail)
                .filter(userEmail -> userEmail.equals(email))
                .collect(Collectors.toList());
        return emails.isEmpty();
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public User getCreateUser(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean deleteUserById(long userId) {
        return users.remove(userId) != null;
    }

    @Override
    public User updateUser(long userId, User user) {
        users.put(userId, user);
        log.info("Поле name у пользователя с id = {} обновлено", userId);
        return users.get(userId);
    }
}
