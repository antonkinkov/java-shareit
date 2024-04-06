package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    private final LocalDateTime endDate = LocalDateTime.of(2024, 4, 10, 12, 12, 1);
    private final LocalDateTime startDate = LocalDateTime.of(2024, 3, 10, 12, 12, 1);

    @Autowired
    private JacksonTester<BookingDto> json;

    private BookingDto bookingDto;

    @BeforeEach
    void init() {
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(startDate)
                .end(endDate)
                .build();
    }

    @Test
    void testBookingDto() throws IOException {
        JsonContent<BookingDto> content = json.write(bookingDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.start")
                .isEqualTo(LocalDateTime.of(2024, 3, 10, 12, 12, 1).toString());
        assertThat(content).extractingJsonPathStringValue("$.end")
                .isEqualTo(LocalDateTime.of(2024, 4, 10, 12, 12, 1).toString());
    }
}
