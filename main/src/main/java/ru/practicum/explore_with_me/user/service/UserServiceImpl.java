package ru.practicum.explore_with_me.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.user.dto.NewUserRequest;
import ru.practicum.explore_with_me.user.dto.UserDto;
import ru.practicum.explore_with_me.user.mapper.UserMapper;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.explore_with_me.utils.Const.ENTITY_NOT_FOUND;
import static ru.practicum.explore_with_me.utils.Const.USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto add(NewUserRequest userRequest) {
        User newUser = repository.save(UserMapper.toUser(userRequest));
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public List<UserDto> get(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (ids != null) {
            return repository.findAllByIdIn(ids).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return repository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void remove(long userId) {
        if (repository.existsById(userId)) repository.deleteById(userId);
        else throw new NotFoundException(
                String.format(ENTITY_NOT_FOUND, USER, userId)
        );
    }
}