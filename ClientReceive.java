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

public class ClientReceive implements Runnable { 
	private Socket sock;

	ClientReceive(Socket sock) {
		this.sock = sock;
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));) {
			String str;
			
			while (true) {
				str = br.readLine();
				System.out.println("[System]: " + str);
			}

			// System.out.println("클라이언트 " + Thread.currentThread() + " 종료됨");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}
