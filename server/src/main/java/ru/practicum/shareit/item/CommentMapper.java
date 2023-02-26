package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthorName(),
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentCreateDto commentDto, User user, Long itemId) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthorName(user.getName());
        comment.setCreated(LocalDateTime.now());
        comment.setItemId(itemId);
        return comment;
    }
}
