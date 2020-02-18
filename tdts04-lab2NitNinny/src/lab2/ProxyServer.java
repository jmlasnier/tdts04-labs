package lab2;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ProxyServer {

	public static void main(String[] args) {
		
	}
	
	public static void runServer(String host, int localport) throws IOException{
		// Create ServerSocket to listen for connections
		ServerSocket ss = new ServerSocket(localport);
		// Create buffers for client-to-server and server-to-client communication
		// Make one final so it can be used in an anonymous class below
		// Note the assumptions about volume of traffic in each direction...
		final byte[] request = new byte[1024];
		byte[] reply = new byte[4096];
		// This is a server that never returns, so enter an infinite loop.
		while (true) {
			// Variables to hold sockets to client and server
			Socket client = null, server = null;
			try {
				// Wait for a connection on local port
				client = ss.accept();
				// Get client streams, make them final
				// so they can be used in anonymous thread below
				final InputStream from_client = client.getInputStream();
				final OutputStream to_client = client.getOutputStream();
				// Make connection to real server
				// If can't connect, send error to client
				// disconnect, then continue waiting for another connection
				try { server = new Socket(host, localport); }
				catch (IOException e) {
					PrintWriter out = new PrintWriter(new OutputStreamWriter(to_client));
					out.println("Proxy server cannot connect to " + host + ":\n" + e);
					out.flush();
					client.close();
					continue;
				}
				// Get server streams
				final InputStream from_server = server.getInputStream();
				final OutputStream to_server = server.getOutputStream();
				// Make a thread to read client's requests and pass them to server
				// Must use separate thread b/c requests and responses may be asynchronous
				new Thread() {
					public void run() {
						int bytes_read;
						try {
							while ((bytes_read = from_client.read(request)) != -1) {
								to_server.write(request, 0, bytes_read);
								System.out.println(bytes_read + "to_server---->" + new String(request, "UTF-8") + "<---");
								to_server.flush();
							}
						}
						catch (IOException e) {
							// client closed connection, so close connection to server
							// causes server-to-client loop in main thread exit
							try { to_server.close(); } catch (IOException er) {}
						}
					}
				}.start();
				// Meanwhile, in main thread, read server's responses + pass them back to client
				// in parallel with client-to-server request thread above
				int bytes_read;
				try {
					while ((bytes_read = from_server.read(reply)) != -1) {
						try {
							Thread.sleep(1);
							System.out.println(bytes_read + "to_client--->" + new String(request, "UTF-8") + "<---");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						to_client.write(reply, 0, bytes_read);
						to_client.flush();
					}
				}
				catch (IOException e) {}
				// server closed connection to us, close our connection to client
				to_client.close();
			}
			catch (IOException e ) {System.err.println(e); }
			// Close sockets no matter what happens each time through loop
			finally {
				try {
					if (server != null) server.close();
					if (client != null) client.close();
				}
				catch (IOException e) {}
			}
		}
	}
}
