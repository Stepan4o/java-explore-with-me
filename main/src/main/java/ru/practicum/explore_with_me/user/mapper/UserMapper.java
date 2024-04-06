package ru.practicum.explore_with_me.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.user.dto.NewUserRequest;
import ru.practicum.explore_with_me.user.dto.UserDto;
import ru.practicum.explore_with_me.user.dto.UserShortDto;
import ru.practicum.explore_with_me.user.model.User;

@UtilityClass
public class UserMapper {

    public User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public UserShortDto toUserShortDto(User user) {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());

        return userShortDto;
    }

    public User toUser(NewUserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());

        return user;
    }

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }
}
