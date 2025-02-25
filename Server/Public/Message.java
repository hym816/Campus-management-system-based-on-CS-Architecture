package Server.Public;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable 
{
    private static final long serialVersionUID = 1L;
    
    public enum MessageType {
        end,
        login_info,
        success_student,
        success_teacher,
        success_manager,
        success,
        failure,
        test,
        register_info,
        registerError_0,
        registerError_1,
        revise_info,
        reviseError_1,
        logoff_info,
        logoffError_0,
        user_info,

        
        //学籍管理系统
        student_info_query,// 查询学生的详细信息（如姓名、性别、生日、入学日期、班级、状态等）  
        student_info_add, 
        student_info_update, //当学生的基本信息（如姓名、地址、邮箱等）被更新时，可以使用此消息类型通知客户端或服务端               
        grade_query,//查询学生的成绩时使用的消息类型             
        grade_add,
        grade_update,//更新学生的成绩时使用的消息类型            
        award_disciplinary_query, //查询学生的奖励和处分记录时使用的消息类型           
        award_disciplinary_add,
        award_disciplinary_update, //添加或更新学生的奖励和处分记录时使用的消息类型      
        enrollment_change_query, //查询学生的学籍变更记录（如班级变更、休学、复学等）时使用的消息类型        
        enrollment_change_add,
        enrollment_change_update, //更新学生的学籍变更记录时使用的消息类型      
        student_avatar_update, //更新学生头像时使用的消息类型，可以传输图片数据。
        student_status_change,
        everyone_id,

        //atm
        atm_login,
        check_balance,
        report_loss,
        change_password,
        add_account,
        delete_account,
        check_loss,
        deposit,
        deposit_2,
        determinePassword,
        withdraw,
        showQR,


        all_product,
        addProduct,
        removeProduct,
        purchaseProduct,
        increaseProductCount,

        search,
        borrow,
        add,
        delete,
        getCover,
        renew,
        revise,
        borrowInfo,
        topBooks,
        topReaders,
        returnBooks,
        recommend,
        getRecommend,
        disposeRecommend,

        //课程管理系统
        //课程管理系统
        add_course,//增加课程
        add_course_success,//增加课程成功
        add_course_failure,//增加课程失败
        delete_course,//删除课程
        select_course,//选课
        unselect_course,//退课
        add_announcement,//添加公告
        get_all_announcement,//获取所有公告
        send_all_announcement,//发送所有公告
        get_announcement_by_id,//根据id获取公告
        send_announcement_by_id,//根据id发送公告
        modify_announcement,//修改公告
        delete_announcement,//删除公告
        get_all_course,//获取所有课程
        get_all_course_manager,
        send_all_course,//发送所有课程
        updata_course,//编辑课程
        get_course_by_teachername,//根据老师名字获取课程
        send_course_by_teachername,//根据老师名字发送课程
        send_course_by_teacherid,
        get_course_by_teacherid,
        get_course_by_studentid,//获取学生选课列表
        send_course_by_studentid,//发送学生课程列表
        get_students_by_courseid,//获取课程的所有学生
        get_students_by_studentid,
        send_students_by_studentid,
        send_students_by_courseid,//发送课程的所有学生
        save_score,//保存成绩
        send_score_by_studentid,
        get_score_by_studentid


    }
    	
    
    private MessageType type; 
    private List<Serializable> content; 
    public Message(MessageType type, List<Serializable> content) {
        this.type = type;
        this.content = content;
    }

    
    public Message(MessageType type) {
    	this.type = type;
    }
    
    // 空构造函数，用于序列化
    public Message() {
    }

    // Getter 和 Setter 方法
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public List<Serializable> getContent() {
        return content;
    }

    public void setContent(List<Serializable> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", content='" + content + '\'' +
                '}';
    }
}
