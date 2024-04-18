package ru.practicum.explore_with_me.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.comment.dto.CommentDto;
import ru.practicum.explore_with_me.comment.dto.NewCommentDto;
import ru.practicum.explore_with_me.comment.mapper.CommentMapper;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.comment.repository.CommentRepository;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.explore_with_me.event.model.State.PUBLISHED;
import static ru.practicum.explore_with_me.utils.Const.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    @Override
    public CommentDto addComment(long userId, long eventId, NewCommentDto newDto) {
        User savedUser = getUserIfExists(userId);
        Event savedEvent = getEventIfExists(eventId);
        if (savedEvent.getState() == PUBLISHED) {
            Comment newComment = new Comment(
                    newDto.getText(),
                    savedUser,
                    LocalDateTime.now(),
                    savedEvent
            );
            return CommentMapper.toDto(commentRepository.save(newComment), savedEvent.getId());
        } else {
            throw new ConflictException("Comment can be added to published event only");
        }
    }

    @Override
    public void deleteComment(long commentId) {
        checkExistsComment(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteCommentByOwner(long userId, long eventId, long commentId) {
        Comment savedComment = getCommentIfExists(commentId);
        User owner = getUserIfExists(userId);
        if (Objects.equals(savedComment.getAuthor().getId(), owner.getId())) {
            commentRepository.delete(savedComment);
        } else {
            throw new ConflictException(OWNER_ONLY);
        }
    }

    @Override
    public CommentDto editComment(long userId, long eventId, long commentId, NewCommentDto newCommentDto) {
        User owner = getUserIfExists(userId);
        Event savedEvent = getEventIfExists(eventId);
        Comment savedComment = commentRepository.findByIdAndEventId(commentId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, COMMENT, commentId
                )));
        if (Objects.equals(owner.getId(), savedComment.getAuthor().getId())) {
            savedComment.setText(newCommentDto.getText());
            savedComment.setEdited(LocalDateTime.now());
            return CommentMapper.toDto(commentRepository.save(savedComment), savedEvent.getId());
        }
        throw new ConflictException(OWNER_ONLY);
    }

    @Override
    public CommentDto getCommentByOwnerId(long userId, long commentId) {
        User owner = getUserIfExists(userId);
        Comment savedComment = commentRepository.findByIdAndAuthorId(commentId, owner.getId()).
                orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, COMMENT, commentId
                )));
        return CommentMapper.toDto(savedComment, savedComment.getEvent().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(long eventId, long commentId) {
        Event savedEvent = getEventIfExists(eventId);
        Comment savedComment = commentRepository.findByIdAndEventId(commentId, savedEvent.getId()).orElseThrow(
                () -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, COMMENT, commentId
                ))
        );
        return CommentMapper.toDto(savedComment, savedComment.getId());
    }

    @Override
    public List<CommentDto> getCommentsByEventId(long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Event savedEvent = getEventIfExists(eventId);

        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(x -> CommentMapper.toDto(x, savedEvent.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsByParam(long eventId, String text, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Event savedEvent = getEventIfExists(eventId);

        if (text != null && !text.isBlank())
            return commentRepository.searchByText(text.toLowerCase(), eventId, pageable).stream()
                    .map(x -> CommentMapper.toDto(x, savedEvent.getId()))
                    .collect(Collectors.toList());

        else return List.of();
    }

    private Comment getCommentIfExists(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ENTITY_NOT_FOUND, COMMENT, commentId)
                ));
    }

    private void checkExistsComment(long commentId) {
        if (!commentRepository.existsById(commentId))
            throw new NotFoundException(String.format(
                    String.format(ENTITY_NOT_FOUND, COMMENT, commentId)
            ));
    }

    private User getUserIfExists(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, USER, userId
                ))
        );
    }

    private Event getEventIfExists(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, EVENT, eventId
                )));
    }
}
