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

public class ServerReceive implements Runnable {
	private Socket clientSocket;
	private int playerId; // 해당 플레이어 id
	public String receivedMsg;
	// private char role;
	public char status; // w:수신 대기, r:수신
 
	ServerReceive(Socket clientSocket, int id, char r) {
		this.playerId = id;
		this.clientSocket = clientSocket;
		// this.role = r;
		this.receivedMsg = "";
		this.status = 'w';
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
			while (true) {
				if (status == 'r') {
					receivedMsg = br.readLine();
					System.out.println(receivedMsg);
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}
