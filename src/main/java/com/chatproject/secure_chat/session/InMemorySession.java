package com.chatproject.secure_chat.session;

import com.chatproject.secure_chat.transport.Outbound;

/** 하드코딩 세션(사용자ID + 전달 채널) */
public class InMemorySession {
    public final String userId;
    public final Outbound outbound;

    public InMemorySession(String userId, Outbound outbound) {
        this.userId = userId;
        this.outbound = outbound;
    }
}
