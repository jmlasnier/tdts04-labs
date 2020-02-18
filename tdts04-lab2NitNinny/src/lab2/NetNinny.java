package lab2;
import java.net.*;
import java.io.*;

//http://requestbin.net/r/1gtru6d1
//http://iberianodonataucm.myspecies.info/
//http://diptera.myspecies.info/

public class NetNinny {
	final static int BUFFER_SIZE = 65536;
	final static int PORT = 80;
	final static String[] blackList = {"odonata"};
	
	
	public static void main(String[] args){
		try {
			ServerSocket proxy = new ServerSocket(2006);
			while(true) {
				Socket socketServer = proxy.accept();
				proxy_server(socketServer);
			}
		}
		catch(IOException e) {
			System.err.println(e);
		}
	}
	
	public static void proxy_server(Socket socketServer) {
		new Thread(new Runnable() {
			public void run() {
				try {
					byte[] requestBuffer = new byte[BUFFER_SIZE];
					
					//receive request from browser
					InputStream input = socketServer.getInputStream();
					input.read(requestBuffer);
					String inputRequest = new String(requestBuffer);

					//string manip to get port number and host name
					String host = getHostName(inputRequest.split("\n")[0]);				
					
					//send request to web server
					Socket socketClient = new Socket(host, PORT);
					OutputStream out = socketClient.getOutputStream();
					out.write(requestBuffer);
					
					//handle response from web server
					byte[] responseBuffer = new byte[BUFFER_SIZE];
					
					InputStream inputWebResponse = socketClient.getInputStream();
					inputWebResponse.read(responseBuffer);
					String inputResponse = new String(requestBuffer);

					System.out.println("RESPONSE:");
					System.out.println(inputResponse);
					
					//filter
					filterResponse(inputResponse);
					
					//send response to browser
					OutputStream serverOutputStream = socketServer.getOutputStream();
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
	
	public static String getHostName(String header) {
		String host = null;
		if(header.contains("http")) {
			String[] test = header.split("/");
			String almostHost = test[2];
			host = almostHost.split(":")[0];		
		} else {
			String new1 = header.split(" ")[1];
			String new2 = new1.split("/")[0];
			host = new2.split(":")[0];
		}
		return host;
	}
	
	public static void filterResponse(String response) {
		
	}
	
	public static boolean isBlackList(String word) {
		return true;
	}
	
}
