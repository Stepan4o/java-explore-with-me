package ru.practicum.explore_with_me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore_with_me.location.LocationDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.explore_with_me.stats.dto.consts.Constants.TIME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = TIME_PATTERN)
    @Future
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @Min(0)
    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120)
    private String title;
}