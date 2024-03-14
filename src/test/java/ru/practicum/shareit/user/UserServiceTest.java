package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {


    @Autowired
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void init() {
        userDto = UserDto.builder()
                .name("name")
                .email("email")
                .build();
    }

    @Test
    void createTest() {
        UserDto user = userService.create(userDto);
        assertEquals(user.getId(), userService.getById(user.getId()).getId());
    }

    @Test
    void getAllTest() {
        userService.create(userDto);
        assertEquals(1, userService.getAll().size());
    }

    @Test
    void getByIdWrongTest() {
        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void updateTest() {
        UserDto user = userService.create(userDto);
        user.setName("Vasya");
        assertEquals("Vasya", userService.update(user.getId(), user).getName());
    }

    @Test
    void updateWrongTest() {
        UserDto user = userService.create(userDto);
        assertThrows(NotFoundException.class, () -> userService.update(user.getId() + 99, user));
    }

    @Test
    void deleteTest() {
        UserDto user = userService.create(userDto);
        assertEquals(1, userService.getAll().size());
        userService.delete(user.getId());
        assertEquals(0, userService.getAll().size());
    }

    @Test
    void deleteWrongTest() {
        UserDto user = userService.create(userDto);
        assertEquals(1, userService.getAll().size());
        assertThrows(NotFoundException.class, () -> userService.delete(user.getId() + 99));
    }


}

