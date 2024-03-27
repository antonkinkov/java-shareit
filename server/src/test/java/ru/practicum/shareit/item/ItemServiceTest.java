package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTest {
    private final LocalDateTime endDate = LocalDateTime.now().plusDays(10);
    private final LocalDateTime startDate = LocalDateTime.now();
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRequestService itemRequestService;
    private UserDto firstUserDto;
    private UserDto secondUserDto;
    private ItemDto itemDto;
    private ItemRequestDto itemRequestDto;
    private CommentDto commentDto;
    private BookingShortDto bookingDto;

    @BeforeEach
    void init() {
        bookingDto = BookingShortDto.builder()
                .end(endDate)
                .start(startDate)
                .build();

        firstUserDto = UserDto.builder()
                .name("Eric")
                .email("eric@gmail.com")
                .build();

        secondUserDto = UserDto.builder()
                .name("George")
                .email("George@gmail.com")
                .build();

        itemDto = ItemDto.builder()
                .name("My item")
                .description("Very interesting item")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .text("text")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .build();

    }

    @Test
    void createTest() {
        UserDto user = userService.create(firstUserDto);
        itemService.create(itemDto, user.getId());
        assertEquals(1, itemService.getAll(user.getId()).size());
    }

    @Test
    void createWrongTest() {
        UserDto user = userService.create(firstUserDto);
        itemService.create(itemDto, user.getId());
        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, 999L));
    }

    @Test
    void createTestWithItemRequest() {
        UserDto user = userService.create(firstUserDto);
        ItemShortDto itemShortDto = ItemShortDto.builder()
                .ownerId(user.getId())
                .name("name")
                .build();
        itemRequestDto.setItems(List.of(itemShortDto));

        ItemRequestDto itemRequestDto1 = itemRequestService.create(itemRequestDto, user.getId());
        itemDto.setRequestId(itemRequestDto1.getId());
        ItemDto item = itemService.create(itemDto, user.getId());
        assertEquals(1, itemService.getAll(user.getId()).size());
    }

    @Test
    void createWrongTestWithItemRequest() {
        UserDto user = userService.create(firstUserDto);
        ItemShortDto itemShortDto = ItemShortDto.builder()
                .ownerId(user.getId())
                .name("name")
                .build();
        itemRequestDto.setItems(List.of(itemShortDto));
        itemRequestService.create(itemRequestDto, user.getId());

        itemDto.setRequestId(999L);
        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, user.getId()));
    }

    @Test
    void updateTest() {
        UserDto user = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, user.getId());

        item.setName("update test");
        assertEquals("update test", itemService.update(item.getId(), item, user.getId()).getName());
    }

    @Test
    void updateWrongTestByItemId() {
        UserDto user = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, user.getId());

        item.setName("update test");
        assertThrows(NotFoundException.class, () -> itemService.update(item.getId() + 99, item, user.getId()));

    }

    @Test
    void updateWrongTestByUserId() {
        UserDto user = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, user.getId());

        item.setName("update test");
        assertThrows(NotFoundException.class, () -> itemService.update(item.getId(), item, user.getId() + 99));

    }

    @Test
    void getByIdTest() {
        UserDto user = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, user.getId());

        assertEquals(item.getId(), itemService.getById(item.getId(), user.getId()).getId());
    }

    @Test
    void getByIdWrongTest() {
        UserDto user = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, user.getId());

        assertThrows(NotFoundException.class, () -> itemService.getById(item.getId() + 99, user.getId()).getId());
    }

    @Test
    void getByIdWithLastBookingTest() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);

        bookingDto.setItemId(item.getId());
        bookingService.create(bookingDto, user.getId());

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingService.create(bookingDto, user.getId());
        assertEquals(item.getId(), itemService.getById(item.getId(), owner.getId()).getId());
    }

    @Test
    void getAllTest() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);

        bookingDto.setItemId(item.getId());
        bookingService.create(bookingDto, user.getId());

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingService.create(bookingDto, user.getId());

        assertEquals(1, itemService.getAll(owner.getId()).size());
    }

    @Test
    void searchTest() {
        UserDto user = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, user.getId());

        assertEquals(1, itemService.search("interesting").size());
    }

    @Test
    void createCommentWrongTest() {
        UserDto user = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, user.getId());

        assertThrows(BadRequestException.class, () -> itemService.createComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void createCommentTest() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        BookingDto booking = bookingService.create(bookingDto, user.getId());
        BookingDto existedBooking = bookingService.update(booking.getId(), owner.getId(), true);

        assertEquals(item.getId(), itemService.createComment(user.getId(), item.getId(), commentDto).getItemId());
    }
}
