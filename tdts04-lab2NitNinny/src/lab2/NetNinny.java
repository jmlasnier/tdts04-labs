package lab2;
import java.net.*;
import java.io.*;

//http://requestbin.net/r/1gtru6d1

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
					
					
					String header = splittedInput[0];
					String host = findHost(header);
					System.out.println("header:");
					System.out.println(header);
					
					System.out.println("host: " + host);
					System.out.println("port: " + portNumber);

					//send request to web server
					
					Socket socketClient = new Socket(host, Integer.parseInt(portNumber));
					OutputStream out = socketClient.getOutputStream();
					out.write(requestBuffer);
					//handle response from web server

					byte[] responseBuffer = new byte[BUFFER_SIZE];

					InputStream inputWebResponse = socketClient.getInputStream();
					inputWebResponse.read(responseBuffer);
					String inputResponse = new String(requestBuffer);

					System.out.println("response:");
					System.out.println(inputResponse);
					System.out.println("succes!!");
					
					//send response to browser
					
					OutputStream serverOutputStream = client.getOutputStream();
					serverOutputStream.write(responseBuffer);
					
					//close everything
					socketClient.close();
				}
				catch(IOException e) {
					System.err.println(e);
				}
			}
		}).start();
	}
	
	public static String findHost(String header) {
		String host = header;
		int start = host.indexOf("://");
		if (start != -1) {
			start += 3;
		} else {
			start = 4;
		}
		host = host.substring(start);
		int end = host.indexOf(':');
		if (end == -1) {
			end = (host.indexOf('/') != -1) ? host.indexOf('/') : host.length();
		}
		host = host.substring(0, end);
		return host;
	}
	
}
