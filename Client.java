import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		System.out.println("Please wait...");
			try {
				InetAddress localAddress = InetAddress.getLocalHost();
				try (Socket cSocket = new Socket(localAddress, 10000);//Socket cSocket = new Socket("127.0.0.1", 9000);
					PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
					BufferedReader br = new BufferedReader(new InputStreamReader(cSocket.getInputStream()))
					    ) {
					System.out.println("connected!");
					Scanner scv = new Scanner(System.in);
					while (true) {
						System.out.print("메세지 입력 : ");
						String inputLine = scv.nextLine();
						if ("quit".equalsIgnoreCase(inputLine)) {break;}
						out.println(inputLine); // 서버에 키보드 입력 스트링을 전송
						String response = br.readLine();
						System.out.println(response);
					}
					scv.close();
				 }
			} catch (IOException ex) {
			}
	}
}
