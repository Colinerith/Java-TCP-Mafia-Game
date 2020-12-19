import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.Scanner;

public class Server {
	private static Socket clientSocket;
	private static int playerNum; // 플레이어의 수
	private static int mafia1, mafia2; // 마피아인 플레이어들의 id
	private static int police; // 경찰의 id
	private static int doctor; // 의사의 id
	private static int aliveNum; // 살아있는 플레이어의 수
	private static int mafiaNum; // 살아있는 마피아의 수
	// volatile private static
	static Scanner scv = new Scanner(System.in);

	private static class Player implements Runnable {
		private int playerId; // 해당 플레이어 id
		private char role;
		private Socket clientSocket;

		Player(Socket clientSocket, int id, char r) {
			this.playerId = id;
			this.clientSocket = clientSocket;
			this.role = r;
		}

		@Override
		public void run() {
			System.out.println("Player " + this.playerId + " entered.");
			System.out.println("role: " + this.role);
			try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {
				Supplier<String> socketInput = () -> {
					try {
						return br.readLine();
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				};

				Stream<String> stream = Stream.generate(socketInput);
				stream.map(s -> {
					System.out.println("[Player" + this.playerId + ": ]"); // 채팅 출력

					// out.println(s);
					// out.println(msg); 사회자가 원하는 메시지를 전달
					return s;
				}).allMatch(s -> s != null);

				// System.out.println("클라이언트 " + Thread.currentThread() + " 종료됨");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void waiting_room() { // 대기실. 플레이어 모집 & 역할 분배
		System.out.println("*** NEW GAME ***");
		System.out.print("Enter the number of players(5~8): ");
		playerNum = scv.nextInt();
		int policeIdx, doctorIdx, mafiaIdx1;
		int mafiaIdx2 = 0;
		int rand = 0;

		mafiaNum = (playerNum < 7) ? 1 : 2; // 마피아 수 결정
		// 랜덤하게 역할 결정 (나머지는 시민)
		mafiaIdx1 = (int) (Math.random() * (playerNum - 1)) + 1; // 범위: 1~playerNum
		while (rand == mafiaIdx1)
			rand = (int) (Math.random() * (playerNum - 1)) + 1;
		policeIdx = rand;
		while (rand == mafiaIdx1 || rand == policeIdx)
			rand = (int) (Math.random() * (playerNum - 1)) + 1;
		doctorIdx = rand;
		if (mafiaNum == 2) {
			while (rand == mafiaIdx1 || rand == policeIdx || rand == doctorIdx)
				rand = (int) (Math.random() * (playerNum - 1)) + 1;
			mafiaIdx2 = rand;
		}

		try (ServerSocket sSocket = new ServerSocket(10000)) {
			for (int j = 1; j <= playerNum; j++) { // 입력한 플레이어 수만큼 클라이언트 대기
				clientSocket = sSocket.accept();
				char role;
				if (j == mafiaIdx1 || j == mafiaIdx2) // 마피아
					role = 'm';
				else if (j == policeIdx) // 경찰
					role = 'p';
				else if (j == doctorIdx)// 의사
					role = 'd';
				else
					role = 'c';

				Player p = new Player(clientSocket, j, role); // 개별스레드 생성. 수락 했을 때의 client 주소와 id를 담는다.
				new Thread(p).start();
				// System.out.println("Player" + j + " entered");
				// Thread t = new Thread(p);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void send_message(String msg) { // 사회자가 플레이어들에게 메시지(msg 인자)를 보냄

	}

	public static void day() { // 낮-플레이어들이 채팅&투표로 용의자 지목
		System.out.println("낮이 되었습니다.");
		System.out.println("플레이어들은 채팅을 통해 용의자를 지목해 주세요. (제한 시간: 2분)");
		// java도 alarm signal 되나?
	}

	public static void night() { // 밤-경찰/의사의 미션 & 마피아의 살인
		System.out.println("밤이 되었습니다.");
	}

	public static void main(String[] args) {
		while (true) {
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
