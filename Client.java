import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	public static void main(String[] args) {
		System.out.println("Please wait...");
		try {
			InetAddress localAddress = InetAddress.getLocalHost();
			try (Socket cSocket = new Socket(localAddress, 10000);) { 

				ClientSend cs = new ClientSend(cSocket);
				ClientReceive cr = new ClientReceive(cSocket);
				Thread ts = new Thread(cs);
				Thread tr = new Thread(cr);

				ts.start();
				tr.start();
				
				while(true) {
					//
				}
			}
		} catch (IOException ex) {
		}
	}
}
