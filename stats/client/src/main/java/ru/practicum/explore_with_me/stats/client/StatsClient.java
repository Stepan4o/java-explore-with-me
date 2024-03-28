package ru.practicum.explore_with_me.stats.client;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.practicum.explore_with_me.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsClient {
    private final String serverUrl = "http://localhost:9090";
    private final RestTemplate rest;

    public StatsClient() {
        this.rest = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        rest.setRequestFactory(requestFactory);
    }

    public ResponseEntity<Object> saveHit(EndpointHitDto hitDto) {
        return rest.postForEntity(serverUrl + "/hit", hitDto, Object.class);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        HashMap<String, Object> params = new HashMap<>(
                Map.of(
                        "start", start,
                        "end", end,
                        "uris", uris,
                        "unique", unique
                ));
        return rest.getForEntity(serverUrl + "/stats", Object.class, params);
    }
}
