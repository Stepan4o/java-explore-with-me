package ru.practicum.explore_with_me.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

import static ru.practicum.explore_with_me.stats.dto.consts.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {

    @NotBlank(message = NOT_NULL_OR_EMPTY)
    private String app;

    @NotBlank(message = NOT_NULL_OR_EMPTY)
    private String uri;

    @NotBlank(message = NOT_NULL_OR_EMPTY)
    private String ip;

    @NotNull(message = NOT_NULL)
    @Past(message = PAST_ONLY)
    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime timestamp;
}
