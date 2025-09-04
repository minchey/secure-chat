package com.chatproject.secure_chat.chat;

import com.chatproject.secure_chat.model.MsgFormat;
import com.chatproject.secure_chat.session.InMemorySession;

import java.time.Instant;
import java.util.*;

/**
 * 단일 채팅방을 관리합니다.
 * <p>
 * 책임:
 * <ul>
 *   <li>입장/퇴장 관리(현재 멤버 목록 유지)</li>
 *   <li>메시지 브로드캐스트(멤버들의 Outbound로 전송)</li>
 *   <li>(옵션) 간단 히스토리 보관</li>
 * </ul>
 *
 * <p><b>동시성</b>: 공개 메서드(joinHardcoded, leave, post, sendMembersTo)는
 * {@code synchronized}로 보호되어 멤버/히스토리 상태가 꼬이지 않도록 합니다.</p>
 */
public class ChatRoom {
    /** 이 방의 ID(예: "room-1"). */
    private final String roomId;

    /** 하드코딩된 전체 세션 등록표(userId -> session). */
    private final Map<String, InMemorySession> registry;

    /** 현재 방에 입장한 멤버(userId -> session). */
    private final Map<String, InMemorySession> members = new HashMap<>();

    /** (옵션) 메시지 히스토리. */
    private final List<MsgFormat> history = new ArrayList<>();

    /** 히스토리 최대치 */
    private static final int DEFAULT_HISTORY_ON_JOIN = 50;

    /**
     * 채팅방을 생성합니다.
     *
     * @param roomId   방 ID
     * @param registry 하드코딩 세션 등록표(userId → session)
     */
    public ChatRoom(String roomId, Map<String, InMemorySession> registry) {
        this.roomId = roomId;
        this.registry = registry;
    }

    /**
     * 등록표(registry)에서 사용자를 찾아 이 방에 입장시킵니다.
     * <p>이미 입장했거나 등록표에 없으면 아무 작업도 하지 않습니다.
     * 입장 성공 시 시스템 메시지("<code>{userId} joined</code>")를 방 전체에 전송합니다.</p>
     *
     * @param userId 입장할 사용자 ID
     */
    public synchronized void joinHardcoded(String userId) {
        InMemorySession s = registry.get(userId);
        if (s == null) return;                    // 미등록 사용자
        if (members.containsKey(userId)) return; // 이미 입장
        members.put(userId, s);
        // ✅ 입장 직후, 그 사람에게만 지난 대화 n개를 보여준다(예: 50개)
        sendHistoryTo(userId, 50);
        broadcast(systemMsg(userId + " joined"));
    }

    /**
     * 사용자를 방에서 내보냅니다. 실제로 멤버 목록에 있을 때만 동작합니다.
     * 성공 시 시스템 메시지("<code>{userId} left</code>")를 방 전체에 전송합니다.
     *
     * @param userId 퇴장할 사용자 ID
     */
    public synchronized void leave(String userId) {
        if (members.remove(userId) != null) {
            broadcast(systemMsg(userId + " left"));
        }
    }

    /**
     * 한 메시지를 방 멤버 전원에게 브로드캐스트합니다.
     * <ul>
     *   <li>메시지의 {@code roomId}가 이 방과 같아야 하며,</li>
     *   <li>{@code from} 사용자가 현재 멤버여야 합니다.</li>
     * </ul>
     * 조건을 만족하면 히스토리에 저장한 뒤 전송합니다.
     *
     * @param msg 보낼 메시지(정규화/검증을 통과한 객체 권장)
     */
    public synchronized void post(MsgFormat msg) {
        if (!roomId.equals(msg.roomId)) return;     // 방 불일치
        if (!members.containsKey(msg.from)) return; // 미입장 사용자
        history.add(msg);
        broadcast(msg);
    }

    /**
     * 현재까지의 히스토리를 읽기 전용 뷰로 반환합니다.
     * <p>반환 리스트는 수정할 수 없으며(수정 시 {@link UnsupportedOperationException}), 내부 변경은 반영됩니다(뷰).</p>
     *
     * @return 읽기 전용 히스토리 뷰
     */
    public List<MsgFormat> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * (편의) 요청자 한 명에게만 현재 멤버 목록을 시스템 메시지로 보냅니다.
     * <p>요청자가 방에 입장해 있지 않으면 무시합니다.</p>
     *
     * @param requesterId 목록을 요청한 사용자 ID
     */
    public synchronized void sendMembersTo(String requesterId) {
        var session = members.get(requesterId);
        if (session == null) return;

        String list = String.join(", ", members.keySet());
        if (list.isBlank()) list = "(none)";

        MsgFormat m = new MsgFormat();
        m.type = "system";
        m.roomId = roomId;
        m.from = "system";
        m.body = "[members] " + list;
        m.ts = Instant.now();
        m.msgId = java.util.UUID.randomUUID().toString();

        session.outbound.send(m); // 요청자에게만 전송
    }

    /** 요청자에게만 최근 N개 히스토리를 순서대로 전송합니다. 요청자가 방에 없으면 무시. */
    public synchronized void sendHistoryTo(String requesterId, int limit) {
        var session = members.get(requesterId);
        if (session == null) return; // 입장하지 않았으면 무시

        int size = history.size();
        int n = Math.min(Math.max(limit, 0), size);
        int start = size - n;

        // 헤더(시작 알림) 한 줄 보내기
        session.outbound.send(systemMsg("[history] last " + n + " messages"));

        // 최근 n개 메시지 차례대로 재전송
        for (int i = start; i < size; i++) {
            session.outbound.send(history.get(i));
        }

        // 끝 표시(선택)
        session.outbound.send(systemMsg("[history] end"));
    }


    /* ---------- 내부 도우미 ---------- */

    /** 방 멤버 전원에게 전송합니다. (private helper) */
    private void broadcast(MsgFormat msg) {
        for (InMemorySession s : members.values()) {
            s.outbound.send(msg);
        }
    }

    /** 시스템 공지 메시지를 만듭니다. (private helper) */
    private MsgFormat systemMsg(String text) {
        MsgFormat m = new MsgFormat();
        m.type = "system";
        m.roomId = roomId;
        m.from = "system";
        m.body = text;
        m.ts = Instant.now();
        m.msgId = java.util.UUID.randomUUID().toString();
        return m;
    }
}
