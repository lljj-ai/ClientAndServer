/*
 *华迪实训第八组
 */
package nuc.huadi.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nuc.huadi.tools.DateTools;

/**
 * 
 * @author liujie
 * @version 1.0
 * socket：当前客户端socket
 * serverSocket：当前客户端serverSocket
 * ip：当前客户端的ip地址
 * port：当前客户端的端口号
 * messge：存放ip和port连接的字符串
 * messages：存放messge的List集合
 * MessAndThread：存放messge和线程的Map集合，messge为键，当前线程为值
 */
class MyThread implements Runnable {
	private Socket socket = null;
	private ServerSocket serverSocket = null;
	private String ip = null;
	private int port = 0;
	private String message = null;
	static List<String> messages = new ArrayList<String>();
	static Map<String, MyThread> MessAndThread = new HashMap<String, MyThread>();

	/**
	 * 
	 * @param socket
	 * @param serverSocket
	 * @param ip
	 * @param port         构造方法
	 */
	public MyThread(Socket socket, ServerSocket serverSocket, String ip, int port) {
		this.socket = socket;
		this.serverSocket = serverSocket;
		this.ip = ip;
		this.port = port;
		message = ip + ":" + port;
	}

	/**
	 * 1.创建输入流、输出流
	 * 2.客户端登录后显示成功连接服务器 
	 * 3.获取客户端传入的操作类型：exit/(下线)，chat/(聊天)
	 * 4.退出：实时更新在线用户列表 5.聊天：转发消息至指定客户端
	 * @function 实现Runnable抽象方法
	 */
	@Override
	public void run() {
		messages.add(message);
		MessAndThread.put(message, this);
		//设置时间格式
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			//创建输入输出流
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			DateTools dateConfig=new DateTools();
			String successString = dateConfig.Date() + "\n成功连接服务器...\n服务器IP: "
					+ serverSocket.getInetAddress().getLocalHost().getHostAddress()  + ", 端口: 8888\n客户端IP: " + ip + ", 端口: " + port + "\n";
			//将连接成功的信息传回客户端
			outputStream.write(successString.getBytes());
			//实时更新在线客户端列表
			updateOnlineList(outputStream);
			byte[] space = new byte[1024];
			int length = 0;
			while (true) {
				//读取客户端传入的数据
				length = inputStream.read(space);
				String mes = new String(space, 0, length);
				//获取要进行的操作类型
				String messagetype = mes.substring(0, mes.indexOf("/"));
				//获取消息内容
				String content = mes.substring(mes.indexOf("/") + 1);
				//退出操作
				if (messagetype.equals("exit")) {
					messages.remove(messages.indexOf(message));
					MessAndThread.remove(message);
					//更新在线客户端列表
					updateOnlineList(outputStream);
					//在控制台打印已退出的客户端的信息
					System.out.println("ip：" + ip + "端口号：" + port + "的用户，已退出");
					break;
					//聊天操作
				} else if (messagetype.equals("Chat")) {
					//要发往的客户端的地址
					String sites[] = content.substring(0, content.indexOf("/")).split(",");
					//聊天的内容
					String words = content.substring(content.indexOf("/") + 1);
					//发送给指定客户端
					chatOnlineList(outputStream, message, sites, words);
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * @param outputStream:当前客户端输出流
	 * @throws Exception
	 * @Function进行实时更新在线列表
	 */
	public void updateOnlineList(OutputStream outputStream) throws Exception {
		//通过增强型for循环遍历当前在线客户端，并获取在线客户端的输出流  
		for (String keys : messages) {
			outputStream = MessAndThread.get(keys).socket.getOutputStream();
			//通过StringBuilder进行客户端信息的拼接用“，”隔开
			StringBuilder stringBuilder = new StringBuilder("OnlineListUpdate/");
			for (String mes : messages) {
				stringBuilder.append(mes);
				if (messages.indexOf(mes) != messages.size() - 1)
					stringBuilder.append(",");
			}
			outputStream.write(stringBuilder.toString().getBytes());
		}
	}

	/**
	 * 
	 * @param outputStream:当前客户端输出流
	 * @param message:当前客户端地址
	 * @param sites:要发往的客户端的地址
	 * @param words：要发送给指定客户端的内容
	 * @throws Exception
	 * @function 给指定客户端发送聊天消息
	 */
	public void chatOnlineList(OutputStream outputStream, String message, String sites[], String words)
			throws Exception {
		/*通过增强型for循环遍历要发往的客户端的地址，发送给指定客户端内容*/
		for (String site : sites) {
			outputStream = MessAndThread.get(site).socket.getOutputStream();
			outputStream.write(("Chat/" + message + "/" + words).getBytes());
		}
	}
}
