package com.chatproject.secure_chat.chat;

import com.chatproject.secure_chat.model.MsgFormat;
import com.chatproject.secure_chat.session.InMemorySession;

import java.time.Instant;
import java.util.*;

/**
 * 단일 채팅방 도메인.
 * - joinHardcoded(userId): 사전 레지스트리에서 세션 찾아 입장
 * - post(msg): 방 멤버에게 브로드캐스트
 * - leave(userId): 퇴장 브로드캐스트
 */
public class ChatRoom {
    private final String roomId;

    /** 현재 방에 입장해 있는 멤버들 (userId -> session) */
    private final Map<String, InMemorySession> members = new HashMap<>();

    /** 하드코딩 세션 레지스트리 (userId -> session) : 생성자 주입 */
    private final Map<String, InMemorySession> registry;

    /** (선택) 간단한 히스토리 저장 */
    private final List<MsgFormat> history = new ArrayList<>();

    public ChatRoom(String roomId, Map<String, InMemorySession> sessionRegistry) {
        this.roomId = roomId;
        this.registry = sessionRegistry;
    }

    /** 하드코딩 레지스트리에서 세션 찾아 입장시키기 */
    public synchronized void joinHardcoded(String userId) {
        InMemorySession s = registry.get(userId);
        if (s == null) return; // 등록되지 않은 유저는 무시
        if (members.containsKey(userId)) return; // 이미 입장한 경우 무시
        members.put(userId, s);
        broadcast(systemMsg(userId + " joined"));
    }

    /** 퇴장 */
    public synchronized void leave(String userId) {
        if (members.remove(userId) != null) {
            broadcast(systemMsg(userId + " left"));
        }
    }

    /** 메시지 브로드캐스트 (방 불일치/미입장 방어 포함) */
    public synchronized void post(MsgFormat msg) {
        if (!roomId.equals(msg.roomId)) return;         // 방 불일치 방어
        if (!members.containsKey(msg.from)) return;     // 미입장 사용자는 무시
        history.add(msg);                                // (옵션) 히스토리 보관
        broadcast(msg);
    }

    /** (옵션) 읽기 전용 히스토리 */
    public List<MsgFormat> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /** 내부 브로드캐스트 */
    private void broadcast(MsgFormat msg) {
        for (InMemorySession s : members.values()) {
            s.outbound.send(msg);
        }
    }

    /** 시스템 메시지 생성 (항상 ts 채우기) */
    private MsgFormat systemMsg(String body) {
        MsgFormat m = new MsgFormat();
        m.type = "system";
        m.roomId = roomId;
        m.from = "system";
        m.body = body;
        m.ts = Instant.now(); // 서버 기준 시간
        return m;
    }
}
