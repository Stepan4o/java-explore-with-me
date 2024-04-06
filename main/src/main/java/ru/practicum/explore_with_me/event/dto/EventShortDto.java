package ru.practicum.explore_with_me.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private Long id;
    private CategoryDto category;
    private Long confirmedRequests;
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
