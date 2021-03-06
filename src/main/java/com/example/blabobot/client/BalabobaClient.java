package com.example.blabobot.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Slf4j
@Service
public class BalabobaClient {

    private final String balabobaUrl;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public BalabobaClient(@Value("${balaboba-url}") String balabobaUrl, ObjectMapper objectMapper) {
        this.balabobaUrl = balabobaUrl;
        this.objectMapper = objectMapper;

        webClient = WebClient.builder().build();
    }


    public String callBalaboba(String message, int type) {

        String body = String.format("{\"query\":\"%s\",\"intro\":%d,\"filter\":1}", message, type);

        String response = Objects.requireNonNull(webClient.post()
                .uri(balabobaUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .block()).getBody();

        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            log.error("Error parsing base response: {}", response);
            e.printStackTrace();
        }

        String s = Objects.requireNonNull(jsonNode).get("text").asText();
        if (s == null || s.isEmpty()) {
            s = "На это мне нечего ответить.";
        }
        return s;
    }
}
