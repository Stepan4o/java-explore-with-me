package ru.practicum.explore_with_me.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.comment.dto.CommentDto;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.user.mapper.UserMapper;

@UtilityClass
public class CommentMapper {

    public CommentDto toDto(Comment comment, long eventId) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthor(UserMapper.toUserShortDto(comment.getAuthor()));
        commentDto.setEventId(eventId);
        commentDto.setCreated(comment.getCreated());
        commentDto.setEdited(comment.getEdited());

        return commentDto;
    }
}
