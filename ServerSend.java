import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerSend implements Runnable {
	private Socket clientSocket;
	private int playerId; // 해당 플레이어 id
	public char role;
	public String msg;
	public char status; // w:wait.기다리기 s:send 보내기
	public boolean alive;

	ServerSend(Socket clientSocket, int id, char r) {
		this.playerId = id;
		this.clientSocket = clientSocket;
		this.role = r;
		this.msg = "";
		this.status = 'w';
		this.alive = true;
	}

	@Override
	public void run() {
		System.out.println("Player " + this.playerId + " entered. ");
		// System.out.println("role: " + this.role);
		try (PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);) { // , true (autoflush)?
			out.print("You're player" + playerId + ". Role: ");
			if (this.role == 'm')
				out.println("Mafia");
			else if (this.role == 'p')
				out.println("Police");
			else if (this.role == 'd')
				out.println("Doctor");
			else
				out.println("Citizen");

			while (true) {
				if (status == 's') {
					// System.out.println(playerId + ": 's'상태로 바뀜");
					out.println(this.msg);
					this.status = 'w';
					// System.out.println(playerId + ": 'w'상태로 바뀜");
				} else {
					System.out.print("");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
