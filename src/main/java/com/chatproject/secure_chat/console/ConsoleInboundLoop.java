package com.chatproject.secure_chat.console;

import com.chatproject.secure_chat.chat.ChatRoom;
import com.chatproject.secure_chat.model.MsgFormat;
import com.chatproject.secure_chat.shared.Jsons;
import com.chatproject.secure_chat.shared.MessageNormalizer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 콘솔에서 "한 줄 = 한 JSON" 입력을 읽어
 * MsgFormat으로 변환한 뒤, type에 따라 ChatRoom 메서드로 분기(라우팅)합니다.
 * 예) join → joinHardcoded, message → post, members → sendMembersTo
 *
 * 종료 명령: /quit
 */
public class ConsoleInboundLoop implements Runnable {
    private final ChatRoom room;

    /** @param room 입력 메시지를 처리할 채팅방 */
    public ConsoleInboundLoop(ChatRoom room) {
        this.room = room;
    }

    /** 콘솔 입력을 계속 읽어 라우팅하는 메인 루프 */
    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            System.out.println("""
                [ConsoleInboundLoop] 한 줄에 하나의 JSON을 입력하세요. 예)
                {"type":"join","roomId":"room-1","from":"alice"}
                {"type":"message","roomId":"room-1","from":"alice","body":"안녕!"}
                {"type":"members","roomId":"room-1","from":"alice"}
                {"type":"leave","roomId":"room-1","from":"alice"}
                /quit  (종료)
                """);

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if ("/quit".equals(line)) break;

                try {
                    // JSON → 객체
                    MsgFormat msg = Jsons.mapper().readValue(line, MsgFormat.class);

                    // 표준화(ts/msgId 자동 생성, 공백/소문자 정리)
                    MessageNormalizer.normalize(msg);

                    // 최소 검증
                    if (!msg.isValid()) {
                        System.err.println("[WARN] invalid message (ignored): " + line);
                        continue;
                    }

                    // 🔽 라우팅: type에 맞는 처리로 분기
                    switch (msg.type) {
                        case "join"    -> room.joinHardcoded(msg.from);
                        case "leave"   -> room.leave(msg.from);
                        case "message" -> room.post(msg);
                        case "members" -> room.sendMembersTo(msg.from);
                        case "history" -> room.sendHistoryTo(msg.from, 50); // body 무시, 고정 50개
                        default        -> System.err.println("[WARN] unknown type: " + msg.type);
                    }
                } catch (Exception parseErr) {
                    System.err.println("[ERROR] parse failed: " + parseErr.getMessage());
                }
            }
            System.out.println("[ConsoleInboundLoop] 종료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
