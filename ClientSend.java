import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSend implements Runnable {
	private Socket sock;

	ClientSend(Socket sock) {
		this.sock = sock;
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				PrintWriter out = new PrintWriter(sock.getOutputStream(), true);) {

			System.out.println("accepted!"); 
			String str;
			
			while (true) {
				//System.out.print("");
				str = br.readLine();
				if ("quit".equalsIgnoreCase(str))
					break;
				out.println(str); // to server
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
