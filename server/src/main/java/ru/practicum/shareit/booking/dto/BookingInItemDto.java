package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingInItemDto {
    @Id
    private Long id;
    @Column(name = "booker_id")
    private Long bookerId;
}
