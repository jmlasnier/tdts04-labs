package lab2;
import java.net.*; 
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//http://requestbin.net/r/1gtru6d1
//http://iberianodonataucm.myspecies.info/
//http://diptera.myspecies.info/
//http://zebroid.ida.liu.se/goodtest2.html
//http://zebroid.ida.liu.se/SpongeBob.html
//http://zebroid.ida.liu.se/badtest1.html

public class NetNinny {
	final static int BUFFER_SIZE = 1024;
	final static int PORT = 80;
    final static String ERROR_PAGE = "HTTP/1.1 301 Moved Permanently\nContent-Length: 145\nLocation: http://zebroid.ida.liu.se/error1.html\nConnection: keep-alive\nContent-Type: text/html";
	final static String[] blackList = {"odonata", "SpongeBob"};
	
	boolean legalRequest = true;
	
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		try {
			int portNumber = 0;
			while (portNumber < 1024) {
				System.out.println("Enter port number (must be above 1024): ");
				portNumber = sc.nextInt();
				if (portNumber < 1024) {
					System.out.println("Port number must be above 1024!");
				}
			}
			ServerSocket proxy = new ServerSocket(portNumber);
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
					byte[] requestBuffer = new byte[65536];
					
					//receive request from browser
					InputStream input = socketServer.getInputStream();
					input.read(requestBuffer);
					String inputRequest = new String(requestBuffer);
					String header = inputRequest.split("\n")[0];
					
//					System.out.println("input: ");
//					System.out.println(input);
////					
//					System.out.println("inputRequest: ");
//					System.out.println(inputRequest);
//					
//					System.out.println("header: ");
//					System.out.println(header);
//					
					//if illegal url, redirect and send error page to browser
					if (isBlackListed(header)) {
                        String redirect = getErrorPage();
                        System.out.println(new String(redirect.getBytes()));

                        OutputStream clientOutputStream = socketServer.getOutputStream();
                        clientOutputStream.write(redirect.getBytes());
                        socketServer.close();
                        return;  
					}
					
					//string manip to get port number and host name
					
					String host = getHostName(header);				
					
					//send request to web server
					Socket socketClient = new Socket(host, PORT);
					OutputStream out = socketClient.getOutputStream();
					out.write(requestBuffer);
					
					
					//handle response from web server
					byte[] responseBuffer = new byte[BUFFER_SIZE];
					
//					String fullResponseBuffer = "";
					InputStream inputWebResponse = socketClient.getInputStream();
					inputWebResponse.read(responseBuffer);
					String fullResponseBuffer = new String(responseBuffer);
					
//					while(true) {
//						if(responseBuffer[0] != '\u0000') {
//							fullResponseBuffer += new String(responseBuffer);
//						}
//						else {
//							break;
//						}
//					}
//					String inputResponse = new String(responseBuffer);

					System.out.println("RESPONSE:");
					System.out.println(fullResponseBuffer);
					
					
					
					//filter incoming data
					if (fullResponseBuffer.contains("Content-Type: text")) {
						if (isBlackListed(fullResponseBuffer)) {
	                        String redirect = getErrorPage();
	                        System.out.println(new String(redirect.getBytes()));

	                        OutputStream clientOutputStream = socketServer.getOutputStream();
	                        clientOutputStream.write(redirect.getBytes());
	                        socketServer.close();
	                        return;  
						}
					}
					
						
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
	
	public static boolean isBlackListed(String request ) {
		for (String badWord: blackList) {
			if (request.contains(badWord)) return true;
		}
		return false;
	}
	
	public static String getErrorPage() {
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        Date date = new Date();
        String dateText = dateFormat.format(date) + " GMT\n";
        dateText = dateText.replace(".", "");
        return ERROR_PAGE + "\nDate: " + dateText;
	}


}
