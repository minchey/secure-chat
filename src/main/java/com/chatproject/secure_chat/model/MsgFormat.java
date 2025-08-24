package com.chatproject.secure_chat.model;

import java.time.Instant;

/**
 * 채팅 메시지 1건의 데이터.
 * type: "join" | "message" | "leave"
 * roomId/from은 필수, "message"일 땐 body도 필요.
 * ts/msgId는 입력이 없어도 서버가 자동 채움.
 */

public class MsgFormat {
    public String type;   // "join", "message", "leave"
    public String roomId; // 방 ID
    public String from;   // 보낸 사람 ID
    public String body;   // 본문 내용 message에서만 사용(기타 타입에서는 null)
    public Instant ts;     // 전송/생성 시각(UTC, ISO-8601 문자열로 입출력) 나중에 정렬/히스토리에 유용
    public String msgId;

    /** 필수값 검사. "message"면 body도 필요. */
    public boolean isValid() {
        if (type == null || roomId == null || from == null) return false;
        if ("message".equals(type) && (body == null || body.isBlank())) return false;
        return true;
    }
}
