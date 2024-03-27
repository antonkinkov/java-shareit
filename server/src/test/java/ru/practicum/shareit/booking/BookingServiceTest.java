package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceTest {

    private final LocalDateTime endDate = LocalDateTime.now().plusDays(10);
    private final LocalDateTime startDate = LocalDateTime.now();

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private BookingShortDto bookingDto;
    private UserDto firstUserDto;
    private UserDto secondUserDto;
    private ItemDto itemDto;
    @Autowired
    private ObjectMapper mapper;

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
    }

    @Test
    void createTest() {

        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);

        bookingDto.setItemId(item.getId());
        BookingDto booking = bookingService.create(bookingDto, user.getId());
        BookingDto existBooking = bookingService.getById(booking.getId(), user.getId());
        assertEquals(1L, existBooking.getId());

    }

    @Test
    void createWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void createWrongItemTest() {
        UserDto owner = userService.create(firstUserDto);
        bookingDto.setItemId(2L);
        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, owner.getId()));
    }

    @Test
    void createWrongItemTestByAvailableFalse() {
        UserDto owner = userService.create(firstUserDto);
        itemDto.setAvailable(false);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        assertThrows(BadRequestException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void createWrongItemTestByStartAndEndDateIsNull() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);

        bookingDto.setItemId(item.getId());
        bookingDto.setStart(null);
        bookingDto.setEnd(null);
        assertThrows(BadRequestException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void createWrongItemTestByUserIsOwner() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        bookingDto.setItemId(item.getId());
        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, owner.getId()));
    }

    @Test
    void createWrongItemTestByEndDateBeforeStartDate() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);

        bookingDto.setItemId(item.getId());

        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now());
        assertThrows(BadRequestException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void updateItemTestByApproved() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        BookingDto booking = bookingService.create(bookingDto, user.getId());
        BookingDto existedBooking = bookingService.update(booking.getId(), owner.getId(), true);

        assertEquals(BookingStatus.APPROVED.name(), existedBooking.getStatus().name());
    }

    @Test
    void updateItemTestByRejected() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        BookingDto booking = bookingService.create(bookingDto, user.getId());
        BookingDto existedBooking = bookingService.update(booking.getId(), owner.getId(), false);

        assertEquals(BookingStatus.REJECTED.name(), existedBooking.getStatus().name());
    }

    @Test
    void updateWrongItemTestByIncorrectUserId() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        BookingDto booking = bookingService.create(bookingDto, user.getId());

        assertThrows(NotFoundException.class, () -> bookingService.update(booking.getId(), user.getId(), true));
    }

    @Test
    void updateWrongItemTestByIncorrectBookingId() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        BookingDto booking = bookingService.create(bookingDto, user.getId());

        assertThrows(NotFoundException.class, () -> bookingService.update(booking.getId() + 999, user.getId(), true));
    }

    @Test
    void updateWrongItemTestByWaitingStatus() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        BookingDto booking = bookingService.create(bookingDto, user.getId());
        bookingService.update(booking.getId(), owner.getId(), true);

        assertThrows(BadRequestException.class, () -> bookingService.update(booking.getId(), owner.getId(), true));
    }

    @Test
    void getByIdItemTest() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        BookingDto booking = bookingService.create(bookingDto, user.getId());
        BookingDto existedBooking = bookingService.getById(booking.getId(), owner.getId());

        assertEquals(booking.getId(), existedBooking.getId());
    }

    @Test
    void getByIdWrongItemTestByIncorrectBookingId() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        BookingDto booking = bookingService.create(bookingDto, user.getId());
        assertThrows(NotFoundException.class, () -> bookingService.getById(booking.getId() + 999, owner.getId()));
    }

    @Test
    void getByIdWrongItemTestByIncorrectUserId() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        BookingDto booking = bookingService.create(bookingDto, user.getId());
        assertThrows(NotFoundException.class, () -> bookingService.getById(booking.getId(), owner.getId() + 999));
    }

    @Test
    void getAllByUserTestByAll() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        bookingService.create(bookingDto, user.getId());
        List<BookingDto> existedBooking = bookingService.getAllByUser(user.getId(), BookingState.ALL, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByUserTestByWaiting() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        bookingService.create(bookingDto, user.getId());
        List<BookingDto> existedBooking = bookingService.getAllByUser(user.getId(), BookingState.WAITING, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByUserTestByRejected() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        BookingDto booking = bookingService.create(bookingDto, user.getId());
        bookingService.update(booking.getId(), owner.getId(), false);

        List<BookingDto> existedBooking = bookingService.getAllByUser(user.getId(), BookingState.REJECTED, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByUserTestByCurrent() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        bookingService.create(bookingDto, user.getId());
        List<BookingDto> existedBooking = bookingService.getAllByUser(user.getId(), BookingState.CURRENT, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByUserTestByPast() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().minusDays(10));
        bookingDto.setEnd(LocalDateTime.now().minusDays(5));
        bookingService.create(bookingDto, user.getId());
        List<BookingDto> existedBooking = bookingService.getAllByUser(user.getId(), BookingState.PAST, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByUserTestByFuture() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(5));
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingService.create(bookingDto, user.getId());
        List<BookingDto> existedBooking = bookingService.getAllByUser(user.getId(), BookingState.FUTURE, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByUserTestByUnsupportedStatus() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(5));
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingService.create(bookingDto, user.getId());

        assertThrows(BadRequestException.class, () -> bookingService.getAllByUser(user.getId(), BookingState.UNSUPPORTED_STATUS, 1, 10));

    }

    @Test
    void getAllByUserTestByIncorrectFrom() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(5));
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingService.create(bookingDto, user.getId());

        assertThrows(BadRequestException.class, () -> bookingService.getAllByUser(user.getId(), BookingState.FUTURE, -1, 10));
    }

    @Test
    void getAllByUserTestByIncorrectUser() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(5));
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingService.create(bookingDto, user.getId());

        assertThrows(NotFoundException.class, () -> bookingService.getAllByUser(user.getId() + 999, BookingState.FUTURE, 1, 10));
    }

    @Test
    void getAllByOwnerTestByAll() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        bookingService.create(bookingDto, user.getId());
        List<BookingDto> existedBooking = bookingService.getAllByOwner(owner.getId(), BookingState.ALL, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByOwnerTestByWaiting() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        bookingService.create(bookingDto, user.getId());
        List<BookingDto> existedBooking = bookingService.getAllByOwner(owner.getId(), BookingState.WAITING, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByOwnerTestByRejected() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        BookingDto booking = bookingService.create(bookingDto, user.getId());
        bookingService.update(booking.getId(), owner.getId(), false);

        List<BookingDto> existedBooking = bookingService.getAllByOwner(owner.getId(), BookingState.REJECTED, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByOwnerTestByCurrent() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());

        bookingService.create(bookingDto, user.getId());
        List<BookingDto> existedBooking = bookingService.getAllByOwner(owner.getId(), BookingState.CURRENT, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByOwnerTestByPast() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().minusDays(10));
        bookingDto.setEnd(LocalDateTime.now().minusDays(5));
        bookingService.create(bookingDto, user.getId());
        List<BookingDto> existedBooking = bookingService.getAllByOwner(owner.getId(), BookingState.PAST, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByOwnerTestByFuture() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(5));
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingService.create(bookingDto, user.getId());
        List<BookingDto> existedBooking = bookingService.getAllByOwner(owner.getId(), BookingState.FUTURE, 1, 10);

        assertEquals(1, existedBooking.size());

    }

    @Test
    void getAllByOwnerTestByUnsupportedStatus() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(5));
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingService.create(bookingDto, user.getId());

        assertThrows(BadRequestException.class, () -> bookingService.getAllByOwner(owner.getId(), BookingState.UNSUPPORTED_STATUS, 1, 10));

    }

    @Test
    void getAllByOwnerTestByIncorrectFrom() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(5));
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingService.create(bookingDto, user.getId());

        assertThrows(BadRequestException.class, () -> bookingService.getAllByOwner(owner.getId(), BookingState.FUTURE, -1, 10));
    }

    @Test
    void getAllByOwnerTestByIncorrectUser() {
        UserDto owner = userService.create(firstUserDto);
        ItemDto item = itemService.create(itemDto, owner.getId());
        UserDto user = userService.create(secondUserDto);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(5));
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        bookingService.create(bookingDto, user.getId());

        assertThrows(NotFoundException.class, () -> bookingService.getAllByOwner(owner.getId() + 999, BookingState.FUTURE, 1, 10));
    }


}