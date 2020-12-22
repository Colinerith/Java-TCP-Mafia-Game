import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerReceive implements Runnable {
	private Socket clientSocket;
	private int playerId; // 해당 플레이어 id
	public String receivedMsg;
//	public char status; // w:수신 대기, r:수신
	public boolean received;
//	public char send; // 채팅을 누구에게 보낼 것인지. 's':서버만 볼 수 있음, 'a':모두에게
	public boolean alive;

	ServerReceive(Socket clientSocket, int id) {
		this.playerId = id;
		this.clientSocket = clientSocket;
		this.receivedMsg = "";
//		this.status = 'w';
		this.received = false;
		this.alive = true;
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
			while (true) {
				// if (status == 'r') {
				// System.out.println(playerId + ": receive 상태로 바뀜.");
				this.receivedMsg = br.readLine();
				System.out.println("[Player" + playerId + "]: " + this.receivedMsg);
				this.received = true; // 받았다는 사실을 Server에서 알게끔
				// }
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
