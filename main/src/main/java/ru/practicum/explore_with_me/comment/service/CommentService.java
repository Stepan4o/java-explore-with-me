package ru.practicum.explore_with_me.comment.service;

import ru.practicum.explore_with_me.comment.dto.CommentDto;
import ru.practicum.explore_with_me.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(long userId, long eventId, NewCommentDto newDto);

    void deleteComment(long commentId);

    void deleteCommentByOwner(long userId, long eventId, long commentId);

    CommentDto editComment(long userId, long eventId, long commentId, NewCommentDto newCommentDto);

    CommentDto getCommentByOwnerId(long userId, long commentId);

    CommentDto getCommentById(long eventId, long commentId);

    List<CommentDto> getCommentsByEventId(long eventId, int from, int size);

    List<CommentDto> getAllCommentsByParam(long eventId, String text, int from, int size);
}
