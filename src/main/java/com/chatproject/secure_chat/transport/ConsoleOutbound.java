package com.chatproject.secure_chat.transport;

import com.chatproject.secure_chat.model.MsgFormat;
import com.chatproject.secure_chat.shared.Jsons;

/**
 * 전송 구현 1: 콘솔에 JSON 한 줄(NDJSON)로 출력합니다.
 * <p>
 * - CUI 개발/테스트용 구현체로, 실제 전송 대신 표준 출력에 찍습니다.<br>
 * - 같은 인터페이스(Outbound)를 유지하면 나중에 Socket/WebSocket 구현으로 쉽게 교체 가능합니다.
 */
public class ConsoleOutbound implements Outbound {
    /** (선택) 수신자 식별용 라벨. 로깅/디버깅 편의를 위해   유지합니다. */
    private final String userId;

    /**
     * @param userId 이 출력 채널의 대상 사용자 라벨(예: "alice"). 로그 식별 용도입니다.
     */
    public ConsoleOutbound(String userId) {
        this.userId = userId;
    }

    /**
     * 메시지 1건을 콘솔에 JSON 문자열로 출력합니다.
     * <p>
     * 구현 상세:
     * <ul>
     *   <li>{@link Jsons#mapper()}로 객체를 JSON 문자열로 직렬화</li>
     *   <li>한 줄 = 한 메시지(NDJSON) 형태로 {@code System.out.println(...)} 출력</li>
     * </ul>
     *
     * @param msg 보낼 메시지(보통 정규화/검증을 통과한 객체)
     */
    @Override //send 오버라이딩
    public void send(MsgFormat msg) {
        try { //예외처리
            System.out.println(Jsons.mapper().writeValueAsString(msg));
        } catch (Exception e) {
            e.printStackTrace(); // 변환 실패 시: 개발 단계에선 스택트레이스 출력
        }
    }
}
