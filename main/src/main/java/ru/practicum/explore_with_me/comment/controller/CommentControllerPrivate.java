package ru.practicum.explore_with_me.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.comment.dto.CommentDto;
import ru.practicum.explore_with_me.comment.dto.NewCommentDto;
import ru.practicum.explore_with_me.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class CommentControllerPrivate {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createNewComment(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long eventId,
            @RequestBody @Valid NewCommentDto newCommentDto
    ) {
        log.debug("POST: /users/{}/comments/{}", userId, eventId);
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentByOwnerId(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long commentId
    ) {
        log.debug("GET: /users/{}/comments/{}", userId, commentId);
        return commentService.getCommentByOwnerId(userId, commentId);
    }

    @PatchMapping("/{eventId}/{commentId}")
    public CommentDto editComment(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long eventId,
            @PathVariable @Min(1) Long commentId,
            @RequestBody @Valid NewCommentDto newCommentDto
    ) {
        log.info("PATCH: /users/{}/comments/{}/{}", userId, eventId, commentId);
        return commentService.editComment(userId, eventId, commentId, newCommentDto);
    }

    @DeleteMapping("/{eventId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByOwner(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long eventId,
            @PathVariable @Min(1) Long commentId
    ) {
        log.debug("DELETE: /users/{}/comments/{}/{}", userId, eventId, commentId);
        commentService.deleteCommentByOwner(userId, eventId, commentId);
    }
}
