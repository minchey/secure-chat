import com.chatproject.secure_chat.chat.ChatRoom;
import com.chatproject.secure_chat.console.ConsoleInboundLoop;
import com.chatproject.secure_chat.session.InMemorySession;
import com.chatproject.secure_chat.transport.ConsoleOutbound;

import java.util.HashMap;
import java.util.Map;

public class ConsoleTest {
    public static void main(String[] args) {
        // 0) 하드코딩 등록표
        Map<String, InMemorySession> reg = new HashMap<>();
        reg.put("alice", new InMemorySession("alice", new ConsoleOutbound("alice")));
        reg.put("bob",   new InMemorySession("bob",   new ConsoleOutbound("bob")));

        // 1) 방 만들기
        ChatRoom room = new ChatRoom("room-1", reg);

        // 2) 콘솔 루프 시작 (다른 스레드)
        new Thread(new ConsoleInboundLoop(room), "console-json-loop").start();
    }
}
