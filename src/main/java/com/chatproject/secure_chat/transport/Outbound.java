package com.chatproject.secure_chat.transport;

import com.chatproject.secure_chat.model.MsgFormat;

/** 메시지를 바깥으로 내보내는 추상 채널 (콘솔/소켓/WebSocket로 교체 가능) */
public interface Outbound {
    void send(MsgFormat msg);
}
