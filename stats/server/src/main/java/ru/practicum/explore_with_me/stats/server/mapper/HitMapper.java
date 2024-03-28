package ru.practicum.explore_with_me.stats.server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.stats.dto.EndpointHitDto;
import ru.practicum.explore_with_me.stats.server.model.EndpointHit;

@UtilityClass
public class HitMapper {

    public EndpointHit toHit(EndpointHitDto hitDto) {
        EndpointHit hit = new EndpointHit();
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(hitDto.getTimestamp());
        return hit;
    }
}
