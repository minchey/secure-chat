package com.chatproject.secure_chat.shared;

import com.chatproject.secure_chat.model.MsgFormat;
import java.time.Instant;
import java.util.UUID;

/**
 * 들어온 메시지를 표준 형태로 보정
 * - type/roomId/from의 공백 제거, type은 소문자로 통일
 * - message 타입이면 body의 앞뒤 공백 제거
 * - ts(시간)와 msgId가 비어 있으면 자동 생성
 */

public final class MessageNormalizer {
    private MessageNormalizer() {} //유틸클래스: 생성불가


    /**
     * 메시지 값을 표준으로 정리하고 누락된 ts/msgId를 채웁니다
     *
     * @param m 정리할 메시지(입력 객체)
     * @return 정리된 같은 메시지 객체(입력 객체를 직접 수정)
     */
    public static MsgFormat normalize(MsgFormat m) {
        if (m== null) return null;

        // 1) 공백 제거 + type 소문자 통일
        if (m.type   != null) m.type   = m.type.trim().toLowerCase();
        if (m.roomId != null) m.roomId = m.roomId.trim();
        if (m.from   != null) m.from   = m.from.trim();

        // 2) message 타입이면 body도 다듬기
        if ("message".equals(m.type) && m.body != null) m.body = m.body.strip();

        // 3) 시간 자동 생성 (없을 때만)
        if (m.ts == null) m.ts = Instant.now();

        // 4) 고유 ID 자동 생성 (없을 때만)
        if (m.msgId == null || m.msgId.isBlank()){
            m.msgId = UUID.randomUUID().toString();
        }
        return m;
    }
}
