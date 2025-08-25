package com.chatproject.secure_chat.transport;

import com.chatproject.secure_chat.model.MsgFormat;


/**
 * "메시지를 밖으로 보내는 방법"을 약속하는 매우 간단한 인터페이스입니다.
 * 구현체 예: 콘솔 출력(ConsoleOutbound), 소켓 전송(SocketOutbound) 등.
 * 목적: ChatRoom이 전송 방식의 세부 구현에 묶이지 않도록(결합도↓).
 */
public interface Outbound {

    /**
     * 한 건의 메시지를 전송합니다.
     *
     * @param msg 보낼 메시지 (MsgFormat)
     */
    void send(MsgFormat msg);
}
