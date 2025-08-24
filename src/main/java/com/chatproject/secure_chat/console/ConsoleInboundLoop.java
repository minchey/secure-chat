package com.chatproject.secure_chat.console;

import com.chatproject.secure_chat.chat.ChatRoom;
import com.chatproject.secure_chat.model.MsgFormat;
import com.chatproject.secure_chat.shared.Jsons;
import com.chatproject.secure_chat.shared.MessageNormalizer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 터미널(콘솔)에서 "한 줄 = 한 JSON"을 계속 읽어서
 * ChatRoom에 넘겨주는 작은 리더(입력 루프)입니다
 *
 * 사용 예:
 *   new Thread(new ConsoleInboundLoop(room)).start();
 *   // 터미널에 아래처럼 치면 됩니다:
 *   // {"type":"join","roomId":"room-1","from":"alice"}
 *   // {"type":"message","roomId":"room-1","from":"alice","body":"안녕!"}
 *   // /quit  <-- 종료
 */
public class ConsoleInboundLoop implements Runnable {
    private final ChatRoom room;

    /** @param room 입력된 메시지를 처리할 대상 채팅방 */
    public ConsoleInboundLoop(ChatRoom room) {
        this.room = room;
    }

    @Override
    public void run() {
        // System.in(키보드 입력)을 문자 스트림으로 감싼다
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            System.out.println("""
                [ConsoleInboundLoop] 한 줄에 하나의 JSON을 입력하세요. 예)
                {"type":"join","roomId":"room-1","from":"alice"}
                {"type":"message","roomId":"room-1","from":"alice","body":"안녕!"}
                {"type":"leave","roomId":"room-1","from":"alice"}
                /quit  (종료)
                """);

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;    // 빈 줄은 무시
                if ("/quit".equals(line)) break; // 종료 명령

                try {
                    // 1) JSON → MsgFormat
                    MsgFormat msg = Jsons.mapper().readValue(line, MsgFormat.class);

                    // 2) 정규화: ts/msgId 자동 생성, 공백/대소문자 정리
                    MessageNormalizer.normalize(msg);

                    // 3) 최소 유효성 검사
                    if (!msg.isValid()) {
                        System.err.println("[WARN] invalid message (ignored): " + line);
                        continue;
                    }

                    // 4) 타입에 맞게 ChatRoom으로 전달
                    switch (msg.type) {
                        case "join"    -> room.joinHardcoded(msg.from);
                        case "leave"   -> room.leave(msg.from);
                        case "message" -> room.post(msg);
                        default        -> System.err.println("[WARN] unknown type: " + msg.type);
                    }
                } catch (Exception parseErr) {
                    // JSON이 잘못 됐거나 타입이 안 맞을 때
                    System.err.println("[ERROR] parse failed: " + parseErr.getMessage());
                }
            }
            System.out.println("[ConsoleInboundLoop] 종료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
