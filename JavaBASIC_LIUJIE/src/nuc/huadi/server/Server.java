/*
 *华迪实训第八组
 */
package nuc.huadi.server;

import java.net.*;

/**
 * 
 * @author liujie
 * @version 1.0
 */
public class Server {
	/**
	 * 
	 * @param args
	 * @throws Exception 
	 * serverSocket： 与客户端进行连接
	 */
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(8888);
		while (true) {
			Socket socket = serverSocket.accept();
			String ip = socket.getInetAddress().getHostAddress();
			int port = socket.getPort();
			Thread thread = new Thread(new MyThread(socket, serverSocket, ip, port));
			thread.start();
		}
	}
}
