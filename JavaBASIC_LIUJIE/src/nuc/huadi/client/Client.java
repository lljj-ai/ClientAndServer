/*
 *华迪实训第八组
 */
package nuc.huadi.client;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

/**
 * 
 * @author liujie
 * @version 1.0 
 * socket：与服务器进行通信
 * receiver：接收者的信息
 */
public class Client {
	static Socket socket = null;
	static StringBuilder receiver = null;

	/**
	 * 
	 * @param args frame:窗口对象
	 * 
	 */
	public static void main(String[] args) {
		MyFrame frame = new MyFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		int w = Toolkit.getDefaultToolkit().getScreenSize().width;
		int h = Toolkit.getDefaultToolkit().getScreenSize().height;
		// 窗口居中
		frame.setLocation((w - frame.WIDTH) / 2, (h - frame.HEIGHT) / 2);
		frame.setVisible(true);

		try {
			// 连接服务器，创建输入流
			socket = new Socket(InetAddress.getLocalHost(), 8888);
			InputStream inputStream = socket.getInputStream();
			byte[] buf = new byte[1024];
			int len = inputStream.read(buf);
			// 连接成功的信息显示到聊天框中
			String meString = new String(buf, 0, len);
			frame.jtaChat.append(meString);
			frame.jtaChat.append("\n");

			while (true) {
				// 读取服务器传入的消息
				inputStream = socket.getInputStream();
				len = inputStream.read(buf);
				String msg = new String(buf, 0, len);
				// 接收相应的类型，OnlineListUpdate：更新在线列表，chat：聊天
				String type = msg.substring(0, msg.indexOf("/"));
				// 接收更新后的消息内容或在线列表
				String content = msg.substring(msg.indexOf("/") + 1);
				// 更新在线列表
				if (type.equals("OnlineListUpdate")) {
					DefaultTableModel model = (DefaultTableModel) frame.jtbOnline.getModel();
					model.setRowCount(0);
					String[] onlinelist = content.split(",");
					// 采用增强型for循环遍历每个客户端信息，添加到列表中去，自己的信息不添加
					for (String mesg : onlinelist) {
						String[] strings = new String[2];
						// 如果是自己则不在名单中显示
						if (mesg.equals(InetAddress.getLocalHost().getHostAddress() + ":" + socket.getLocalPort()))
							continue;
						strings[0] = mesg.substring(0, mesg.indexOf(":"));
						strings[1] = mesg.substring(mesg.indexOf(":") + 1);
						// 添加当前在线者之一
						model.addRow(strings);
					}
					// 渲染模型
					DefaultTableCellRenderer dmodel = new DefaultTableCellRenderer();
					// 表格居中显示
					dmodel.setHorizontalAlignment(JLabel.CENTER);
					frame.jtbOnline.setDefaultRenderer(Object.class, dmodel);
				}
				// 聊天
				else if (type.equals("Chat")) {
					String sender = content.substring(0, content.indexOf("/"));
					String word = content.substring(content.indexOf("/") + 1);
					// 在聊天窗打印聊天信息
					frame.jtaChat.append(frame.sdf.format(new Date()) + "\n来自 " + sender + ":\n" + word + "\n\n");
					// 显示最新消息
					frame.jtaChat.setCaretPosition(frame.jtaChat.getDocument().getLength());
				}
			}
		} catch (Exception e) {
			frame.jtaChat.append("服务器连接中断！！！！\n");
			e.printStackTrace();
		}
	}
}
