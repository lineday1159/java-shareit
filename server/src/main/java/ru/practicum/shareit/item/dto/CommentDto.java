package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private Date created;
}
