package com.chatproject.secure_chat.model;

import java.time.Instant;

/** MsgFormat v1: join/message/leave + ts(UTC) */
public class MsgFormat {
    public String type;   // "join", "message", "leave"
    public String roomId; // 방 ID
    public String from;   // 보낸 사람 ID
    public String body;   // 본문 내용 message에서만 사용(기타 타입에서는 null)
    public Instant ts;     // 전송/생성 시각(UTC, ISO-8601 문자열로 입출력)


    /** 아주 기본 유효성 검사 */
    public boolean isValid() {
        if (type == null || roomId == null || from == null) return false;
        if ("message".equals(type) && (body == null || body.isBlank())) return false;
        return true;
    }
}
