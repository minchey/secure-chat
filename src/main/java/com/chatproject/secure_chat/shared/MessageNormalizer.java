package com.chatproject.secure_chat.shared;

import com.chatproject.secure_chat.model.MsgFormat;
import java.time.Instant;

public final class MessageNormalizer {
    private MessageNormalizer() {}

    /** 입력 메시지 표준화 + ts 자동 생성 */
    public static MsgFormat normalize(MsgFormat m) {
        if (m.type   != null) m.type   = m.type.trim().toLowerCase();
        if (m.roomId != null) m.roomId = m.roomId.trim();
        if (m.from   != null) m.from   = m.from.trim();
        if ("message".equals(m.type) && m.body != null) m.body = m.body.strip();

        if (m.ts == null) m.ts = Instant.now();  // 🔹 ts 자동 생성(UTC)
        return m;
    }
}
