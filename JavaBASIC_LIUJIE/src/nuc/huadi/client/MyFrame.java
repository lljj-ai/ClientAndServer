/*
 *华迪实训第八组
 */
package nuc.huadi.client;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 * 
 * @author liujie
 * @version 1.0 
 * sdf：时间格式 
 * send：按钮(发送) 
 * clear：按钮(清屏) 
 * exit：按钮(退出) 
 * label：标签
 * input：文本输入框
 * jtaChat：聊天消息框
 * jspChat：聊天框的滚动窗
 * jtbOnline：在线列表所在表格
 * jspOnline：在线列表框滚动窗
 */
class MyFrame extends JFrame {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	final int WIDTH = 700;
	final int HEIGHT = 700;
	JButton send = new JButton("发送");
	JButton btnClear = new JButton("清屏");
	JButton btnExit = new JButton("退出");
	JLabel label = new JLabel("请选择要发送的客户端：");
	JTextArea input = new JTextArea();
	JTextArea jtaChat = new JTextArea();
	String[] colTitles = { "IP", "端口" };
	String[][] rowData = null;
	// 表格只能"只读"
	JTable jtbOnline = new JTable(new DefaultTableModel(rowData, colTitles) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	});
	JScrollPane jspChat = new JScrollPane(jtaChat);
	JScrollPane jspOnline = new JScrollPane(jtbOnline);

	/**
	 * 构造方法 进行相关组件的连接
	 */
	public MyFrame() {
		setTitle("华迪实训-聊天工具");
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		// 自定义布局
		setLayout(null);
		send.setBounds(20, 600, 100, 60);
		btnClear.setBounds(140, 600, 100, 60);
		btnExit.setBounds(260, 600, 100, 60);
		label.setBounds(20, 420, 300, 30);
		send.setFont(new Font("宋体", Font.BOLD, 18));
		btnClear.setFont(new Font("宋体", Font.BOLD, 18));
		btnExit.setFont(new Font("宋体", Font.BOLD, 18));
		this.add(send);
		this.add(btnClear);
		this.add(btnExit);
		this.add(label);
		input.setBounds(20, 460, 360, 120);
		input.setFont(new Font("宋体", Font.BOLD, 16));
		this.add(input);
		// 聊天消息框自动换行
		jtaChat.setLineWrap(true);
		// 聊天框不可编辑，只用来显示
		jtaChat.setEditable(false);
		jtaChat.setFont(new Font("宋体", Font.BOLD, 16));
		jspChat.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jspChat.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jspChat.setBounds(20, 20, 360, 400);
		this.add(jspChat);
		jspOnline.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jspOnline.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jspOnline.setBounds(420, 20, 250, 400);
		this.add(jspOnline);
		// 发消息操作
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jtaChat.setCaretPosition(jtaChat.getDocument().getLength());
				try {
					// 判断是否有其他客户端
					if (Client.receiver.toString().equals("") == false) {
						jtaChat.append(sdf.format(new Date()) + "\n发往 " + Client.receiver.toString() + ":\n");
						jtaChat.append(input.getText() + "\n\n");
						// 向服务器发送聊天的内容
						OutputStream out = Client.socket.getOutputStream();
						out.write(("Chat/" + Client.receiver.toString() + "/" + input.getText()).getBytes());
					}
				} catch (Exception e) {
				} finally {
					// 发送完以后清除输出的内容
					input.setText("");
				}
			}
		});
		// 清屏操作
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				jtaChat.setText("");
			}
		});
		// 退出操作
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					OutputStream out = Client.socket.getOutputStream();
					out.write("exit/".getBytes());
					System.exit(0);
				} catch (Exception e) {
				}
			}
		});
		// 添加在线列表项被鼠标选中的相应事件
		jtbOnline.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent event) {
				DefaultTableModel dmodel = (DefaultTableModel) jtbOnline.getModel();
				// 选取的客户端
				int[] selectedIndex = jtbOnline.getSelectedRows();
				Client.receiver = new StringBuilder("");
				for (int i = 0; i < selectedIndex.length; i++) {
					Client.receiver.append((String) dmodel.getValueAt(selectedIndex[i], 0));
					Client.receiver.append(":");
					Client.receiver.append((String) dmodel.getValueAt(selectedIndex[i], 1));
					if (i != selectedIndex.length - 1)
						Client.receiver.append(",");
				}
				label.setText("发给：" + Client.receiver.toString());
			}

			@Override
			public void mousePressed(MouseEvent event) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			};

		});
	}
}
