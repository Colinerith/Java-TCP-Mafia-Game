mport java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TEServer implements Runnable {
	private static Socket clientSocket;
	
	public TEServer(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	public static void main(String[] args) {
		System.out.println("쓰레드 에코 서버 시작");
		try(ServerSocket sSocket = new ServerSocket(10000)){
			//계속 접속하는 클라이언트들에 대한 무한 서비스를 하려면
			while(true) {
				System.out.println("연결 대기 중...");
				clientSocket = sSocket.accept();
				TEServer tes = new TEServer(clientSocket);//개별스레드 생성. 수락 했을 때의 client 주소(정보)가 담김
				new Thread(tes).start();
			}			
		} catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("스레드 에코 서버 종료");
	}

	@Override
	public void run() {
		System.out.println("클라이언트 " + Thread.currentThread() + " 연결됨");
		try (
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		){
//			String inputLine;
//			while((inputLine = br.readLine())!=null) {
//				System.out.println(Thread.currentThread().getName() + "클라이언트 요청: "+inputLine);
//				out.println(inputLine); //메아리
//			}
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

