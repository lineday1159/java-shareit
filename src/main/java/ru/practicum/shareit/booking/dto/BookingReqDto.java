package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class BookingReqDto {
    @NotNull
    private String start;
    @NotNull
    private String end;
    private Long itemId;
}
