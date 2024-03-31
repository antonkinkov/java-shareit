package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    private ItemRequestDto itemRequestDto;

    private UserDto userDto;

    private ItemDto itemDto;

    @BeforeEach
    void init() {
        itemRequestDto = ItemRequestDto.builder()
                .description("desc")
                .build();

        userDto = UserDto.builder()
                .email("email")
                .name("name")
                .build();

        itemDto = ItemDto.builder()
                .name("My item")
                .description("Very interesting item")
                .available(true)
                .build();
    }

    @Test
    void createTest() {
        UserDto user = userService.create(userDto);
        ItemRequestDto requestDto = itemRequestService.create(itemRequestDto, user.getId());
        assertEquals(requestDto.getId(), itemRequestService.getRequestById(user.getId(), requestDto.getId()).getId());

    }

    @Test
    void createWrongTest() {
        UserDto user = userService.create(userDto);
        ItemRequestDto requestDto = itemRequestService.create(itemRequestDto, user.getId());
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(user.getId() + 99, requestDto.getId()).getId());

    }

    @Test
    void getAllByUserTest() {
        UserDto user = userService.create(userDto);
        ItemRequestDto requestDto = itemRequestService.create(itemRequestDto, user.getId());
        assertEquals(1, itemRequestService.getAllByUser(user.getId()).size());
    }

    @Test
    void getAllByUserWrongTest() {
        UserDto user = userService.create(userDto);
        ItemRequestDto requestDto = itemRequestService.create(itemRequestDto, user.getId());
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllByUser(user.getId() + 99));
    }

    @Test
    void getAllTest() {
        UserDto user = userService.create(userDto);
        ItemRequestDto requestDto = itemRequestService.create(itemRequestDto, user.getId());
        itemDto.setRequestId(requestDto.getId());
        itemService.create(itemDto, user.getId());
        assertEquals(1, itemRequestService.getAll(user.getId(), 0, 10).size());
    }

    @Test
    void getAllWrongTestByUser() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getAll(99, 0, 10));
    }

    @Test
    void getAllWrongTestByFrom() {
        UserDto user = userService.create(userDto);
        ItemRequestDto requestDto = itemRequestService.create(itemRequestDto, user.getId());
        assertThrows(BadRequestException.class, () -> itemRequestService.getAll(user.getId(), -1, 10));
    }


    @Test
    void getRequestByIdWrongTestByUser() {
        UserDto user = userService.create(userDto);
        ItemRequestDto requestDto = itemRequestService.create(itemRequestDto, user.getId());
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(user.getId() + 99, requestDto.getId()));
    }

    @Test
    void getRequestByIdWrongTestByRequest() {
        UserDto user = userService.create(userDto);
        ItemRequestDto requestDto = itemRequestService.create(itemRequestDto, user.getId());
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(user.getId(), requestDto.getId() + 99));
    }

}