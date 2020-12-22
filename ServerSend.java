import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerSend implements Runnable {
	private Socket clientSocket;
	private int playerId;
	public char role;
	public String msg;
	public char status; // w:wait, s:send
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
		try (PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);) { // , true (autoflush)?
			out.print("You're player" + playerId + ". Role: ");
			if (this.role == 'm')
				out.println("Mafia");
			else if (this.role == 'd')
				out.println("Doctor");
			else
				out.println("Citizen");

			while (true) {
				if (status == 's') {
					out.println(this.msg);
					this.status = 'w';
				} else
					System.out.print("");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
