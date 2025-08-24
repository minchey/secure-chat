package com.chatproject.secure_chat.session;

import com.chatproject.secure_chat.transport.Outbound;

/**
 * 유저 1명의 좌석표(연결 정보).
 * userId: 누구인지
 * outbound: 어디로/어떻게 보낼지(콘솔, 소켓 등)
 */
public class InMemorySession {
    public final String userId;
    public final Outbound outbound;

    /**
     * @param userId   유저 아이디(예: "alice")
     * @param outbound 전송 방법(예: ConsoleOutbound)
     */
    public InMemorySession(String userId, Outbound outbound) {
        this.userId = userId;
        this.outbound = outbound;
    }
}
