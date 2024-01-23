package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserDto {
        @NotNull
        private long id;
        private String name;
        @Email
        @NotBlank
        private String email;
}
