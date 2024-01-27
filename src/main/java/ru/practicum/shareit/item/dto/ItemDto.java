package ru.practicum.shareit.item.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotNull
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private String request;

}
