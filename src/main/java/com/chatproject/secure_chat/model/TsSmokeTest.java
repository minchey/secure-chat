import com.chatproject.secure_chat.model.MsgFormat;
import static java.time.Instant.now;

public class TsSmokeTest {
    public static void main(String[] args) throws Exception {
        var om = Jsons.mapper();

        // 객체 -> JSON (ts 직접 채움)
        MsgFormat out = new MsgFormat();
        out.type = "message";
        out.roomId = "room-1";
        out.from = "alice";
        out.body = "안녕!";
        out.ts = now(); // 없으면 나중에 처리부에서 now() 세팅
        String json = om.writeValueAsString(out);
        System.out.println(json);
        // 예) {"type":"message","roomId":"room-1","from":"alice","body":"안녕!","ts":"2025-08-21T13:05:00Z"}

        // JSON -> 객체 (ts 포함)
        String raw = """
            {"type":"join","roomId":"room-1","from":"bob","ts":"2025-08-21T13:06:30Z"}
        """;
        MsgFormat in = om.readValue(raw, MsgFormat.class);
        System.out.println(in.type + " / " + in.ts); // join / 2025-08-21T13:06:30Z
    }
}
