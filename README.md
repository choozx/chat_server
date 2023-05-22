# Netty 기반 채팅 서버


클라이언트			 : JAVA(Netty)  
서버          : JAVA(Netty)  
프로토콜       : protobuf  

클라이언트는 추후 다른 repo로 올릴 예정

# 1.구조

월드 (1)  
&nbsp;&nbsp;&nbsp; └ 채팅방 (N)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; └ 유저 (N)  
			
protobuf. 
```
message ChatMessage{
  ChatMethod.Type chatMethod = 1;
  string roomName = 2;
  string nickName = 3;
  string msg = 4;
  chatException chatException = 5;
}
```
```
message ChatMethod {
  enum Type {
    NONE = 0;
    CREATE = 1;
    ENTER = 2;
    SEND = 3;
    QUIT = 4;
    EXIT = 5;
    IDLE = 6;
    JOIN = 7;
  }
}
```

			
# 2.동작  

*기본적으로 enum의 type이 send를 제외한 모든 type에 대해 synchronized 적용됨

- 연결  
	- 기본적인 TCP의 연결방식으로 작동 (3hand-shaking)
	- 계정 생성
		- 계정의 대한 validCheck
	- 연결 후 입장 안내
		- 연결된 Channel은 자료구조에서 관리 (한번 연결된 클라를 접속 종료까지 추적하기 위함)
	- 방 생성/입장

- 입장
	- 대화시작

- 퇴장
	- 퇴장시 방의 인원이 0명이면 자동으로 채팅방 삭제
	- 퇴장된 클라이언트는 다시 방을 생성/입장 가능한 상태가 됨

- 종료
	- 서버와 클라의 연결을 끊고 자료구조에서 Channel도 삭제 

# 3.예외

- 1.더미소켓의 처리
	- 어떠한 이유로 클라가 서버와의 연결이 강제종료 되었을 때 
	- Netty에서 재공되는 userEventTriggered를 사용
	- 3가지 상태를 설정 가능(read, write, all)
	- idle상태의 걸리게 되면 해당 클라와의 연결 해제(자료구조애서도 삭제)

- 2.중복로그인 방지
	- 로그인시 클라의 Channel을 관리하는 자료구조에서 해당 id를 찾아내어 id의 중복을 체크

- 3.없는 방 입장불가
	- 방의 이름으로 입장하기때문에 존재하지 않는 방의 대한 입력 예외처리 

# 4.개선사항

- 1.무분별한 synchronized의 사용 
- 2.모든 프로토콜을 하나의 메세지로만 됨 (Select모델과 유사함)


