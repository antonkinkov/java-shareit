package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {

    private final LocalDateTime endDate = LocalDateTime.now().plusDays(10);
    private final LocalDateTime startDate = LocalDateTime.now();

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private CommentRepository commentRepository;

    private User firstUser;
    private User secondUser;
    private Item item;
    private ItemRequest itemRequest;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void init() {
        booking = Booking.builder()
                .end(endDate)
                .start(startDate)
                .build();

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
                .build();

        comment = Comment.builder()
                .text("text")
                .build();

        itemRequest = ItemRequest.builder()
                .description("description")
                .build();

    }

    @Test
    void findAllByOwnerIdOrderByIdAscTest() {
        User owner = userRepository.save(firstUser);
        item.setOwner(owner);
        Item items = itemRepository.save(item);

        List<Item> item = itemRepository.findAllByOwnerIdOrderByIdAsc(owner.getId());

        assertEquals(1, item.size());
    }

    @Test
    void searchTest() {
        User owner = userRepository.save(firstUser);
        item.setOwner(owner);
        Item items = itemRepository.save(item);

        assertEquals(1, itemRepository.search("interesting").size());
    }

    @Test
    void findByRequestIdTest() {
        User user = userRepository.save(firstUser);
        itemRequest.setCreationDate(LocalDateTime.now());
        itemRequest.setUser(user);

        ItemRequest request = itemRequestRepository.save(itemRequest);
        item.setOwner(user);
        item.setRequest(request);
        Item items = itemRepository.save(item);
        assertEquals(1, itemRepository.findByRequestId(user.getId()).size());

    }
}
