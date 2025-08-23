package com.chatproject.secure_chat.transport;

import com.chatproject.secure_chat.model.MsgFormat;
import com.chatproject.secure_chat.shared.Jsons;

/** 테스트용: 브로드캐스트 결과를 콘솔(JSON 한 줄)로 출력 */
public class ConsoleOutbound implements Outbound {
    private final String userId; // 수신자 식별(로그용)

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
