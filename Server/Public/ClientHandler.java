package Server.Public;

import Server.Public.Message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


class ClientHandler implements Runnable {
	private Socket clientSocket;
	private StuInfoManage stuInfoManage;

	public ClientHandler(Socket socket) {
		stuInfoManage = new StuInfoManage();
		this.clientSocket = socket;
	}

	@Override
	public void run() {
		try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

			while (true) {
				// 接收客户端发送的Message对象
				Message receivedMessage = (Message) in.readObject();
				System.out.println("Received from client (" + clientSocket.getInetAddress().getHostAddress() + "): " + receivedMessage.getContent());

				// 如果收到结束连接的消息，则发送结束消息并退出循环
				if (MessageType.end.equals(receivedMessage.getType())) {

					Message endMessage = new Message(MessageType.end);
					out.writeObject(endMessage);
					out.flush();
					System.out.println("Client (" + clientSocket.getInetAddress().getHostAddress() + ") requested to end the connection.");
					break;
				}

				// 发送Message对象回客户端


				MessageType m = receivedMessage.getType();
				switch (m) {
					case login_info:
						loginHandler(receivedMessage, out);
						break;

					case student_info_query:
						queryStudentInfo(receivedMessage, out);
						break;
					case student_info_add:
						addStudentInfo(receivedMessage, out);
						break;
					case student_info_update:
						updateStudentInfo(receivedMessage, out);
						break;
					case grade_query:
						queryGrades(receivedMessage, out);
						break;
					case grade_add:
						addGrade(receivedMessage, out);
						break;
					case grade_update:
						updateGrade(receivedMessage, out);
						break;
					case award_disciplinary_query:
						queryAwardsDisciplinary(receivedMessage, out);
						break;
					case award_disciplinary_add:
						addAwardDisciplinary(receivedMessage, out);
						break;
					case award_disciplinary_update:
						updateAwardDisciplinary(receivedMessage, out);
						break;
					case enrollment_change_query:
						queryEnrollmentChanges(receivedMessage, out);
						break;
					case enrollment_change_add:
						addEnrollmentChange(receivedMessage, out);
						break;
					case enrollment_change_update:
						updateEnrollmentChange(receivedMessage, out);
						break;
					case student_avatar_update:
						updateStudentAvatar(receivedMessage, out);
						break;
					case everyone_id:
						everyoneid(receivedMessage, out);
						break;

					case student_status_change:
						changeStudentStatus(receivedMessage, out);
						break;

					case register_info:
						registerHandler(receivedMessage, out);
						break;
					case revise_info:
						reviseHandler(receivedMessage, out);
						break;
					case logoff_info:
						logoffHandler(receivedMessage, out);
						break;
					case user_info:
						showInfoHandler(receivedMessage, out);
						break;

					case all_product:
						handleAllProducts(out);
						break;
					case addProduct:
						handleAddProduct(receivedMessage, out);
						break;
					case removeProduct:
						handleRemoveProduct(receivedMessage, out);
						break;
					case purchaseProduct:
						handlePurchaseProduct(receivedMessage, out);
						break;
					case increaseProductCount:
						handleIncreaseProductCount(receivedMessage, out);
						break;


					case check_balance:
						getBalance(getCardId(receivedMessage), out);
						break;
					case report_loss:
						reportCardLoss(getCardId(receivedMessage), out);
						break;
					case change_password:
						changeCardPassword(getCardId(receivedMessage), (String) receivedMessage.getContent().get(1), out);
						break;
					case atm_login:
						determinePassword(receivedMessage,out);
						break;
					//case add_account:

					//	break;
					//case delete_account:

					//	break;
					case check_loss:
						isCardLost(getCardId(receivedMessage), out);
						break;
					case deposit:
						deposit(out);
						break;
					case deposit_2:
						deposit_2(getCardId(receivedMessage), (double) receivedMessage.getContent().get(1), out);
						break;
					case determinePassword:
						determinePassword(receivedMessage,out);
						break;
					case withdraw:
						withdraw(receivedMessage,out);
						break;

					case search:
						search(receivedMessage, out);
						break;


					case borrow:
						borrow(receivedMessage, out);
						break;

					case add:
						add(receivedMessage, out);
						break;

					case renew:
						renew(receivedMessage, out);
						break;

					case getCover:
						getCover(receivedMessage, out);
						break;

					case delete:
						delete(receivedMessage, out);
						break;

					case revise:
						revise(receivedMessage, out);
						break;

					case borrowInfo:
						borrowInfo(receivedMessage, out);
						break;

					case topBooks:
						topBooks(receivedMessage, out);
						break;

					case topReaders:
						topReaders(receivedMessage, out);
						break;

					case returnBooks:
						returnBooks(receivedMessage, out);
						break;

					case recommend:
						recommend(receivedMessage, out);
						break;
					case getRecommend:
						getRecommend(receivedMessage, out);
						break;

					case disposeRecommend:
						disposeRecommend(receivedMessage, out);
						break;

					case add_course:
						addCourse(receivedMessage, out);
						break;

					case add_announcement:
						addAnnoouncement(receivedMessage, out);
						break;

					case get_all_announcement:
						getAllAnnouncement(receivedMessage, out);
						break;

					case get_announcement_by_id:
						getAnnouncementById(receivedMessage,out);
						break;

					case get_all_course:

						getAllCourse(receivedMessage, out);
						break;

					case select_course:
                        try {
                            selectCourse(receivedMessage, out);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;

					case unselect_course:
						unselectCourse(receivedMessage,out);
						break;

					case delete_course:
						deleteCourse(receivedMessage,out);
						break;

					case get_course_by_teachername:
						getCourseByTeacherName(receivedMessage,out);
						break;

					case get_students_by_courseid:
						getStudentByCourseId(receivedMessage,out);
						break;

					case save_score:
						saveScore(receivedMessage,out);
						break;

					case get_course_by_studentid:
                        try {
                            getCourseByStudentId(receivedMessage,out);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;

					case modify_announcement:
                        try {
                            modifyAnnouncement(receivedMessage,out);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;

					case delete_announcement:
						deleteAnnouncement(receivedMessage,out);
						break;

					case updata_course:
						updataCourse(receivedMessage,out);
						break;

					case get_course_by_teacherid:
                        try {
                            getCourseByTeacherId(receivedMessage,out);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;

					case get_all_course_manager:
                        try {
                            getAllcourseManager(receivedMessage,out);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;

					case get_students_by_studentid:
						getStudentByStudentId(receivedMessage,out);
						break;

					case get_score_by_studentid:
						sendScoreByStudentId(receivedMessage,out);
						break;

					default:
						Message responseMessage = new Message(MessageType.failure);
						out.writeObject(responseMessage);
						out.flush();
						break;

				}


			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void Login(Message m, ObjectOutputStream out) throws IOException {
		int temp = Login.login(m);
		Message responseMessage = null;
		switch (temp) {
			case 1:
				responseMessage = new Message(MessageType.success_student);
				break;
			case 2:
				responseMessage = new Message(MessageType.success_teacher);
				break;
			case 3:
				responseMessage = new Message(MessageType.success_manager);
				break;
			case 0:
			default:
				responseMessage = new Message(MessageType.failure);
				break;
		}
		out.writeObject(responseMessage);
		out.flush();
	}

	public void queryStudentInfo(Message m, ObjectOutputStream out) throws IOException {
		Message responseMessage = new Message(MessageType.student_info_query,
				StuInfoManage.queryStudentInfo(m));
		out.writeObject(responseMessage);
		out.flush();
	}

	public void addStudentInfo(Message m, ObjectOutputStream out) throws IOException {
		if (StuInfoManage.addStudentInfo(m)) {
			out.writeObject(new Message(MessageType.student_info_add));
			out.flush();
		} else {
			out.writeObject(new Message(MessageType.failure));
			out.flush();
		}
	}

	public void updateStudentInfo(Message m, ObjectOutputStream out) throws IOException {
		StuInfoManage.updateStudentInfo(m);
		out.writeObject(new Message(MessageType.student_info_update));
		out.flush();
	}

	public void queryGrades(Message m, ObjectOutputStream out) throws IOException {
		out.writeObject(new Message(MessageType.grade_query, StuInfoManage.queryGrades(m)));
		out.flush();
	}

	public void addGrade(Message m, ObjectOutputStream out) throws IOException {
		StuInfoManage.addGrade(m);
		out.writeObject(new Message(MessageType.grade_add));
		out.flush();
	}


	public void updateGrade(Message m, ObjectOutputStream out) throws IOException {
		StuInfoManage.updateGrade(m);
		out.writeObject(new Message(MessageType.grade_update));
		out.flush();
	}


	public void queryAwardsDisciplinary(Message m, ObjectOutputStream out) throws IOException {
		out.writeObject(new Message(MessageType.award_disciplinary_query,
				StuInfoManage.queryAwardsDisciplinary(m)));
		out.flush();
	}


	public void addAwardDisciplinary(Message m, ObjectOutputStream out) throws IOException {
		StuInfoManage.addAwardDisciplinary(m);
		out.writeObject(new Message(MessageType.award_disciplinary_add));
		out.flush();
	}


	public void updateAwardDisciplinary(Message m, ObjectOutputStream out) throws IOException {
		StuInfoManage.updateAwardDisciplinary(m);
		out.writeObject(new Message(MessageType.award_disciplinary_update));
		out.flush();
	}


	public void queryEnrollmentChanges(Message m, ObjectOutputStream out) throws IOException {
		out.writeObject(new Message
				(MessageType.enrollment_change_query, StuInfoManage.queryEnrollmentChanges(m)));
		out.flush();
	}


	public void addEnrollmentChange(Message m, ObjectOutputStream out) throws IOException {
		StuInfoManage.addEnrollmentChange(m);
		out.writeObject(new Message(MessageType.enrollment_change_add));
		out.flush();
	}


	public void updateEnrollmentChange(Message m, ObjectOutputStream out) throws IOException {
		StuInfoManage.updateEnrollmentChange(m);
		out.writeObject(new Message(MessageType.enrollment_change_update));
		out.flush();
	}


	public void updateStudentAvatar(Message m, ObjectOutputStream out) throws IOException {
		StuInfoManage.updateStudentAvatar(m);
		out.writeObject(new Message(MessageType.student_avatar_update));
		out.flush();
	}


	public void changeStudentStatus(Message m, ObjectOutputStream out) throws IOException {
		StuInfoManage.changeStudentStatus(m);
		out.writeObject(new Message(MessageType.student_status_change));
		out.flush();
	}

	public void everyoneid(Message m, ObjectOutputStream out) throws IOException {
		try {
			// 调用 StuInfoManage 类的 getEveryoneId 方法获取所有学生 ID
			List<Serializable> studentIds = StuInfoManage.getEveryoneId();

			// 创建一个新的 Message 对象，包含学生 ID 列表，并设置消息类型为 everyone_id
			Message responseMessage = new Message(MessageType.everyone_id, studentIds);

			// 将消息对象发送给客户端
			out.writeObject(responseMessage);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			// 发送失败消息或处理异常
			out.writeObject(new Message(MessageType.failure));
			out.flush();
		}
	}

		//获取需要展示的信息
		public void showInfoHandler (Message message, ObjectOutputStream out) throws IOException {
			List<Serializable> userInfo = stuInfoManage.queryStudentInfo_2(message);
			Message responseMessage = new Message(message.getType(), userInfo);
			out.writeObject(responseMessage);
			out.flush();
		}
		//注册信息处理
		public void registerHandler (Message message, ObjectOutputStream out) throws IOException {
			int temp = stuInfoManage.registerInfo(message);
			Message responseMessage = null;
			switch (temp) {
				case 1:
					responseMessage = new Message(MessageType.success_student);
					break;
				case 2:
					responseMessage = new Message(MessageType.success_teacher);
					break;
				case 3:
					responseMessage = new Message(MessageType.success_manager);
					break;
				case 0:
					responseMessage = new Message(MessageType.registerError_0);
					break;
				case -1:
					responseMessage = new Message(MessageType.registerError_1);
					break;
				default:
					responseMessage = new Message(MessageType.failure);
					break;
			}
			out.writeObject(responseMessage);
			out.flush();
		}
		//登陆信息处理
		public void loginHandler (Message m, ObjectOutputStream out) throws IOException {
			int temp = stuInfoManage.loginInfo(m);
			Message responseMessage = null;
			switch (temp) {
				case 1:
					responseMessage = new Message(MessageType.success_student);
					break;
				case 2:
					responseMessage = new Message(MessageType.success_teacher);
					break;
				case 3:
					responseMessage = new Message(MessageType.success_manager);
					break;
				case 0:
				default:
					responseMessage = new Message(MessageType.failure);
					break;
			}
			out.writeObject(responseMessage);
			out.flush();
		}
		//修改信息处理
		public void reviseHandler (Message message, ObjectOutputStream out) throws IOException {
			int temp = stuInfoManage.reviseInfo(message);
			Message responseMessage = null;
			switch (temp) {
				case 1:
					responseMessage = new Message(MessageType.success_student);
					break;
				case 2:
					responseMessage = new Message(MessageType.success_teacher);
					break;
				case 3:
					responseMessage = new Message(MessageType.success_manager);
					break;
				case 0:
					responseMessage = new Message(MessageType.reviseError_1);
				default:
					responseMessage = new Message(MessageType.failure);
					break;
			}
			out.writeObject(responseMessage);
			out.flush();
		}
		//注销信息处理
		public void logoffHandler (Message message, ObjectOutputStream out) throws IOException {
			int temp = stuInfoManage.logoffInfo(message);
			Message responseMessage = null;
			switch (temp) {
				case 1:
					responseMessage = new Message(MessageType.success_student);
					break;
				case 2:
					responseMessage = new Message(MessageType.success_teacher);
					break;
				case 3:
					responseMessage = new Message(MessageType.success_manager);
					break;
				case 0:
					responseMessage = new Message(MessageType.logoffError_0);
					break;
				default:
					responseMessage = new Message(MessageType.failure);
					break;
			}
			out.writeObject(responseMessage);
			out.flush();
		}

	private void handleAllProducts(ObjectOutputStream out) throws IOException {
		Message response = Shop.getProducts(Shop.getConn());
		out.writeObject(response);
		out.flush();
	}

	private void handleAddProduct(Message receivedMessage, ObjectOutputStream out) throws IOException {
		if (!receivedMessage.getContent().isEmpty() && receivedMessage.getContent().get(0) instanceof Product) {
			Product product = (Product) receivedMessage.getContent().get(0);
			Message response = Shop.addProduct(product);
			out.writeObject(response);
			out.flush();
		} else {
			out.writeObject(new Message(MessageType.failure));
			out.flush();
		}
	}

	private void handleRemoveProduct(Message receivedMessage, ObjectOutputStream out) throws IOException {
		if (!receivedMessage.getContent().isEmpty()) {
			Object identifier = receivedMessage.getContent().get(0);
			Message response;
			if (identifier instanceof Integer) {
				// 根据 productId 删除
				response = Shop.removeProduct((int) identifier);
			} else if (identifier instanceof String) {
				// 根据 productName 删除
				response = Shop.removeProduct((String) identifier);
			} else {
				response = new Message(MessageType.failure);
			}
			out.writeObject(response);
			out.flush();
		}
	}

	private void handlePurchaseProduct(Message receivedMessage, ObjectOutputStream out) throws IOException {
		if (receivedMessage.getContent().size() >= 2) {
			Object identifier = receivedMessage.getContent().get(0);
			int num = (int) receivedMessage.getContent().get(1);
			Message response;
			if (identifier instanceof Integer) {
				// 根据 productId 购买
				response = Shop.purchaseProduct((int) identifier, num);
			} else if (identifier instanceof String) {
				// 根据 productName 购买
				response = Shop.purchaseProduct((String) identifier, num);
			} else {
				response = new Message(MessageType.failure);
			}
			out.writeObject(response);
			out.flush();
		} else {
			out.writeObject(new Message(MessageType.failure));
			out.flush();
		}
	}

	private void handleIncreaseProductCount(Message receivedMessage, ObjectOutputStream out) throws IOException {
		if (receivedMessage.getContent().size() >= 2) {
			Object identifier = receivedMessage.getContent().get(0);
			int amount = (int) receivedMessage.getContent().get(1);
			Message response;
			if (identifier instanceof Integer) {
				// 根据 productId 增加库存
				response = Shop.increaseProductCount((int) identifier, amount);
			} else if (identifier instanceof String) {
				// 根据 productName 增加库存
				response = Shop.increaseProductCount((String) identifier, amount);
			} else {
				response = new Message(MessageType.failure);
			}
			out.writeObject(response);
			out.flush();
		} else {
			out.writeObject(new Message(MessageType.failure));
			out.flush();
		}
	}

	public void renew(Message m, ObjectOutputStream out) throws IOException
	{
		Library.renew(m);
		out.writeObject(new Message(MessageType.renew));
		out.flush();
	}

	//atm
	public String getCardId(Message m)
	{
		return (String)m.getContent().get(0);
	}

	public void getBalance(String cardId, ObjectOutputStream out) throws IOException
	{
		List<Serializable> balance = new ArrayList<>();
		balance.add(ATM.getBalance(cardId));
		out.writeObject(new Message(MessageType.success,balance));
		out.flush();
	}

	public void reportCardLoss(String cardId, ObjectOutputStream out) throws IOException
	{
		if(ATM.reportCardLoss(cardId))
			out.writeObject(new Message(MessageType.success));
		else
			out.writeObject(new Message(MessageType.failure));
		out.flush();
	}

	public void changeCardPassword(String cardId, String newPassword, ObjectOutputStream out) throws IOException
	{
		if(ATM.changeCardPassword(cardId, newPassword))
			out.writeObject(new Message(MessageType.success));
		else
			out.writeObject(new Message(MessageType.failure));
		out.flush();
	}

	public void addNewAccount(String cardId, double balance, String cardPass,String id, ObjectOutputStream out) throws IOException
	{
		if(ATM.addNewAccount(cardId, balance, cardPass, id))
			out.writeObject(new Message(MessageType.success));
		else
			out.writeObject(new Message(MessageType.failure));
		out.flush();
	}

	public void deleteAccount(String cardId, ObjectOutputStream out) throws IOException {
		ATM.deleteAccount(cardId);
		out.writeObject(new Message(MessageType.failure));
		out.flush();
	}

	public void isCardLost(String cardId, ObjectOutputStream out) throws IOException {
		if(ATM.isCardLost(cardId))
			//丢了
			out.writeObject(new Message(MessageType.success));
		else
			out.writeObject(new Message(MessageType.failure));
		out.flush();
	}

	public void deposit(ObjectOutputStream out) throws IOException
	{
		if(ATM.deposit())
			out.writeObject(new Message(MessageType.success));
		else
			out.writeObject(new Message(MessageType.failure));
		out.flush();
		Global.setVerificationSuccess(false);
	}

	private void deposit_2(String cardId, double v, ObjectOutputStream out) throws IOException {
		out.writeObject(ATM.deposit_2(cardId,v));
		out.flush();
	}

	private void determinePassword(Message receivedMessage, ObjectOutputStream out) throws IOException {
		if(ATM.determinePassword((String) receivedMessage.getContent().get(0),
				(String) receivedMessage.getContent().get(1)))
			out.writeObject(new Message(MessageType.success));
		else
			out.writeObject(new Message(MessageType.failure));
		out.flush();
	}

	private void withdraw(Message receivedMessage, ObjectOutputStream out) throws IOException {
		if(ATM.withdraw((String) receivedMessage.getContent().get(0),
				(Double) receivedMessage.getContent().get(1)))
			out.writeObject(new Message(MessageType.success));
		else
			out.writeObject(new Message(MessageType.failure));
		out.flush();
	}

	//library
	public void search(Message m, ObjectOutputStream out) throws IOException
	{
		Message responseMessage = new Message(MessageType.search,
				Library.search(m));

		out.writeObject(responseMessage);
		out.flush();
	}

	public void borrow(Message m, ObjectOutputStream out) throws IOException
	{
		Library.borrow(m);
		out.writeObject(new Message(MessageType.borrow));
		out.flush();
	}

	public void add(Message m, ObjectOutputStream out) throws IOException
	{
		Library.add(m);
		out.writeObject(new Message(MessageType.add));
		out.flush();
	}

	public void getCover(Message m, ObjectOutputStream out) throws IOException
	{
		Message responseMessage = new Message(MessageType.getCover,
				Library.getCover(m));

		out.writeObject(responseMessage);
		out.flush();
	}

	public void revise(Message m, ObjectOutputStream out) throws IOException
	{
		Library.revise(m);
		out.writeObject(new Message(MessageType.revise));
		out.flush();
	}

	public void delete(Message m, ObjectOutputStream out) throws IOException
	{
		Library.delete(m);
		out.writeObject(new Message(MessageType.delete));
		out.flush();
	}

	public void borrowInfo(Message m, ObjectOutputStream out) throws IOException
	{
		Message responseMessage = new Message(MessageType.borrowInfo,
				Library.borrowInfo(m));

		out.writeObject(responseMessage);
		out.flush();
	}

	public void topBooks(Message m, ObjectOutputStream out) throws IOException
	{
		Message responseMessage = new Message(MessageType.topBooks,
				Library.topBooks(m));

		out.writeObject(responseMessage);
		out.flush();
	}

	public void topReaders(Message m, ObjectOutputStream out) throws IOException
	{
		Message responseMessage = new Message(MessageType.topReaders,
				Library.topReaders(m));

		out.writeObject(responseMessage);
		out.flush();
	}

	public void returnBooks(Message m, ObjectOutputStream out) throws IOException
	{
		Library.returnBooks(m);
		out.writeObject(new Message(MessageType.returnBooks));
		out.flush();
	}
	public void recommend(Message m, ObjectOutputStream out) throws IOException
	{
		Library.recommend(m);
		out.writeObject(new Message(MessageType.recommend));
		out.flush();
	}

	public void getRecommend(Message m, ObjectOutputStream out) throws IOException
	{
		Message responseMessage = new Message(MessageType.getRecommend,
				Library.getRecommend(m));

		out.writeObject(responseMessage);
		out.flush();
	}

	public void disposeRecommend(Message m, ObjectOutputStream out) throws IOException
	{
		Library.disposeRecommend(m);
		out.writeObject(new Message(MessageType.disposeRecommend));
		out.flush();
	}

	public void addCourse(Message m, ObjectOutputStream out) throws IOException {
		Message response = CourseController.addCourse(m);
		out.writeObject(response);
		out.flush();
	}

	public void addAnnoouncement(Message m, ObjectOutputStream out) throws IOException {
		Message response = AnnouncementController.addAnnouncement(m);
		out.writeObject(response);
		out.flush();
	}

	public void getAllAnnouncement(Message m, ObjectOutputStream out) throws IOException {
		Message response = AnnouncementController.getAnnouncements();
		out.writeObject(response);
		out.flush();
	}

	public void getAnnouncementById(Message m, ObjectOutputStream out) throws IOException {
		Message response = AnnouncementController.getAnnouncementsById(m);
		out.writeObject(response);
		out.flush();


	}

	public void getAllCourse(Message m, ObjectOutputStream out) throws IOException {
		Message response = CourseController.getAllCourses(m);
		out.writeObject(response);
		out.flush();

	}

	public void selectCourse(Message m, ObjectOutputStream out) throws IOException, SQLException {
		Message response =  CourseController.selectCourse(m);
		out.writeObject(response);
		out.flush();
	}

	public void unselectCourse(Message m,ObjectOutputStream out) throws IOException
	{
		Message response = CourseController.unselectCourse(m);
		out.writeObject(response);
		out.flush();

	}

	public void deleteCourse(Message m,ObjectOutputStream out)throws IOException
	{
		Message response = CourseController.deleteCourse(m);
		out.writeObject(response);
		out.flush();

	}

	public void getCourseByTeacherName(Message m,ObjectOutputStream out)throws IOException
	{
		Message response = CourseController.getCourseByTeacherName(m);
		out.writeObject(response);
		out.flush();

	}

	public  void getStudentByCourseId(Message m,ObjectOutputStream out)throws IOException
	{
		Message response = CourseController.getStudentByCourseId(m);
		out.writeObject(response);
		out.flush();
	}

	public void saveScore(Message m,ObjectOutputStream out)throws IOException
	{
		Message response = CourseController.saveScores(m);
		out.writeObject(response);
		out.flush();
	}

	public void getCourseByStudentId(Message m,ObjectOutputStream out) throws IOException, SQLException {
		Message response =CourseController.getCourses(m);
		out.writeObject(response);
		out.flush();
	}

	public void modifyAnnouncement(Message m,ObjectOutputStream out)throws IOException,SQLException
	{
		Message response = AnnouncementController.editAnnouncement(m);
		out.writeObject(response);
		out.flush();
	}

	public void deleteAnnouncement(Message m,ObjectOutputStream out)throws IOException
	{
		Message response = AnnouncementController.deleteAnnouncement(m);
		out.writeObject(response);
		out.flush();
	}

	public void updataCourse(Message m,ObjectOutputStream out)throws IOException
	{
		Message response = CourseController.updataCourse(m);
		out.writeObject(response);
		out.flush();
	}

	public void getCourseByTeacherId(Message m,ObjectOutputStream out) throws IOException, SQLException {
		Message response = CourseController.getCourses1(m);
		out.writeObject(response);
		out.flush();

	}
	public void getAllcourseManager(Message m,ObjectOutputStream out) throws IOException,SQLException
	{
		Message response = CourseController.getAllCoursesManager(m);
		out.writeObject(response);
		out.flush();
	}

	public void getStudentByStudentId(Message m,ObjectOutputStream out) throws IOException,SQLException
	{
		Message response = CourseController.getStudentByStudentId(m);
		out.writeObject(response);
		out.flush();
	}

	public void sendScoreByStudentId(Message m,ObjectOutputStream out) throws IOException,SQLException
	{
		Message response = CourseController.getScore(m);
		out.writeObject(response);
		out.flush();
	}


}






