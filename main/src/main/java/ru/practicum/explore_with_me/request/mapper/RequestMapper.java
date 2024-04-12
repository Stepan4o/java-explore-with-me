package ru.practicum.explore_with_me.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.model.ParticipationRequest;

@UtilityClass
public class RequestMapper {

    public ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(request.getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setStatus(request.getStatus());

        return requestDto;
    }

    public ParticipationRequestDto toDto(ParticipationRequest request) {
        ParticipationRequestDto res = new ParticipationRequestDto();
        res.setId(request.getId());
        res.setEvent(request.getEvent().getId());
        res.setCreated(request.getCreated());
        res.setRequester(request.getRequester().getId());
        res.setStatus(request.getStatus());

        return res;
    }
}
