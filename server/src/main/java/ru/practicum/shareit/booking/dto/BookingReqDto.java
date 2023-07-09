package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingReqDto {
    private String start;
    private String end;
    private Long itemId;
}
