import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.Scanner;

public class Server implements Runnable {
	private static Socket clientSocket;
	
	public Server(Socket clientSocket) {
		this.clientSocket = clientSocket;
	} 
	public static void main(String[] args) {
		Scanner scv = new Scanner(System.in);
		
		// 이 아래부터 함수로 따로 빼야할 것 같
		System.out.println("Game start...");
		System.out.print("Enter the number of players(5~8): ");
		int playerNum = scv.nextInt();
		//int temp = playerNum;
		try(ServerSocket sSocket = new ServerSocket(10000)){
			//계속 접속하는 클라이언트들에 대한 무한 서비스를 하려면
			//while(true) {
			for(int j=0; j < playerNum; j++){ //입력한 플레이어 수만큼 클라이언트 대기
				System.out.println("연결 대기 중...");
				clientSocket = sSocket.accept();
				Server tes = new Server(clientSocket);//개별스레드 생성. 수락 했을 때의 client 주소(정보)가 담김
				new Thread(tes).start();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("The Winner is: ");
		System.out.println("Restart game? (Y/N): ");
		char restart = sc.next().charAt(0);
		
		// 함수 다시 부름
		//if(restart == 'Y') {
		//	
		//}
	}

	@Override
	public void run() {
		System.out.println("클라이언트 " + Thread.currentThread() + " 연결됨");
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
			stream
			.map(s -> {
				System.out.println(Thread.currentThread().getName() + "클라이언트 요청: " + s);
				out.println(s);
				return s;
			})
			.allMatch( s -> s != null);
			
			System.out.println("클라이언트 " + Thread.currentThread() + " 종료됨");
		} catch(IOException ex){
			ex.printStackTrace();
		}		
	}
}

