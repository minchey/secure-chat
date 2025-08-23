package com.chatproject.secure_chat.console;

import com.chatproject.secure_chat.chat.ChatRoom;
import com.chatproject.secure_chat.model.MsgFormat;
import com.chatproject.secure_chat.shared.Jsons;
import com.chatproject.secure_chat.shared.MessageNormalizer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConsoleInboundLoop implements Runnable {
    private final ChatRoom room;

    public ConsoleInboundLoop(ChatRoom room) {
        this.room = room;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if ("/quit".equals(line)) break;

                // JSON(한 줄) -> 객체
                MsgFormat msg = Jsons.mapper().readValue(line, MsgFormat.class);

                // ✅ 바로 여기서 "정규화 + ts 자동 생성"
                MessageNormalizer.normalize(msg);

                // 최소 검증
                if (!msg.isValid()) {
                    System.err.println("invalid message, ignored: " + line);
                    continue;
                }

                // 타입 라우팅
                switch (msg.type) {
                    case "join"    -> room.joinHardcoded(msg.from);
                    case "leave"   -> room.leave(msg.from);
                    case "message" -> room.post(msg);
                    default        -> { /* 알 수 없는 타입은 무시 */ }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
