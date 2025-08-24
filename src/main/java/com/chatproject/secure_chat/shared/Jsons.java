package com.chatproject.secure_chat.shared;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class Jsons {
    private static final ObjectMapper M = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // Instant 등 java.time
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // 시간 형식은 ISO-8601
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); //모르는 키 무시

    private Jsons() {}
    public static ObjectMapper mapper() { return M; }
}
