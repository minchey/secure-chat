package com.chatproject.secure_chat.transport;

import com.chatproject.secure_chat.model.MsgFormat;

/**
 * "메시지를 밖으로 보내는 방법"을 약속하는 간단한 인터페이스입니다.
 * - 지금은 콘솔 출력용 구현(ConsoleOutbound)을 다음 단계에서 만듭니다.
 * - 나중엔 소켓/웹소켓 전송 구현으로 쉽게 교체할 수 있게 합니다.
 */
public interface Outbound {

    /**
     * 한 건의 메시지를 전송합니다.
     *
     * @param msg 보낼 메시지 (MsgFormat)
     */
    void send(MsgFormat msg);
}
