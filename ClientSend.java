import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSend implements Runnable {
	private Socket sock;

	ClientSend(Socket sock) {
		this.sock = sock;
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				PrintWriter out = new PrintWriter(sock.getOutputStream(), true);) { //,true(autoflush)

			System.out.println("서버에 연결됨!"); 
			String str;
			
			while (true) {
				System.out.print("메세지 입력 : ");
				str = br.readLine();
				if ("quit".equalsIgnoreCase(str))
					break;
				out.println(str); // 서버에 키보드 입력 스트링을 전송
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
