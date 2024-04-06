package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {
    @Test
    void toItemShortDtoTest() {
        Item item = Item.builder()
                .id(1L)
                .name("My item")
                .description("Very interesting item")
                .available(true)
                .request(ItemRequest.builder().id(2L).build())
                .owner(User.builder().id(3L).build())
                .build();

        ItemShortDto itemShortDto = ItemMapper.toItemShortDto(item);
        assertEquals(Long.valueOf(1L), itemShortDto.getId());

    }

}
