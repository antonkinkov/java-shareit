package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerMockTest {

    private final LocalDateTime endDate = LocalDateTime.now().plusDays(10);
    private final LocalDateTime startDate = LocalDateTime.now().plusDays(1);
    private final String header = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    private BookingDto bookingDto;
    private UserDto firstUserDto;
    private UserDto secondUserDto;
    private ItemDto itemDto;
    private BookingShortDto bookingShortDto;

    @BeforeEach
    void init() {
        firstUserDto = UserDto.builder()
                .id(1L)
                .name("Eric")
                .email("eric@gmail.com")
                .build();

        secondUserDto = UserDto.builder()
                .id(2L)
                .name("George")
                .email("George@gmail.com")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("My item")
                .description("Very interesting item")
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(startDate)
                .end(endDate)
                .booker(UserMapper.toUser(secondUserDto))
                .item(itemDto)
                .build();

        bookingShortDto = BookingShortDto.builder()
                .start(startDate)
                .end(endDate)
                .itemId(itemDto.getId())
                .bookerId(secondUserDto.getId())
                .build();

    }

    @Test
    void createTest() throws Exception {
        bookingDto.setStatus(BookingStatus.WAITING);
        when(bookingService.create(any(BookingShortDto.class), anyLong())).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingShortDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

    }

    @Test
    void createInvalidTest() throws Exception {
        mvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void updateTest() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);
        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getByIdTest() throws Exception {
        bookingDto.setStatus(BookingStatus.WAITING);
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto);
        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getAllByUserTest() throws Exception {
        when(bookingService.getAllByUser(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        when(bookingService.getAllByUser(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllByUserWrongStateTest() throws Exception {
        when(bookingService.getAllByUser(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings?state=test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 2L))
                .andExpect(status().isBadRequest());

    }

}
