package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerMockTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("desc")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();
    }

    @SneakyThrows
    @Test
    void createTest() {
        when(itemRequestService.create(any(ItemRequestDto.class), anyLong())).thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", "1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @Test
    @SneakyThrows
    public void getAllByUserTest() {
        when(itemRequestService.getAllByUser(1L)).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void getAllTest() {

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .param("from", "1")
                        .header("X-Sharer-User-Id", "1")
                        .param("size", "1"))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getRequestByIdTest() {
        long requestId = 0L;
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getRequestById(anyLong(), anyLong());
    }

}
