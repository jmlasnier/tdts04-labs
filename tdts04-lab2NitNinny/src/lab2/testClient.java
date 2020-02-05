package lab2;
import java.net.Socket;

public class testClient {

	public static void main(String[] args) {
		
		try {
			System.out.println("Client started");
			Socket soc = new Socket("localhost", 9806);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
