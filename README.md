cat > README.md <<'EOF'
# Secure Chat (CUI) — 현재 범위: ConsoleInboundLoop까지

> **CUI 전용** 간단 채팅 프로젝트. 지금 단계에서는 콘솔에서 줄 단위 JSON을 입력하면 방 로직이 처리하고, 결과를 콘솔(NDJSON)로 출력합니다.

---

## 오늘까지 구현된 기능

- **메시지 스키마(`MsgFormat`)**
    - `type`, `roomId`, `from`, `body` + `ts`, `msgId`
    - `isValid()`로 최소 유효성 검사
- **정규화(`MessageNormalizer`)**
    - 공백/대소문자 정리
    - `ts`(현재 UTC), `msgId`(UUID) 자동 생성
- **전송 인터페이스(`Outbound`) & 콘솔 구현(`ConsoleOutbound`)**
    - 전송 방법을 인터페이스로 분리 → 콘솔/소켓 교체 용이
- **세션(`InMemorySession`)**
    - 사용자 ID와 전송 경로(Outbound) 묶음
- **채팅방(`ChatRoom`)**
    - **입장/퇴장/브로드캐스트** + (옵션) **히스토리**
    - `sendMembersTo(user)` : 현재 멤버 목록을 요청자에게만 시스템 메시지로 전송
- **콘솔 인바운드 루프(`ConsoleInboundLoop`)**
    - 콘솔에서 **한 줄 = 한 JSON** 입력을 읽어, **type에 따라 라우팅**

> ⚠️ 참고: `ConsoleCommandLoop`, `HardcodedRunner`(dev 자동 실행) 같은 편의 코드는 **현재 범위에서 제외**. 필요 시 이후 단계에서 다시 추가합니다.

---

## 구조(패키지)

src/main/java/com/chatproject/secure_chat
├─ model/ # 데이터 스키마
│ └─ MsgFormat.java
├─ shared/ # 공용 유틸
│ ├─ Jsons.java
│ └─ MessageNormalizer.java
├─ transport/ # 전송 방법(인터페이스/구현)
│ ├─ Outbound.java
│ └─ ConsoleOutbound.java
├─ session/ # 사용자 세션 카드
│ └─ InMemorySession.java
├─ chat/ # 방 도메인 로직
│ └─ ChatRoom.java
└─ console/ # 콘솔 입력 루프
└─ ConsoleInboundLoop.java


---

## 메시지 포맷 (JSON, 한 줄 = 한 메시지)

```jsonc
// join
{"type":"join","roomId":"room-1","from":"alice"}

// message
{"type":"message","roomId":"room-1","from":"alice","body":"안녕!"}

// members (현재 인원 목록 요청: 요청자에게만 시스템 메시지로 회신)
{"type":"members","roomId":"room-1","from":"alice"}

// leave
{"type":"leave","roomId":"room-1","from":"alice"}

정규화 규칙

type/roomId/from → trim(), type은 toLowerCase()

type == "message" 이면 body.strip()

ts == null → Instant.now()

msgId == null or blank → UUID.randomUUID().toString()