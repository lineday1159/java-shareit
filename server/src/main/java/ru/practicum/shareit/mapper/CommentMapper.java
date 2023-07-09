package ru.practicum.shareit.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static CommentDto commentToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    public static Comment commentDtoToComment(CommentDto commentDto, User author, Item item) {
        return new Comment(null, commentDto.getText(), item, author, commentDto.getCreated());
    }

    public static List<CommentDto> commentsToCommentsDto(Iterable<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(commentToCommentDto(comment));
        }
        return commentDtos;
    }
}
