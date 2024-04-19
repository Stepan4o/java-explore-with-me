package ru.practicum.explore_with_me.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore_with_me.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.explore_with_me.stats.dto.consts.Constants.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private UserShortDto author;
    private Long eventId;
    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime created;
    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime edited;
}
