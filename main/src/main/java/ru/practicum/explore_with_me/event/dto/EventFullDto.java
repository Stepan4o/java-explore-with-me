package ru.practicum.explore_with_me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.event.model.State;
import ru.practicum.explore_with_me.location.Location;
import ru.practicum.explore_with_me.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.explore_with_me.stats.dto.consts.Constants.TIME_PATTERN;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private State state;
    private String title;
    private Long views;
}
