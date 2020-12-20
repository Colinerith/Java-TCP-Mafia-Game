import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerSend implements Runnable {
	private Socket clientSocket;
	private int playerId; // 해당 플레이어 id
	private char role;
	public String msg;
	public char status; // w:wait.기다리기  s:send 보내기

	ServerSend(Socket clientSocket, int id, char r) {
		this.playerId = id;
		this.clientSocket = clientSocket;
		this.role = r;
		this.msg = "";
		this.status = 'w';
	}

	@Override
	public void run() {
		System.out.println("Player " + this.playerId + " entered. ");
		// System.out.println("role: " + this.role);
		try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {
			out.print("You're player" + playerId + ". Role: ");
			if (this.role == 'm')
				out.println("Mafia");
			else if (this.role == 'p')
				out.println("Police");
			else if (this.role == 'd')
				out.println("Doctor");
			else
				out.println("Cizizen");

			while (true) {
				if (status == 's') {
					out.println(msg);
					this.status = 'w';
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
