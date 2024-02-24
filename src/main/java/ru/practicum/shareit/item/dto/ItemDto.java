package ru.practicum.shareit.item.dto;


import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private ItemRequest request;

    private BookingShortDto nextBooking;
    private BookingShortDto lastBooking;
    private List<CommentDto> comments;
}
