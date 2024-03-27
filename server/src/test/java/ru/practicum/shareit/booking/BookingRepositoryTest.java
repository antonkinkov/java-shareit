package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class BookingRepositoryTest {

    private final LocalDateTime endDate = LocalDateTime.now().plusDays(10);
    private final LocalDateTime startDate = LocalDateTime.now();
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Booking booking;
    private User firstUser;
    private User secondUser;
    private Item item;

    @BeforeEach
    void init() {

        firstUser = User.builder()
                .name("Eric")
                .email("eric@gmail.com")
                .build();

        secondUser = User.builder()
                .name("George")
                .email("George@gmail.com")
                .build();

        item = Item.builder()
                .name("My item")
                .description("Very interesting item")
                .available(true)
                .owner(firstUser)
                .build();

        booking = Booking.builder()
                .end(endDate)
                .start(startDate)
                .item(item)
                .booker(secondUser)
                .status(BookingStatus.WAITING)
                .build();

    }

    @Test
    void findAllByBookerIdTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        bookingRepository.save(booking);
        long bookings = bookingRepository.findAllByBookerId(secondUser.getId(), Pageable.ofSize(10))
                .size();
        assertThat(bookings, equalTo(1L));

    }

    @Test
    void findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBeforeTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(secondUser.getId(),
                        item.getId(),
                        BookingStatus.APPROVED,
                        LocalDateTime.now().plusDays(100))
                .size();
        assertThat(bookings, equalTo(1L));

    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAscTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(secondUser.getId(),
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(5))
                .size();
        assertThat(bookings, equalTo(1L));
    }

    @Test
    void findAllByBookerIdAndEndBeforeTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByBookerIdAndEndBefore(secondUser.getId(),
                        LocalDateTime.now().plusDays(100),
                        Pageable.ofSize(10))
                .size();
        assertThat(bookings, equalTo(1L));
    }

    @Test
    void findAllByBookerIdAndStartAfterTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByBookerIdAndStartAfter(secondUser.getId(),
                        LocalDateTime.now().minusDays(1),
                        Pageable.ofSize(10))
                .size();
        assertThat(bookings, equalTo(1L));
    }

    @Test
    void findAllByBookerIdAndStatusEqualsTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByBookerIdAndStatusEquals(secondUser.getId(),
                        BookingStatus.APPROVED,
                        Pageable.ofSize(10))
                .size();
        assertThat(bookings, equalTo(1L));
    }

    @Test
    void findAllByItemOwnerIdTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByItemOwnerId(firstUser.getId(),
                        Pageable.ofSize(10))
                .size();
        assertThat(bookings, equalTo(1L));
    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAscTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(firstUser.getId(),
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(5))
                .size();
        assertThat(bookings, equalTo(1L));
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByItemOwnerIdAndEndBefore(firstUser.getId(),
                        LocalDateTime.now().plusDays(20),
                        Pageable.ofSize(10))
                .size();
        assertThat(bookings, equalTo(1L));
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByItemOwnerIdAndStartAfter(firstUser.getId(),
                        LocalDateTime.now().minusDays(1),
                        Pageable.ofSize(10))
                .size();
        assertThat(bookings, equalTo(1L));
    }

    @Test
    void findAllByItemOwnerIdAndStatusEqualsTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByItemOwnerIdAndStatusEquals(firstUser.getId(),
                        BookingStatus.APPROVED,
                        Pageable.ofSize(10))
                .size();
        assertThat(bookings, equalTo(1L));
    }

    @Test
    void findAllByItemIdInAndStartBeforeOrderByItemIdAscStartAscTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByItemIdInAndStartBeforeOrderByItemIdAscStartAsc(List.of(item.getId()),
                        LocalDateTime.now().plusDays(1))
                .size();
        assertThat(bookings, equalTo(1L));
    }

    @Test
    void findAllByItemIdAndStartAfterOrderByStartAscTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByItemIdAndStartAfterOrderByStartAsc(item.getId(),
                        LocalDateTime.now().minusDays(1))
                .size();
        assertThat(bookings, equalTo(1L));
    }

    @Test
    void findAllByItemIdAndStartBeforeOrderByStartDescTest() {
        userRepository.save(firstUser);
        itemRepository.save(item);
        userRepository.save(secondUser);
        bookingRepository.save(booking);
        long bookings = bookingRepository
                .findAllByItemIdAndStartBeforeOrderByStartDesc(item.getId(),
                        LocalDateTime.now().plusDays(1))
                .size();
        assertThat(bookings, equalTo(1L));
    }
}
