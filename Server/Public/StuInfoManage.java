package Server.Public;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StuInfoManage {

	private static Database d = new Database("jdbc:mysql://"+Server.sqlip+":3306/my_database2",Server.user,Server.password);
	private static String DN = "my_database2";


	public List<Serializable> queryStudentInfo_2(Message message){
		String studentId = (String) message.getContent().get(0);
		// 查询学生信息表中的所有字段
		String studentPassword = d.getValueByColumnName("id",studentId,DN,"info","password",String.class);
		String studentIdentity = d.getValueByColumnName("id",studentId,DN,"info","identity",String.class);
		byte[] imageBytes = d.getValueByColumnName("id",studentId,DN,"info","picture",byte[].class);

		// 将查询结果打包成 List 并发送回客户端
		List<Serializable> studentInfo = new ArrayList<>();
		studentInfo.add(studentId);
		studentInfo.add(studentPassword);
		studentInfo.add(studentIdentity);
		studentInfo.add(imageBytes);
		//返回List
		return studentInfo;
	}

	public static List<Serializable> queryStudentInfo(Message m)
	{
		String studentId = (String) m.getContent().get(0);
		// 查询学生信息表中的所有字段
		String studentName = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "student_name", String.class);
		String gender = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "gender", String.class);
		String dateOfBirth = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "date_of_birth", String.class);
		String nationalId = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "national_id", String.class);
		String admissionDate = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "admission_date", String.class); // 修改数据库名
		String program = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "program", String.class);
		String classNumber = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "class", String.class);
		String status = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "status", String.class);
		String email = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "email", String.class);
		String phoneNumber = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "phone_number", String.class);
		String address = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "address", String.class);
		byte[] avatar = d.getValueByColumnName("student_id", studentId, DN, "Student_Information", "avatar", byte[].class);

		// 将查询结果打包成 List 并发送回客户端
		List<Serializable> studentInfo = new ArrayList<>();
		studentInfo.add(studentName);
		studentInfo.add(gender);
		studentInfo.add(dateOfBirth);
		studentInfo.add(nationalId);
		studentInfo.add(admissionDate);
		studentInfo.add(program);
		studentInfo.add(classNumber);
		studentInfo.add(status);
		studentInfo.add(email);
		studentInfo.add(phoneNumber);
		studentInfo.add(address);
		studentInfo.add(avatar);
		return studentInfo;
	}

	public static boolean addStudentInfo(Message m) 
	{
		try {
			// 确保根据实际类型进行提取
			String studentId = (String) m.getContent().get(0); // studentId 是 Integer 类型
			String studentName = (String) m.getContent().get(1); // studentName 是 String 类型
			String gender = (String) m.getContent().get(2);      // gender 是 String 类型
			String dateOfBirth = (String) m.getContent().get(3); // dateOfBirth 是 String 类型
			String nationalId = (String) m.getContent().get(4);  // nationalId 是 String 类型
			String admissionDate = (String) m.getContent().get(5); // admissionDate 是 String 类型
			String program = (String) m.getContent().get(6);     // program 是 String 类型
			String classNumber = (String) m.getContent().get(7); // classNumber 是 Integer 类型
			String status = (String) m.getContent().get(8);      // status 是 String 类型
			String email = (String) m.getContent().get(9);       // email 是 String 类型
			String phoneNumber = (String) m.getContent().get(10); // phoneNumber 是 String 类型
			String address = (String) m.getContent().get(11);    // address 是 String 类型
			byte[] avatar = (byte[]) m.getContent().get(12);     // avatar 是 byte[] 类型

			// 调用 Database 类的方法，执行 SQL 插入操作
			String sql = "INSERT INTO "+DN+".Student_Information (student_id, student_name, gender, date_of_birth, national_id, admission_date, program, class, status, email, phone_number, address, avatar) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			d.addToDatabaseWithMultipleValues(sql, studentId, studentName, gender, 
					dateOfBirth, nationalId, admissionDate, program, classNumber,
					status, email, phoneNumber, address, avatar);

			return true;
		} catch (Exception e) {
			e.printStackTrace(); 
			return false;
		}
	}

	public static void updateStudentInfo(Message m) 
	{
		String studentId = (String) m.getContent().get(0);

		// 假设传入的内容是一个 List，包含了学生的所有信息
		List<Serializable> updatedInfo = m.getContent().subList(1, m.getContent().size());

		// 逐个字段更新
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "student_name", updatedInfo.get(0));
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "gender", updatedInfo.get(1));
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "date_of_birth", updatedInfo.get(2));
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "national_id", updatedInfo.get(3));
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "admission_date", updatedInfo.get(4));
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "program", updatedInfo.get(5));
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "class", updatedInfo.get(6));
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "status", updatedInfo.get(7));
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "email", updatedInfo.get(8));
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "phone_number", updatedInfo.get(9));
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "address", updatedInfo.get(10));
		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "avatar", updatedInfo.get(11));
		
	}

	public static List<Serializable> queryGrades(Message m) 
	{
		String studentId = (String) m.getContent().get(0);
		String semester = (String) m.getContent().get(1); // 假设客户端会发送学期信息
		List<Serializable> grades = new ArrayList<>();

		// 根据课程逐一查询成绩并添加到列表中
		for (int courseId = 1; courseId <= 10; courseId++) { // 假设有10门课程
			String grade = d.getValueByColumnName("student_id", studentId, DN, "Grades", "grade", String.class);
			grades.add(grade);
		}
		return grades;
	}

	public static void addGrade(Message m) 
	{
		String studentId = (String) m.getContent().get(0);
		String courseId = (String) m.getContent().get(1);
		String semester = (String) m.getContent().get(2);
		String grade = (String) m.getContent().get(3);

		// 调用 Database 类的方法，执行 SQL 插入操作
		String sql = "INSERT INTO "+DN+".Grades (student_id, course_id, semester, grade) VALUES (?, ?, ?, ?)";
		d.addToDatabaseWithMultipleValues(sql, studentId, courseId, semester, grade);
	}


	public static void updateGrade(Message m) 
	{
		String studentId = (String) m.getContent().get(0);
		String courseId = (String) m.getContent().get(1);
		String semester = (String) m.getContent().get(2);
		String grade = (String) m.getContent().get(3);

		// 调用 Database 类的方法，执行 SQL 更新操作
		d.reviseStringByColumnName("student_id", studentId, DN, "Grades", "grade", grade);
	}


	public static List<Serializable> queryAwardsDisciplinary(Message m)
	{
		String studentId = (String) m.getContent().get(0);
		String recordType = (String) m.getContent().get(1); // 奖励或处分类型
		List<Serializable> records = new ArrayList<>();

		String recordDescription = d.getValueByColumnName("student_id", studentId, DN, "Awards_and_Disciplinary_Actions", "record_description", String.class);
		records.add(recordDescription);

		return records;
	}



	public static void addAwardDisciplinary(Message m)
	{
		String studentId = (String) m.getContent().get(0);
		String recordType = (String) m.getContent().get(1);
		String recordDescription = (String) m.getContent().get(2);
		String recordDate = (String) m.getContent().get(3);

		// 调用 Database 类的方法，执行 SQL 插入操作
		String sql = "INSERT INTO "+DN+".Awards_and_Disciplinary_Actions (student_id, record_type, record_description, record_date) VALUES (?, ?, ?, ?)";
		d.addToDatabaseWithMultipleValues(sql, studentId, recordType, recordDescription, recordDate);
	}



	public static void updateAwardDisciplinary(Message m)
	{
		String studentId = (String) m.getContent().get(0);
		String recordType = (String) m.getContent().get(1);
		String recordDescription = (String) m.getContent().get(2);

		// 调用 Database 类的方法，执行 SQL 更新操作
		d.reviseStringByColumnName("student_id", studentId, DN, "Awards_and_Disciplinary_Actions", "record_description", recordDescription);

	}


	public static List<Serializable> queryEnrollmentChanges(Message m)
	{
		return null;
	}




	public static void addEnrollmentChange(Message m)
	{
		String studentId = (String) m.getContent().get(0);
		String changeType = (String) m.getContent().get(1);
		String changeDate = (String) m.getContent().get(2);
		String previousValue = (String) m.getContent().get(3);
		String newValue = (String) m.getContent().get(4);

		// 调用 Database 类的方法，执行 SQL 插入操作
		String sql = "INSERT INTO "+DN+".Enrollment_Changes (student_id, change_type, change_date, previous_value, new_value) VALUES (?, ?, ?, ?, ?)";
		d.addToDatabaseWithMultipleValues(sql, studentId, changeType, changeDate, previousValue, newValue);

	}


	public static void updateEnrollmentChange(Message m)
	{
		String studentId = (String) m.getContent().get(0);
		String changeType = (String) m.getContent().get(1);
		String newValue = (String) m.getContent().get(2);

		// 调用 Database 类的方法，执行 SQL 更新操作
		d.reviseStringByColumnName("student_id", studentId, DN, "Enrollment_Changes", "new_value", newValue);
		
	}



	public static void updateStudentAvatar(Message m) 
	{
		String studentId = (String) m.getContent().get(0);
		byte[] avatar = (byte[]) m.getContent().get(1);

		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "avatar", avatar);
		
	}


	public static void changeStudentStatus(Message m)
	{
		String studentId = (String) m.getContent().get(0);
		String status = (String) m.getContent().get(1);

		d.reviseStringByColumnName("student_id", studentId, DN, "Student_Information", "status", status);
		
	}


	public static List<Serializable> getEveryoneId() {
		List<Serializable> studentIds = new ArrayList<>();
		try {
			// 使用 Database 类中的 getAllIds 方法获取所有学生 ID
			List<String> ids = d.getAllIds(DN, "Student_Information", "student_id");

			// 将结果转换为 Serializable 类型并添加到列表
			studentIds.addAll(ids);
		} catch (Exception e) {
			e.printStackTrace();
			// 处理异常，可能记录日志或返回一个错误的响应
		}
		return studentIds;
	}

	//对登陆信息判断
	public int loginInfo(Message message) {
		if(message.getType() == Message.MessageType.login_info){
			String id = (String) message.getContent().get(0);
			String password = (String)message.getContent().get(1);
			System.out.println("id: "+id+" password: "+password);
			String tempPassword = d.getValueByColumnName("id",id,DN,"info","password",String.class);
			System.out.println(tempPassword);

			if(password.equals(tempPassword)){
				String identity = d.getValueByColumnName("id",id,DN,"info","identity",String.class);
				System.out.println(identity);
				if(identity.equals("student")){
					return 1;
				}
				if(identity.equals("teacher")){
					return 2;
				}
				if(identity.equals("manager")){
					return 3;
				}
			}
			else{//登陆失败
				System.out.println("登陆失败");
			}
		}
		return 0;
	}
	//对注册信息判断
	public int infoJudge(String id, String password, String identity){
		//进行判断身份与id是否匹配，判断id是否重复
		//这里进行判断id与身份是否匹配

		//id或passwowrd或identity为空，返回0
		//无法修改和注册
		if(id.equals("")||password.equals("")||identity.equals("")){
			return 0;
		}
		//这里判断id是否重复
		String isExist = d.getValueByColumnName("id",id,DN,"info","password",String.class);
		if(isExist!=null){//已经存在id，无法继续注册
			return -1;//返回-1，表示id已经存在，表示可以修改和删除
		}
		else{
			return 1;//返回1，id不存在数据库中且密码身份不为空，表示可以注册
		}

	}
	//注册信息
	public int registerInfo(Message message){
		//判断是否为注册信息
		if(message.getType() == Message.MessageType.register_info){
			String id =(String) message.getContent().get(0);
			String password = (String) message.getContent().get(1);
			String identity = (String) message.getContent().get(2);
			byte[] imageUser = (byte[]) message.getContent().get(3);
			int flagIdentity = infoJudge(id,password,identity);//此处可执行函数，进行判断身份与id是否匹配，判断id是否重复
			if(flagIdentity == 1){//注册成功
//				//写入数据库
//				String databaseName = DN;
				String chartName = "info";
				//新增一行，并写入id
				d.addToDatabase(DN,chartName,"id",id,"password");
				//写入密码
				d.reviseStringByColumnName("id",id,DN,chartName,"password",password);
				//写入身份
				d.reviseStringByColumnName("id",id,DN,chartName,"identity",identity);
				//写入图片
				d.reviseStringByColumnName("id",id,DN,chartName,"picture",imageUser);
				//发送注册成功消息
				if(identity.equals("student")) {
					return 1;
				}
				else if(identity.equals("teacher")){
					return 2;
				}
				else if(identity.equals("manager")){
					return 3;
				}
			}
			else if(flagIdentity == -1){
				//id重复无法注册
				return -1;
			}
			else if(flagIdentity == 0){
				//有空位，无法注册
				return 0;
			}
			//
		}
		return 0;
	}
	//修改信息
	public int reviseInfo(Message message) {
		//判断是否为修改信息
		if (message.getType() == Message.MessageType.revise_info) {
			String id = (String) message.getContent().get(0);
			String password = (String) message.getContent().get(1);
			String identity = (String) message.getContent().get(2);
			byte[] imageBytes = (byte[]) message.getContent().get(3);
			int flagIdentity = infoJudge(id, password, identity);//此处可执行函数，进行判断身份与id是否匹配，修改是否成功
			if (flagIdentity == -1) {//只有id显示重复才可修改成功
//				写入数据库
//				String databaseName = DN;
				String chartName = "info";

				//修改密码（暂时只能修改密码）
				d.reviseStringByColumnName("id", id, DN, chartName, "password", password);
				//修改身份
				d.reviseStringByColumnName("id",id,DN,chartName,"identity",identity);
				//修改头像
				d.reviseStringByColumnName("id",id,DN,chartName,"picture",imageBytes);
				//发送修改成功消息
				if (identity.equals("student")) {
					return 1;
				} else if (identity.equals("teacher")) {
					return 2;
				} else if (identity.equals("manager")) {
					return 3;
				}
			}
			else if (flagIdentity == 0) {
				//有空位，无法注册
				return -1;
			}
		}
		return 0;
	}
	//注销信息
	public int logoffInfo(Message message){
		if (message.getType() == Message.MessageType.logoff_info) {
			String id = (String) message.getContent().get(0);
			String password = (String) message.getContent().get(1);
			String identity = (String) message.getContent().get(2);
			int flagIdentity = infoJudge(id, password, identity);
			if (flagIdentity == -1) {//只有id显示重复才可注销
//				//写入数据库
//				String databaseName = DN;
				String chartName = "info";
				//数据库删除
				d.deleteFromDatabase("id",id,DN,chartName);
				//发送注销成功消息
				if (identity.equals("student")) {
					return 1;
				} else if (identity.equals("teacher")) {
					return 2;
				} else if (identity.equals("manager")) {
					return 3;
				}
			}
		}
		return 0;
	}



}
