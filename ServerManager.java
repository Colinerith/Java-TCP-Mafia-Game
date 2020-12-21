import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerManager implements Runnable {
	private static Socket clientSocket;
	private int playerId; // 해당 플레이어 id
	public String receivedMsg;
	public char status; // w:수신 대기, r:수신
	public boolean received;

	public void run() {
		System.out.println("player" + playerId + " accepted!");			
		try(
				BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
		){		
			String inputLine;
			while((inputLine = br.readLine()) != null) {
				System.out.println("[" + Thread.currentThread().getName() + "] 클라이언트 요청: " + inputLine);
				out.println(inputLine);
			}
			System.out.println("클라이언트 : " + Thread.currentThread() + " 종료됨!");
		}catch(IOException ex) {
			ex.printStackTrace();
		}	
		
	}
}
