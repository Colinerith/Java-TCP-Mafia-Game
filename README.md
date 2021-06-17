#마피아 게임 설계
0. 언어
 - Java

1. 게임 방식
 - 인원 선택 (5~7)
  - 그에 따른 직업별 인원수
    - 5명 : 시민3 의사1 마피아1
    - 6명 : 시민4 의사1 마피아1
    - 7명 : 시민5 의사1 마피아2
 - 직업
  : 시민/의사/마피아
    시민: 무직
    의사: <밤>에 한 명을 골라 살릴 수 있음(본인 선택 가능)
    마피아: <밤>에 한 명을 골라 죽일 수 있음
    -> 두 명 이상일 경우 채팅으로 결정 후 (시간 제한 짧게) 한 명의 플레이어가 입력하도록
 - 죽은 이후
  : 게임은 진행중이나 마피아지목, 투표 등으로 죽었을 경우 (재미 없으니까)모든 상황을 관전할 수 있도록 함 (말은 못 함)
 - 투표
  : 채팅을 통한 추론 및 투표로 마피아 용의자를 지목 (어느 한 플레이어가 'quit'을 입력하면 채팅 종료, 투표는 시간 제한)
 - 승패
  : 마피아를 모두 찾아 죽이면 시민 승, 시민:마피아=1:1이 되면 마피아 승
 - 메시지 보내기
  - 사회자가 각 플레이어에게 메시지를 보낼 때, "[System]: "을 포함
  - 모두에게 보낼 메시지
    - 전체 채팅 내용 (받은 메시지를 모두에게 디스플레이)
    - 공지사항
      - 낮/밤 변경
      - 채팅을 통해 마피아를 추리하세요.
      - 마피아로 의심되는 플레이어 번호를 입력하세요.
      - 마피아는 죽일 플레이어를 선택해 주세요.
      - playerx가 죽었습니다. 남은 플레이어 수: 남은 마피아 수: / 아무도 죽지 않았습니다(의사가 성공했을 때)
      - 투표로 지목된(죽은) playerx는 마피아입니다/마피아가 아닙니다. 남은 플레이어 수: , 남은 마피아 수:
  - 특정 플레이어에게 보낼 메시지
    - 마피아들이 보내는 메시지를 받아 마피아와 죽은 플레이어들에게 보냄
  - 투표는 플레이어 번호로
  
2. 구현 시 필요한 것
 - 인원수에 따른 쓰레드 생성
 - 모두 모이면 시작하도록 -> CyclicBarrier
 - Server의 main쓰레드 : 사회자. ServerSend, ServerReceive 쓰레드들을 생성하고 각 객체들의 속성 관리 및 메시지 송수신
 - 서버에서 생성한 각 쓰레드들: 메시지 전달 및 받아오는 역할
 					- ServerSend Thread	   <-> player(Client)0의 ClientReceive Thread
 	 				- ServerReceive Thread <-> player(Client)0의 ClientSend Thread

 					- ServerSend Thread	   <-> player(Client)1의 ClientReceive Thread
 				 	- ServerReceive Thread <-> player(Client)1의 ClientSend Thread
 					
 - 각 client들에게 랜덤하게 역할 분배 -> 새로 시작할 때마다 달라지도록
 - <밤>에는 마피아들끼리만 채팅할 수 있도록 조절 -> 속성이 'm' 인 쓰레드 객체만 해당 메시지를 Client에게 전달하도록.
 - 투표 기능 -> 각 Client들이 보낸 메시지가 ServerReceive로 전달되면 그 값을 받아 max 비교
 - 죽은 client에 대하여 관전 권한 부여 -> ServerReceive는 무시되고, ServerSend만 기능을 할 수 있게 됨(client에게 메시지 전달만 함)
 - 각 직업이 <밤>에 순서대로 임무를 할 수 있도록 (마피아-의사)
