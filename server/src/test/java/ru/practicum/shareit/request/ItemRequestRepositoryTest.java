package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private ItemRequest itemRequest;

    private User user;

    @BeforeEach
    void init() {

        user = User.builder()
                .email("email")
                .name("name")
                .build();

        itemRequest = ItemRequest.builder()
                .description("desc")
                .build();
    }


    @Test
    void findAllByUserIdOrderByCreationDateAscTest() {
        User createdUser = userRepository.save(user);
        itemRequest.setUser(createdUser);
        itemRequest.setCreationDate(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        List<ItemRequest> items = itemRequestRepository.findAllByUserIdOrderByCreationDateAsc(user.getId());

        assertThat(items.size(), equalTo(1));
    }

    @Test
    void findAllByUserNotLikeOrderByCreationDateAscTest() {
        User createdUser = userRepository.save(user);
        itemRequest.setUser(createdUser);
        itemRequest.setCreationDate(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        Long items1 = itemRequestRepository
                .findAllByUserNotLikeOrderByCreationDateAsc(createdUser, Pageable.ofSize(10)).stream().count();

        assertThat(items1, equalTo(0L));

        User user2 = userRepository.save(User.builder()
                .name("name")
                .email("newEmail@mail.ru")
                .build());

        Long items2 = itemRequestRepository
                .findAllByUserNotLikeOrderByCreationDateAsc(user2, Pageable.ofSize(10)).stream().count();

        assertThat(items2, equalTo(1L));
    }

}
