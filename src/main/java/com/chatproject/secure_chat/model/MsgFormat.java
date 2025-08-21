package com.chatproject.secure_chat.model;

/** MsgFormat v0: join/message/leave 최소 필드만 */
public class MsgFormat {
    public String type;   // "join", "message", "leave"
    public String roomId; // 방 ID
    public String from;   // 보낸 사람 ID
    public String body;   // message에서만 사용(기타 타입에서는 null)

    /** 아주 기본 유효성 검사 */
    public boolean isValid() {
        if (type == null || roomId == null || from == null) return false;
        if ("message".equals(type) && (body == null || body.isBlank())) return false;
        return true;
    }
}
