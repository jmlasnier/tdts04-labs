package lab2;
import java.net.*; 
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//http://requestbin.net/r/1gtru6d1
//http://iberianodonataucm.myspecies.info/
//http://diptera.myspecies.info/
//https://www.ida.liu.se/~TDTS04/labs/2011/ass2/goodtest1.txt
//https://www.ida.liu.se/~TDTS04/labs/2011/ass2/goodtest2.html
//https://www.ida.liu.se/~TDTS04/labs/2011/ass2/SpongeBob.html
//https://www.ida.liu.se/~TDTS04/labs/2011/ass2/badtest1.html

public class NetNinny {
	final static int BUFFER_SIZE = 65536;
	final static int PORT = 80;
    final static String ERROR_PAGE = "HTTP/1.1 301 Moved Permanently\nContent-Length: 145\nLocation: http://zebroid.ida.liu.se/error1.html\nConnection: keep-alive\nContent-Type: text/html";
	final static String[] blackList = {"odonata", "SpongeBob"};
	
	boolean legalRequest = true;
	
	
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
					
					InputStream inputWebResponse = socketClient.getInputStream();
					inputWebResponse.read(responseBuffer);
					String inputResponse = new String(responseBuffer);

//					System.out.println("RESPONSE:");
//					System.out.println(inputResponse);
					
					//filter
					
						
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
