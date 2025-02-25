package Server.Public;

import Server.Public.Message.MessageType;

public class Login {
	private static Database d = new Database("jdbc:mysql://"+Server.sqlip+":3306/my_database2",Server.user,Server.password);
	private static String DN="my_database2";
	public static int login(Message m)
	{
		if(m.getType()==MessageType.login_info)
		{
			String id = (String)m.getContent().get(0);
			String password = (String)m.getContent().get(1);
			
			if(d.getValueByColumnName("id",id,DN,"info", "password",String.class)!=null&&
			password==d.getValueByColumnName("id",id,DN,"info", "password",String.class))
			{
				String identity = d.getValueByColumnName("id",id,DN,"info", "identity",String.class);
				if(identity.equals("student"))
					return 1;
				if(identity.equals("teacher"))
					return 2;
				if(identity.equals("manager"))
					return 3;
			}
			//fail
		}
		return 0;
	}

}
