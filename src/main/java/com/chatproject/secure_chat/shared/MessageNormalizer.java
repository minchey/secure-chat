package com.chatproject.secure_chat.shared;

import com.chatproject.secure_chat.model.MsgFormat;
import java.time.Instant;
import java.util.UUID;

/**
 * 들어온 메시지를 표준 형태로 "정리"해 주는 유틸 클래스입니다.
 * - type/roomId/from 공백 제거, type은 소문자로 통일
 * - type이 "message"일 때 body 앞뒤 공백 제거
 * - ts(시간)와 msgId(고유번호)가 비어 있으면 자동 생성

 * 사용 이유: 입력이 제각각이어도 서버 안에서는 항상 같은 형태로 다루기 위해.
 */

public final class MessageNormalizer {
    private MessageNormalizer() {} //유틸클래스: 생성불가


    /**
     * 메시지 값을 표준으로 정리하고, 누락된 ts/msgId를 채웁니다.
     *
     * @param m 정리할 메시지 객체(입력). null이면 null을 그대로 반환합니다.
     * @return 같은 객체를 정리해서 반환(입력 객체를 직접 수정).

     * 정리 규칙:
     * 1) type/roomId/from: trim() 적용, type은 toLowerCase()
     * 2) type=="message" 이고 body가 있으면 strip() 적용
     * 3) ts==null 이면 Instant.now() 설정
     * 4) msgId==null 또는 공백이면 UUID 생성
     */
    public static MsgFormat normalize(MsgFormat m) {
        if (m== null) return null;

        // 1) 공백 제거 + type 소문자 통일
        // trim = 공백제거  toLowerCase = 소문자로
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
