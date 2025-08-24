package com.chatproject.secure_chat.transport;

import com.chatproject.secure_chat.session.InMemorySession;
import com.chatproject.secure_chat.model.MsgFormat;
import com.chatproject.secure_chat.shared.MessageNormalizer;
public class ConsoleTest {
    public static void main(String[] args) {

        // 0) 전송기
        ConsoleOutbound out = new ConsoleOutbound("alice");

        // 0.5) ✅ 세션 만들어서 전송기 꽂기
        InMemorySession s = new InMemorySession("alice", out);

        // 1) 메시지
        MsgFormat m = new MsgFormat();
        m.type = "message";
        m.roomId = "room-1";
        m.from = "alice";
        m.body = "세션 테스트!";
        MessageNormalizer.normalize(m);

        // 바로 출력해보기
        s.outbound.send(m);

    }
}