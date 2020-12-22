import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerReceive implements Runnable {
	private Socket clientSocket;
	private int playerId;
	public String receivedMsg;
	public boolean received;
	public boolean alive;

	ServerReceive(Socket clientSocket, int id) {
		this.playerId = id;
		this.clientSocket = clientSocket;
		this.receivedMsg = "";
		this.received = false;
		this.alive = true;
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
			while (true) {
				this.receivedMsg = br.readLine();
				System.out.println("[Player" + playerId + "]: " + this.receivedMsg);
				this.received = true; // 받았다는 사실을 Server에서 알게끔
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
