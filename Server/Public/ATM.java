package Server.Public;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ATM
{
    private static Database database = new Database("jdbc:mysql://"+Server.sqlip+":3306/my_database2",
            Server.user,Server.password);
    private static String databasename = "my_database2";
    private static ThreadLocal<String> currCardId = new ThreadLocal<>();
    private static ThreadLocal<Double> curramount = new ThreadLocal<>();
    private static ThreadLocal<String> currOrderId = new ThreadLocal<>();


    public static boolean determinePassword(String cardId,String password)
    {
        try {
            String rp = database.getValueByColumnName("cardId", cardId,
                    databasename, "atm","cardPass",String.class);
            if(rp.equals(password))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 查询账户余额
    public static double getBalance(String cardId)
    {
        try {
            return database.getValueByColumnName("cardId", cardId,
                    databasename, "atm","balance",Double.class);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 报告卡丢失
    public static boolean reportCardLoss(String cardId)
    {
        try {
            int loss = 1;
            database.reviseStringByColumnName("cardId", cardId, databasename, "atm",
                    "isReportLoss", loss);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 更改密码
    public static boolean changeCardPassword(String cardId, String newPassword)
    {
        try {
            database.reviseStringByColumnName("cardId", cardId, databasename,
                    "atm", "cardPass",newPassword);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 添加新账户
    public static boolean addNewAccount(String cardId, double balance, String cardPass,String id)
    {
        try {
            String sql = "INSERT INTO "+ databasename+ " (cardId, balance, isReportLoss, id, cardPass) VALUES (?, ?, ?, ?, ?)";
            database.addToDatabaseWithMultipleValues(sql, cardId,balance,0,id,cardPass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 删除账户
    public static void deleteAccount(String cardId) {
        try {
            database.deleteFromDatabase("cardId", cardId, databasename, "atm");
            System.out.println("Account deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 检查卡是否丢失
    public static boolean isCardLost(String cardId) {
        try {
            int isReportLoss = database.getValueByColumnName(
                    "cardId", cardId, databasename, "atm", "isReportLoss", int.class);
            if(isReportLoss==0)
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // 存款
    public static Message deposit_2(String cardId, double amount) {
        currCardId.set(cardId);
        curramount.set(amount);
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = currentTime.format(formatter);
        String orderId = "orderId_" + formattedDateTime;
        currOrderId.set(orderId);
        AlipayService alipayService = new AlipayService(
                orderId, "存款", String.valueOf(amount));
        byte[] image = alipayService.generatePaymentQRCode();
        List<Serializable> te = new ArrayList<>();
        te.add(image);
        return new Message(Message.MessageType.showQR, te);
    }


    public static boolean deposit()
    {
        String cardId= currCardId.get();
        double amount= curramount.get();
        String orderid= currOrderId.get();

        currCardId.set(null);
        curramount.set(null);
        currOrderId.set(null);

        AlipayService.startPollingPaymentStatus(orderid,10,5);
        try {
            // 获取当前余额
            double currentBalance = getBalance(cardId);
            if (currentBalance == -1) {
                System.out.println("获取余额失败");
            }
            // 更新余额
            double newBalance = currentBalance + amount;
            database.reviseStringByColumnName("cardId", cardId, databasename, "atm",
                    "balance", newBalance);
            System.out.println("存款成功，当前余额：" + newBalance);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean withdraw(String cardId, double amount)
    {
        try {
            // 获取当前余额
            double currentBalance = getBalance(cardId);
            if (currentBalance == -1) {
                System.out.println("获取余额失败");
                return false;
            }
            double newBalance;
            if(amount<=currentBalance)
                newBalance = currentBalance - amount;
            else
                newBalance = currentBalance;
            database.reviseStringByColumnName("cardId", cardId, databasename, "atm", "balance", newBalance);
            System.out.println("取款成功，当前余额：" + newBalance);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}

