package ru.practicum.explore_with_me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore_with_me.location.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.explore_with_me.stats.dto.consts.Constants.TIME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime eventDate;

    //    @NotNull
    private LocationDto location;

    private boolean paid = false;

    private int participantLimit = 0;

    private boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
