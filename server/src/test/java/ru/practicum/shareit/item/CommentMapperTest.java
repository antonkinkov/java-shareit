package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {

    @Test
    void toCommentDtoTest() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Text")
                .created(LocalDateTime.now())
                .author(User.builder().id(5L).name("Name").build())
                .build();

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertEquals(Long.valueOf(1L), commentDto.getId());
    }
}
