package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private long id = 1;
    private final Map<Long, User> userRepository = new HashMap<>();
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> userEmailRepository = new HashMap<>();


    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.get(userId);
    }

    @Override
    public User getCreateUser(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean deleteUserById(long userId) {
        return users.remove(id) != null;
    }

    @Override
    public User updateUser(long userId, User user) {
        userRepository.get(userId).setName(user.getName());
        userEmailRepository.put(userRepository.get(userId).getEmail(), userRepository.get(userId));
        log.info("Поле name у пользователя с id = {} обновлено", userId);
        return userRepository.get(userId);
    }

        @Override
        public User updateUserEmail(long userId, User user) {
            userRepository.get(userId).setName(user.getName());
            userEmailRepository.put(userRepository.get(userId).getEmail(), userRepository.get(userId));
            return userRepository.get(userId);
        }

        @Override
        public User updateUserName(long userId, User user) {
            userEmailRepository.remove(userRepository.get(userId).getEmail());
            userRepository.get(userId).setEmail(user.getEmail());
            userEmailRepository.put(userRepository.get(userId).getEmail(), userRepository.get(userId));
            return userRepository.get(userId);
        }

    @Override
    public Map<Long, User> getUserRepository() {
        return userRepository;
    }

    @Override
    public Map<String, User> getUserEmailRepository() {
        return userEmailRepository;
    }
}
