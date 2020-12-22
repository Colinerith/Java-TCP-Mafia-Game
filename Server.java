import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
	private static Socket clientSocket;
	private static int playerNum; // 플레이어 수
	private static int mafia1Id, mafia2Id; // 마피아인 플레이어들의 id
	private static int doctorId;
	private static int aliveNum; // 살아있는 플레이어의 수
	private static int mafiaNum; // 살아있는 마피아의 수
	static ArrayList<ServerSend> playerSend = new ArrayList<ServerSend>();
	static ArrayList<ServerReceive> playerReceive = new ArrayList<ServerReceive>();
	static Scanner scv = new Scanner(System.in);

	public static void waiting_room() { // 플레이어 모집 & 역할 분배
		System.out.println("*** NEW GAME ***");
		System.out.print("Enter the number of players(5~7): ");
		playerNum = scv.nextInt();
		int rand = 0;
		mafia2Id = -1;
		aliveNum = playerNum;

		mafiaNum = (playerNum < 7) ? 1 : 2; // 마피아 수 결정
		// 랜덤하게 역할 결정 (나머지는 시민)
		mafia1Id = (int) (Math.random() * (playerNum - 1)); // 범위: 0~playerNum-1
		while (rand == mafia1Id)
			rand = (int) (Math.random() * (playerNum - 1));
		doctorId = rand;
		if (mafiaNum == 2) {
			while (rand == mafia1Id || rand == doctorId)
				rand = (int) (Math.random() * (playerNum - 1));
			mafia2Id = rand;
		}

		try (ServerSocket sSocket = new ServerSocket(10000)) {
			for (int j = 0; j < playerNum; j++) { // 입력한 플레이어 수만큼 클라이언트 대기
				clientSocket = sSocket.accept();
				char role;
				if (j == mafia1Id || j == mafia2Id) // 마피아
					role = 'm';
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
		String msg;
		boolean running = true;
		if (who == 'a') {
			while (running) {
				for (int i = 0; i < playerNum; i++) {
					if (playerReceive.get(i).alive == true && playerReceive.get(i).received == true) { // 메시지를 입력했다면
						msg = "[player " + Integer.toString(i) + "]: " + playerReceive.get(i).receivedMsg; // 메시지를 가져오고
						playerReceive.get(i).received = false; // '메시지 받음' 상태를 false로 바꾸고
						send_message('a', msg);// 모두에게 전달
						if (playerReceive.get(i).receivedMsg.equalsIgnoreCase("quit")) {
							running = false;
							break;
						}
					}
				}
			}
		} else if (who == 'm') { // 마피아 두명끼리 대화. 죽은 플레이어는 지켜볼 수 있음
			while (running) {
				for (int i = 0; i < playerNum; i++) {
					if (playerSend.get(i).role == 'm' && playerReceive.get(i).alive == true
							&& playerReceive.get(i).received == true) { // 마피아가 메시지를 입력했다면
						msg = "[player " + Integer.toString(i) + "]: " + playerReceive.get(i).receivedMsg; // 메시지를 가져오고
						playerReceive.get(i).received = false; // '메시지 받음' 상태를 false로 바꾸고
						send_message('m', msg);// 마피아에게 전달
						if (playerReceive.get(i).receivedMsg.equalsIgnoreCase("quit")) {
							running = false;
							break;
						}
					}
				}
			}
		}
	}

	public static void voting() {
		send_message('a', "[System]: Voting. Enter the number of the player. (time limit)");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int v, maxidx = 0, max = 0;
		int[] intArr = { 0, 0, 0, 0, 0, 0, 0 };
		for (int i = 0; i < playerNum; i++) {
			if (playerReceive.get(i).alive == true && playerReceive.get(i).received == true) {
				v = Integer.parseInt(playerReceive.get(i).receivedMsg);
				playerReceive.get(i).received = false;
				intArr[v]++;
			}
		}
		for (int i = 0; i < playerNum; i++) {
			System.out.println(intArr[i]); // test
			if (intArr[i] > max) {
				max = intArr[i];
				maxidx = i;
			}
		}
		String msg = "[System]: Player" + Integer.toString(maxidx) + " was ";
		if (playerSend.get(maxidx).role == 'm') {
			msg += "a Mafia. ";
			mafiaNum--;
		} else
			msg += "not a Mafia. ";

		aliveNum--;
		playerSend.get(maxidx).alive = false; // 죽은 상태로 만듦
		playerReceive.get(maxidx).alive = false;

		msg += "Alive players: " + Integer.toString(aliveNum) + ", Alive Mafia: " + Integer.toString(mafiaNum);
		send_message('a', msg);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void send_message(char who, String msg) { // 사회자가 명시된 플레이어에게 메시지를 보냄
		// 메시지 받고 나면 ServerSend에서 status를 알아서 w로 바꿈
		if (who == 'a') { // 모두에게
			for (int i = 0; i < playerNum; i++) {
				playerSend.get(i).msg = msg;
				playerSend.get(i).status = 's';
			}
		} else if (who == 'm') { // 마피아와 죽은 사람들만 받는 메시지
			for (int i = 0; i < playerNum; i++) {
				if (playerSend.get(i).alive == false && playerSend.get(i).role != 'm') {
					playerSend.get(i).msg = msg;
					playerSend.get(i).status = 's';
				}
			}
			playerSend.get(mafia1Id).msg = msg;
			playerSend.get(mafia1Id).status = 's';
			playerSend.get(mafia2Id).msg = msg;
			playerSend.get(mafia2Id).status = 's';
		}
	}

	public static void day() { // 낮-플레이어들이 채팅&투표로 용의자 지목
		send_message('a', "[System]: Daytime. Find the mafia through chat. Enter 'quit' to end the chat.");
		chatting('a');
		voting();
	}

	public static void night() { // 밤-마피아의 살인 & 의사의 미션
		int killWho = -1;
		if (mafiaNum == 1) { // 살아 있는 마피아가 한 명이면
			send_message('a', "[System]: Night. (Only Mafia) Enter the number of the player to kill. (time limit)");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (playerNum < 7) { // 원래 마피아가 한 명
				if (playerReceive.get(mafia1Id).received == true) {
					killWho = Integer.parseInt(playerReceive.get(mafia1Id).receivedMsg);
					playerReceive.get(mafia1Id).received = false; // '메시지 받음' 상태를 false로 바꾸고
				}
			} else { // 마피아 한 명이 죽은 상태
				if (playerReceive.get(mafia1Id).alive == true && playerReceive.get(mafia1Id).received == true) {
					killWho = Integer.parseInt(playerReceive.get(mafia1Id).receivedMsg);
					playerReceive.get(mafia1Id).received = false; // '메시지 받음' 상태를 false로 바꾸고
				} else if (playerReceive.get(mafia2Id).alive == true && playerReceive.get(mafia2Id).received == true) {
					killWho = Integer.parseInt(playerReceive.get(mafia2Id).receivedMsg);
					playerReceive.get(mafia2Id).received = false; // '메시지 받음' 상태를 false로 바꾸고
				}
			}
		} else { // 살아 있는 마피아가 두 명이면
			send_message('a',
					"[System]: Night. (Only Mafia) Choose a player to kill through chat. Enter 'quit' to end the chat.");
			chatting('m'); // 마피아끼리 대화
			send_message('a',
					"[System]: (Only one of the Mafias) Enter the number of the player to kill. (time limit)");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (playerReceive.get(mafia1Id).received == true) {
				killWho = Integer.parseInt(playerReceive.get(mafia1Id).receivedMsg);
				playerReceive.get(mafia1Id).received = false;
			} else if (playerReceive.get(mafia2Id).received == true) {
				killWho = Integer.parseInt(playerReceive.get(mafia2Id).receivedMsg);
				playerReceive.get(mafia2Id).received = false;
			}
		}

		if (playerSend.get(doctorId).alive) { // 의사가 살아있다면, 미션
			int saveWho = -2;
			send_message('a', "[System]: (Only Doctor) Choose a player to save. (time limit)");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (playerReceive.get(doctorId).received == true) {
				saveWho = Integer.parseInt(playerReceive.get(doctorId).receivedMsg);
				playerReceive.get(doctorId).received = false;
			}
			if (saveWho == killWho) {
				send_message('a', "The doctor saved the player. No one died.");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			else {
				playerSend.get(killWho).alive = false; // 해당 플레이어를 죽은 상태로 바꿈
				playerReceive.get(killWho).alive = false;
				aliveNum--;
				String msg = "Player" + Integer.toString(killWho) + " has just died. Alive players: "
						+ Integer.toString(aliveNum) + ", Alive Mafia: " + Integer.toString(mafiaNum);

				send_message('a', msg);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else { // 의사가 없다면, 살아날 기회 없이 죽음
			playerSend.get(killWho).alive = false; // 해당 플레이어를 죽은 상태로 바꿈
			playerReceive.get(killWho).alive = false;
			aliveNum--;
			String msg = "Player" + Integer.toString(killWho) + " has just died. " + Integer.toString(aliveNum)
					+ " players are alive.";
			send_message('a', msg);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		while (true) {
			playerSend.clear();
			playerReceive.clear();
			waiting_room(); // 플레이어 수를 입력받고 그 수만큼 플레이어가 찰 때까지 기다림

			while (mafiaNum != 0 && mafiaNum * 2 != aliveNum) { // 조건 : 마피아수=0 or 마피아수:시민수 = 1:1
				day();
				if (mafiaNum == 0 || mafiaNum * 2 == aliveNum)
					break;
				night();
			}

			String msg = "The Winner is: " + ((mafiaNum == 0) ? "Citizen" : "Mafia");
			System.out.println(msg);
			send_message('a', msg);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}
