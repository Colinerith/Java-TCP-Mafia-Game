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
	private static int playerNum; //플레이어의 수
	private static int mafia1, mafia2; //마피아인 플레이어들의 id
	private static int police; //경찰의 id
	//private static 
	//volatile private static
	static Scanner scv = new Scanner(System.in);
	
	private static class Player implements Runnable {
		int playerId;  // 해당 플레이어 id
		
		Player(Socket clientSocket, int id){
			this.playerId = id;
		}
		
		@Override
		public void run() {
			System.out.println("Player " + Thread.currentThread().getName() + " has connected.");
			try (
				BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			){
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
					System.out.println(Thread.currentThread().getName() + "클라이언트 요청: " + s);
					out.println(s);
					return s;
				}).allMatch( s -> s != null);
				
				//System.out.println("클라이언트 " + Thread.currentThread() + " 종료됨");
			} catch(IOException ex){
				ex.printStackTrace();
			}		
		}
	}

	public static void waiting_room() { // 대기실. 플레이어들이 모두 모이면 시작
		System.out.println("*** NEW GAME ***");
		System.out.print("Enter the number of players(5~8): ");
		int playerNum = scv.nextInt();
		
		int random = (int)((Math.random()*10000)%10);
		
		try(ServerSocket sSocket = new ServerSocket(10000)){
			for(int j=1; j <= playerNum; j++){ //입력한 플레이어 수만큼 클라이언트 대기
				//System.out.println("연결 대기 중...");
				clientSocket = sSocket.accept();
				Player p = new Player(clientSocket, j); //개별스레드 생성. 수락 했을 때의 client 주소와 id를 담는다.
				new Thread(p).start();
				//Thread t = new Thread(p);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void send_message(String msg) { // 사회자가 플레이어들에게 메시지(msg 인자)를 보냄
		
	}
	
	public static void day() { // 낮-플레이어들이 채팅&투표로 용의자 지목
		
	}
	
	public static void night() { // 밤-경찰/의사의 미션 & 마피아의 살인
		
	}
	
	public static void main(String[] args) {
		while(true) {
			waiting_room(); // 플레이어 수를 입력받고 그 수만큼 플레이어가 찰 때까지 기다림
			
			while(조건) { // 조건 : 마피아수=0 or 마피아수:시민수 = 1:1
				day();
				night();
			}
			
			System.out.println("The Winner is: ");
			System.out.println("Restart game? (Y/N): ");
			char restart = scv.next().charAt(0);
			if(restart == 'N')
				break;
		}
	}


}

