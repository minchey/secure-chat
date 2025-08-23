package com.chatproject.secure_chat;

import com.chatproject.secure_chat.chat.ChatRoom;
import com.chatproject.secure_chat.console.ConsoleInboundLoop;
import com.chatproject.secure_chat.session.InMemorySession;
import com.chatproject.secure_chat.transport.ConsoleOutbound;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HardcodedRunner {

    @Bean
    CommandLineRunner demo() {
        return args -> {
            // 하드코딩 세션 레지스트리
            Map<String, InMemorySession> reg = new HashMap<>();
            reg.put("alice", new InMemorySession("alice", new ConsoleOutbound("alice")));
            reg.put("bob",   new InMemorySession("bob",   new ConsoleOutbound("bob")));

            // 방 생성(레지스트리 주입)
            ChatRoom room = new ChatRoom("room-1", reg);

            // 콘솔 JSON 루프 시작
            new Thread(new ConsoleInboundLoop(room), "console-json-loop").start();

            System.out.println("""
                NDJSON 입력 예:
                {"type":"join","roomId":"room-1","from":"alice"}
                {"type":"join","roomId":"room-1","from":"bob"}
                {"type":"message","roomId":"room-1","from":"alice","body":"안녕 Bob!"}
                {"type":"leave","roomId":"room-1","from":"alice"}
                /quit
                """);
        };
    }
}
