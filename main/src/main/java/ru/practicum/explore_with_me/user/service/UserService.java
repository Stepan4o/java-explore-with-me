package ru.practicum.explore_with_me.user.service;

import ru.practicum.explore_with_me.user.dto.NewUserRequest;
import ru.practicum.explore_with_me.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(NewUserRequest userRequest);

    List<UserDto> get(List<Long> ids, Integer from, Integer size);

    void remove(long userId);
}
