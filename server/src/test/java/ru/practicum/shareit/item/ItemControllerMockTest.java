package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerMockTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    private CommentDto commentDto;

    @BeforeEach
    void init() {

        commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .build();
    }

    @SneakyThrows
    @Test
    void createTest() {
        ItemDto itemToCreate = ItemDto.builder()
                .name("My item")
                .description("Very interesting item")
                .available(true)
                .build();
        when(itemService.create(any(ItemDto.class), anyLong())).thenReturn(itemToCreate);

        String result = mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemToCreate), result);
    }

    @SneakyThrows
    @Test
    void updateTest() {
        ItemDto itemDtoToUpdate = ItemDto.builder()
                .name("updName")
                .description("updDesc")
                .available(false)
                .build();

        ItemDto itemDtoExpected = ItemDto.builder()
                .id(1L)
                .name("updName")
                .description("updDesc")
                .available(false)
                .build();
        when(itemService.update(anyLong(), any(ItemDto.class), anyLong())).thenReturn(itemDtoExpected);

        String result = mockMvc.perform(patch("/items/{itemId}", itemDtoExpected.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDtoToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoExpected), result);
    }

    @Test
    @SneakyThrows
    public void getByIdTest() {

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .build();

        when(itemService.getById(1L, 1L)).thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void getAllTest() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .build();

        when(itemService.getAll(1L)).thenReturn(List.of(itemDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @SneakyThrows
    @Test
    void searchTest() {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text='name'")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemService, times(1)).search(anyString());
    }

    @SneakyThrows
    @Test
    void createCommentTest() {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);
        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }


    @SneakyThrows
    @Test
    void createNotFoundExceptionTest() {
        ItemDto itemToCreate = ItemDto.builder()
                .name("My item")
                .description("Very interesting item")
                .available(true)
                .build();
        when(itemService.create(any(ItemDto.class), anyLong())).thenThrow(new NotFoundException("Объект не найден"));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void createBadRequestExceptionTest() {
        ItemDto itemToCreate = ItemDto.builder()
                .name("My item")
                .available(true)
                .build();
        when(itemService.create(any(ItemDto.class), anyLong())).thenThrow(new BadRequestException("Некорректный запрос"));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isBadRequest());
    }

}