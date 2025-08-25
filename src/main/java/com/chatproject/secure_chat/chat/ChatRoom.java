package com.chatproject.secure_chat.chat;

import com.chatproject.secure_chat.model.MsgFormat;
import com.chatproject.secure_chat.session.InMemorySession;

import java.time.Instant;
import java.util.*;

/**
 * 채팅방 한 개를 관리합니다.
 * - 누가 들어왔는지 기억하고(join/leave)
 * - 방 안 사람 모두에게 메시지를 뿌립니다(post)
 */
public class ChatRoom {
    private final String roomId;                                   // 이 방의 이름/ID
    private final Map<String, InMemorySession> registry;            // 미리 등록된 유저 좌석표(userId -> session)
    private final Map<String, InMemorySession> members = new HashMap<>(); // 현재 방에 들어온 사람들
    private final List<MsgFormat> history = new ArrayList<>();      // 간단 히스토리

    /**
     * @param roomId   방 ID (예: "room-1")
     * @param registry 하드코딩된 세션 목록(userId -> session)
     */
    public ChatRoom(String roomId, Map<String, InMemorySession> registry) {
        this.roomId = roomId;
        this.registry = registry;
    }

    /** 하드코딩 등록표에서 유저를 찾아 이 방에 입장시킵니다. */
    public synchronized void joinHardcoded(String userId) {
        InMemorySession s = registry.get(userId);
        if (s == null) return;               // 등록 안 되어 있으면 무시
        if (members.containsKey(userId)) return; // 이미 들어와 있으면 무시
        members.put(userId, s);
        broadcast(systemMsg(userId + " joined"));
    }

    /** 방에서 내보냅니다(있을 때만). */
    public synchronized void leave(String userId) {
        if (members.remove(userId) != null) {
            broadcast(systemMsg(userId + " left"));
        }
    }

    /**
     * 한 메시지를 방 사람 모두에게 뿌립니다.
     * - 보낸 사람이 이 방에 들어와 있어야 함
     * - 메시지의 roomId가 이 방과 같아야 함
     */
    public synchronized void post(MsgFormat msg) {
        if (!roomId.equals(msg.roomId)) return;        // 다른 방으로 온 메시지면 무시
        if (!members.containsKey(msg.from)) return;    // 입장하지 않은 사람이면 무시
        history.add(msg);                              // 히스토리에 저장
        broadcast(msg);
    }

    /**방안 멤법 리스트 출력 */
    public synchronized void sendMembersTo(String requesterId) {
        // 요청자가 방에 들어와 있는지 확인(입장 안 했으면 무시)
        var session = members.get(requesterId);
        if (session == null) return;

        // 현재 멤버 목록을 문자열로 만들기
        String list = String.join(", ", members.keySet());
        if (list.isBlank()) list = "(none)";

        // 한 줄짜리 시스템 메시지로 요청자에게만 보내기
        MsgFormat m = new MsgFormat();
        m.type = "system";
        m.roomId = roomId;
        m.from = "system";
        m.body = "[members] " + list;
        m.ts = Instant.now();
        m.msgId = java.util.UUID.randomUUID().toString();

        session.outbound.send(m); // 요청자에게만 전송
    }

    /** (읽기 전용) 지금까지의 히스토리 */
    public List<MsgFormat> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /* -------- 내부 도우미들 -------- */

    private void broadcast(MsgFormat msg) {
        for (InMemorySession s : members.values()) {
            s.outbound.send(msg); // 각 사람의 통로(콘솔/소켓 등)로 전송
        }
    }

    private MsgFormat systemMsg(String text) {
        MsgFormat m = new MsgFormat();
        m.type = "system";
        m.roomId = roomId;
        m.from = "system";
        m.body = text;
        m.ts = Instant.now();                  // 시스템 메시지도 시간 채움
        m.msgId = java.util.UUID.randomUUID().toString();
        return m;
    }
}
