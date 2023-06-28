# Multi-Chat
소켓 통신과 스레드를 이용한 자바 기반 멀티 채팅 프로그램

<br>

### Server
* 클라이언트가 접속 했는지 안했는지 확인 용도
* 클라이언트가 접속 시 Socket.accept()를 하여 요청을 받는다.
* 클라이언트로부터 메시지 입력이 들어오면 모든 클라이언트로 메시지를 보낸다.
* 스레드풀을 이용하여 접속 가능한 인원을 제한시킨다.

### Client
* 서버로 연결을 요청한다.
* 서버로부터 메시지를 전달받아 확인 할 수 있다.

### To-Do
- [ ]  GUI 만들기
- [ ]  스레드 풀 더 공부해서 내부 구조 파악하기

<br>
<hr>

#### Reference)
* [multi-chat](https://kadosholy.tistory.com/126)
* [synchronize](https://inpa.tistory.com/entry/JCF-%F0%9F%A7%B1-ArrayList-vs-Vector-%EB%8F%99%EA%B8%B0%ED%99%94-%EC%B0%A8%EC%9D%B4-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0)
* [thread-pool](https://velog.io/@hsbang_thom/Java-Thread-4-Thread-Pool)

