package lab2;
import java.net.*;
import java.io.*;

public class NetNinny {
	final static int BUFFER_SIZE = 65536;
	final static String PORT = "80";
	
	public static void main(String[] args){
		try {
			ServerSocket proxy = new ServerSocket(2006);
			
			while(true) {
				Socket socketClient = proxy.accept();
				proxy_server(socketClient);
			}
			
		}
		catch(IOException e) {
			System.err.println(e);
		}
	}
	
	public static void proxy_server(Socket client) {
		new Thread(new Runnable() {
			public void run() {
				try {
					byte[] requestBuffer = new byte[BUFFER_SIZE];
					InputStream input = client.getInputStream();
					input.read(requestBuffer);
					String inputRequest = new String(requestBuffer);
					System.out.println(inputRequest);

					//get port number and host name
					String[] splittedInput = inputRequest.split("\n");

					String portNumber = PORT;
					String host = new String();
					
					String header = splittedInput[0];

					System.out.println("header:");
					System.out.println(header);
					
					if(header.contains("http")) {
						String[] test = header.split("/");
						String almostHost = test[2];
						host = almostHost.split(":")[0];		
					} else {
						String new1 = header.split(" ")[1];
						String new2 = new1.split("/")[0];
						host = new2.split(":")[0];
					}
					System.out.println("host: " + host);
					System.out.println("port: " + portNumber);

					//send request to web server
					
					Socket socketClient = new Socket(host, Integer.parseInt(portNumber));
					OutputStream out = socketClient.getOutputStream();
					out.write(requestBuffer);
					//handle response from web server

					byte[] responseBuffer = new byte[BUFFER_SIZE];

					InputStream inputWebResponse = socketClient.getInputStream();
					input.read(responseBuffer);
					String inputResponse = new String(requestBuffer);

					System.out.println("response:");
					System.out.println(inputResponse);
					System.out.println("succes!!");
					
					
					
					//send response to browser
					socketClient.close();
				}
				catch(IOException e) {
					System.err.println(e);
				}
			}
		}).start();
	}	
	
}
