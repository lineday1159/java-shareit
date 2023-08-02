package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestWithItemDto {
    private Long id;
    private String description;
    private Date created;
    private List<ItemDto> items;
}
