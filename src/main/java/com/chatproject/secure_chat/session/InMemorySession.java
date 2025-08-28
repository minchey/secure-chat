package com.chatproject.secure_chat.session;

import com.chatproject.secure_chat.transport.Outbound;

/**
 * 한 명의 사용자를 대표하는 "세션 카드".
 * <p>
 * - 누가({@link #userId})인지<br>
 * - 어떤 전송 경로({@link #outbound})로 메시지를 보낼지<br>
 * <p>
 * 두 필드는 {@code final}이라 생성 후 변경되지 않는 불변 설계입니다.
 */
public class InMemorySession {
    /** 사용자 ID(예: "alice"). */
    public final String userId;

    /** 메시지 전송 경로(예: ConsoleOutbound, 추후 SocketOutbound). */
    public final Outbound outbound;

    /**
     * 세션 카드를 생성합니다.
     *
     * @param userId   사용자 ID(널/빈 문자열은 지양)
     * @param outbound 전송 경로 구현체(널이 아니어야 함)
     */
    public InMemorySession(String userId, Outbound outbound) {
        this.userId = userId;
        this.outbound = outbound;
    }
}
