package ru.practicum.explore_with_me.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.comment.dto.CommentDto;
import ru.practicum.explore_with_me.comment.service.CommentService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentControllerPublic {
    private final CommentService commentService;

    @GetMapping("/{eventId}/{commentId}")
    public CommentDto getCommentById(
            @PathVariable @Min(1) Long eventId,
            @PathVariable @Min(1) Long commentId
    ) {
        log.debug("/comments/{}/{}", eventId, commentId);
        return commentService.getCommentById(eventId, commentId);
    }

    @GetMapping("/{eventId}")
    public List<CommentDto> getCommentsByEventId(
            @PathVariable @Min(1) Long eventId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size
    ) {
        log.debug("GET: /comments/{}?from={}&size={}", eventId, from, size);
        return commentService.getCommentsByEventId(eventId, from, size);
    }

    @GetMapping("/{eventId}/search")
    public List<CommentDto> getAllCommentsByParams(
            @PathVariable @Min(1) Long eventId,
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size
    ) {
        log.debug("GET: /comments/{}/search?text={}&from={}&size={}", eventId, text, from, size);
        return commentService.getAllCommentsByParam(eventId, text, from, size);
    }
}
