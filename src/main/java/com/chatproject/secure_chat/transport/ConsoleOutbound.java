package com.chatproject.secure_chat.transport;

import com.chatproject.secure_chat.model.MsgFormat;
import com.chatproject.secure_chat.shared.Jsons;

/**
 * 전송 구현 1: 콘솔로 JSON 한 줄을 찍어서 "보낸 것처럼" 동작.
 * 나중에 소켓 전송기로 쉽게 교체할 수 있게 Outbound 인터페이스를 구현한다.
 */
public class ConsoleOutbound implements Outbound {
    private final String userId; // 수신자 표시용

    public ConsoleOutbound(String userId) {
        this.userId = userId;
    }

    @Override
    public void send(MsgFormat msg) {
        try {
            // NDJSON 스타일: 한 줄에 하나의 JSON
            System.out.println(Jsons.mapper().writeValueAsString(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
