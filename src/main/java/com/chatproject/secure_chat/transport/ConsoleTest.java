package com.chatproject.secure_chat.transport;

public class ConsoleTest {
    public static void main(String[] args) {


        ConsoleOutbound out = new ConsoleOutbound("alice");

        com.chatproject.secure_chat.model.MsgFormat m = new com.chatproject.secure_chat.model.MsgFormat();
        m.type = "message";
        m.roomId = "room-1";
        m.from = "alice";
        m.body = "안녕!";

// 바로 출력해보기
        out.send(m);

    }
}