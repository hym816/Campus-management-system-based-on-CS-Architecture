package Server.Public;

import Server.Login.LoginPage;
import Server.Public.Message.MessageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
	private Client c;
	private static ClientController instance;
	private static Socket socket;
	public ClientController() throws IOException
	{
		c = Client.getInstance();
	}

	public void sendMessage(Message message) throws IOException {
		c.sendMessage(message);
	}

	public Message receiveMessage() throws IOException, ClassNotFoundException {
		return c.receiveMessage();
	}

	public void closeConnection() throws IOException, ClassNotFoundException
	{
		c.sendMessage(new Message(Message.MessageType.end));
		Message receivedMessage = c.receiveMessage();
		if(receivedMessage.getType().equals(MessageType.end))
			c.closeConnection();
	}

	public List<Serializable> showUserInfo(String id) throws IOException, ClassNotFoundException {
		List<Serializable> showInfo = new ArrayList<>();
		showInfo.add(id);
		Message showInfoMessage = new Message(MessageType.user_info,showInfo);
		c.sendMessage(showInfoMessage);
		Message receiveMessage = c.receiveMessage();
		System.out.println(receiveMessage);
		System.out.println(66);
		return receiveMessage.getContent();
	}
	public int loginTest(String id, String password) throws IOException, ClassNotFoundException {
		//获取登陆信息
		List<Serializable> loginInfo = new ArrayList<>();
		loginInfo.add(id);
		loginInfo.add(password);
		Message loginMessage = new Message(MessageType.login_info,loginInfo);
		//发送消息
		c.sendMessage(loginMessage);
		//接收消息
		Message receiveMessage = c.receiveMessage();
		MessageType messageType = receiveMessage.getType();
		System.out.println("学生登陆响应："+receiveMessage.getType());
		switch(messageType){
			case success_student:
				return 1;
			case success_teacher:
				return 2;
			case success_manager:
				return 3;
			default:
				return 0;
		}
	}
	public int registerTest(String id, String password, String identity, byte[] imageUser) throws IOException, ClassNotFoundException {
		List<Serializable> registerInfo =new ArrayList<>();
		registerInfo.add(id);
		registerInfo.add(password);
		registerInfo.add(identity);
		registerInfo.add(imageUser);
		Message registerMessage = new Message(MessageType.register_info,registerInfo);
		//发送消息
		c.sendMessage(registerMessage);
		//接收消息
		Message receiveMessage = c.receiveMessage();
		MessageType messageType = receiveMessage.getType();
		System.out.println("学生注册响应："+receiveMessage.getType());
		switch(messageType){
			case success_student:
				return 1;
			case success_teacher:
				return 2;
			case success_manager:
				return 3;
			case registerError_1:
				return -1;
			default:
				return 0;
		}
	}
	public int reviseTest(String id, String password, String identity,byte[] imageBytes) throws IOException, ClassNotFoundException {
		List<Serializable> reviseInfo =new ArrayList<>();
		reviseInfo.add(id);
		reviseInfo.add(password);
		reviseInfo.add(identity);
		reviseInfo.add(imageBytes);
		Message reviseMessage = new Message(MessageType.revise_info,reviseInfo);
		//发送消息
		c.sendMessage(reviseMessage);
		//接收消息
		Message receiveMessage = c.receiveMessage();
		MessageType messageType = receiveMessage.getType();
		System.out.println("学生修改响应："+messageType);
		switch(messageType){
			case success_student:
				return 1;
			case success_teacher:
				return 2;
			case success_manager:
				return 3;
			case reviseError_1:
				return -1;
			default:
				return 0;
		}
	}
	public int logoffTest(String id, String password, String identity) throws IOException, ClassNotFoundException {
		List<Serializable> logoffInfo =new ArrayList<>();
		logoffInfo.add(id);
		logoffInfo.add(password);
		logoffInfo.add(identity);
		Message logoffMessage = new Message(MessageType.logoff_info,logoffInfo);
		//发送消息
		c.sendMessage(logoffMessage);
		//接收消息
		Message receiveMessage = c.receiveMessage();
		MessageType messageType = receiveMessage.getType();
		System.out.println("学生注销响应："+messageType);
		switch(messageType){
			case success_student:
				return 1;
			case success_teacher:
				return 2;
			case success_manager:
				return 3;
			case logoffError_0:
				return 0;
			default:
				return 0;
		}
	}

	public void createAndShowGUI() {
		JFrame frame = new JFrame("Server Connection");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 150);
		frame.setLayout(new GridLayout(3, 2));

		JTextField ipField = new JTextField("127.0.0.1"); // 默认IP地址
		JTextField portField = new JTextField("8082"); // 默认端口

		frame.add(new JLabel("Server IP:"));
		frame.add(ipField);
		frame.add(new JLabel("Port:"));
		frame.add(portField);

		JButton connectButton = new JButton("Connect");
		frame.add(connectButton);

		JLabel statusLabel = new JLabel("Not connected");
		frame.add(statusLabel);

		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String serverIp = ipField.getText();
				int port = Integer.parseInt(portField.getText());

				// 在新线程中进行服务器连接，避免阻塞 GUI
				new Thread(() -> {
					if (connectToServer(serverIp, port)) {
						SwingUtilities.invokeLater(() -> {
							statusLabel.setText("Connected successfully");
							frame.setVisible(false); // 连接成功后隐藏窗口
							// 连接成功后初始化 LoginPage
							try {
								new LoginPage(new ClientController());
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
						});
					} else {
						SwingUtilities.invokeLater(() -> statusLabel.setText("Connection failed"));
					}
				}).start();
			}
		});

		frame.setVisible(true);
	}


	public static boolean connectToServer(String serverIp, int port) {
		try {
			socket = new Socket(serverIp, port);
			System.out.println("Connected to server successfully!");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) throws ClassNotFoundException
	{

	}







}
			

	
	
	
