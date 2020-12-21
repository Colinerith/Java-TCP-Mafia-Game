import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
	private static Socket clientSocket;
	private static int playerNum; // 플레이어의 수
	private static int mafia1Id, mafia2Id; // 마피아인 플레이어들의 id. 'players'의 인덱스로 활용
	private static int policeId; // 경찰의 id
	private static int doctorId; // 의사의 id
	private static int aliveNum; // 살아있는 플레이어의 수
	private static int mafiaNum; // 살아있는 마피아의 수
	static ArrayList<ServerSend> playerSend = new ArrayList<ServerSend>();
	static ArrayList<ServerReceive> playerReceive = new ArrayList<ServerReceive>();
	static Scanner scv = new Scanner(System.in);

	public static void waiting_room() { // 대기실. 플레이어 모집 & 역할 분배
		System.out.println("*** NEW GAME ***");
		System.out.print("Enter the number of players(5~8): ");
		playerNum = scv.nextInt();
		int rand = 0;
		mafia2Id = -1;

		mafiaNum = (playerNum < 7) ? 1 : 2; // 마피아 수 결정
		// 랜덤하게 역할 결정 (나머지는 시민)
		mafia1Id = (int) (Math.random() * (playerNum - 1)); // 범위: 0~playerNum-1
		while (rand == mafia1Id)
			rand = (int) (Math.random() * (playerNum - 1));
		policeId = rand;
		while (rand == mafia1Id || rand == policeId)
			rand = (int) (Math.random() * (playerNum - 1));
		doctorId = rand;
		if (mafiaNum == 2) {
			while (rand == mafia1Id || rand == policeId || rand == doctorId)
				rand = (int) (Math.random() * (playerNum - 1));
			mafia2Id = rand;
		}

		try (ServerSocket sSocket = new ServerSocket(10000)) {
			for (int j = 0; j < playerNum; j++) { // 입력한 플레이어 수만큼 클라이언트 대기
				clientSocket = sSocket.accept();
				char role;
				if (j == mafia1Id || j == mafia2Id) // 마피아
					role = 'm';
				else if (j == policeId) // 경찰
					role = 'p';
				else if (j == doctorId)// 의사
					role = 'd';
				else
					role = 'c';

				// 개별스레드 생성. 수락 했을 때의 client 주소와 id를 담는다.
				ServerSend ss = new ServerSend(clientSocket, j, role);
				ServerReceive sr = new ServerReceive(clientSocket, j);

				playerSend.add(ss);
				playerReceive.add(sr);

				new Thread(ss).start();
				new Thread(sr).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void chatting(char who) { // a:all, m:mafia끼리만
		if (who == 'a') { // 모두가 대화. 죽은 플레이어는 지켜볼 수 있음
			String msg;
//			for (int i = 0; i < playerNum; i++) {
//				playerReceive.get(i).status = 'r'; // 받을 수 있게 상태 바꿈
//			}
			while (true) {
				for (int i = 0; i < playerNum; i++) {
					if (playerReceive.get(i).received == true) { // 플레이어가 메시지를 입력했다면
						msg = "[player" + Integer.toString(i) + "]: " + playerReceive.get(i).receivedMsg; // 메시지를 가져오고
						playerReceive.get(i).received = false; // '메시지 받음' 상태를 false로 바꾸고
						// 모두에게 전달
						for (int j = 0; j < playerNum; j++) {
							send_message('a', msg);
						}
					}
				}
			}
		} else if (who == 'm') { // 마피아끼리 대화. 죽은 플레이어는 지켜볼 수 있음

		}
	}

	public static void voting() {
		send_message('a', "[System] Voting. Enter the number of the player: ");
	}

	public static void send_message(char who, String msg) { // 사회자가 명시된 플레이어에게 메시지를 보냄
		// 메시지 받고 나면 ServerSend에서 status를 알아서 w로 바꿈

		if (who == 'a') { // 모두에게
			for (int i = 0; i < playerNum; i++) {
				playerSend.get(i).msg = msg;
				playerSend.get(i).status = 's';
			}
		} else if (who == 'p') { // 경찰에게
			playerSend.get(policeId).msg = msg;
			playerSend.get(policeId).status = 's';
		} else if (who == 'd') { // 의사에게
			playerSend.get(doctorId).msg = msg;
			playerSend.get(doctorId).status = 's';
		} else if (who == 'm') { // 마피아에게
			playerSend.get(mafia1Id).msg = msg;
			playerSend.get(mafia1Id).status = 's';
			if (playerNum > 6) {
				playerSend.get(mafia2Id).msg = msg;
				playerSend.get(mafia2Id).status = 's';
			}
		}
	}

	public static void day() { // 낮-플레이어들이 채팅&투표로 용의자 지목
		send_message('a', "[System] Daytime. Find the mafia through chat. (time limit: 2m)");
		// java도 alarm signal 되나? 아니면 사회자가 stop을 입력하면 멈추게끔
		chatting('a');
		while (true)
			;
		// voting();
	}

	public static void night() { // 밤-경찰/의사의 미션 & 마피아의 살인
		System.out.println("[System] Night. ");

		chatting('m');
	}

	public static void main(String[] args) {
		while (true) {
			playerSend.clear();
			playerReceive.clear();
			waiting_room(); // 플레이어 수를 입력받고 그 수만큼 플레이어가 찰 때까지 기다림

			while (mafiaNum != 0 && mafiaNum != aliveNum / 2) { // 조건 : 마피아수=0 or 마피아수:시민수 = 1:1
				day();
				night();
			}

			System.out.println("The Winner is: ");
			System.out.println("Restart game? (Y/N): ");
			char restart = scv.next().charAt(0);
			if (restart == 'N')
				break;
		}
	}
}
