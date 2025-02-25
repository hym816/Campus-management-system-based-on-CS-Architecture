package Server.Public;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static Server.Public.Server.*;


public class Library {

    private static Database d = new Database("jdbc:mysql://"+sqlip+":3306/",user,password);
    private static String DN = "my_database2";


    //搜索书籍操作（用户和管理员都有）
    public static List<Serializable> search(Message m) {
        // 获取查询类型和关键词
        String type = (String) m.getContent().get(0);
        String content = (String) m.getContent().get(1);

        List<Serializable> bookInfo = new ArrayList<>();
        if (type.equals("书名")) {
            // 关键字为书名的搜索

            if(d.getAllValueByColumnName("book_name", content, DN, "book", "bid", String.class)==null)
                return null;//判断书名是否存在，不存在返回null

            List<String> bids = d.getAllValueByColumnName("book_name", content, DN, "book", "bid", String.class);
            List<String> authors = d.getAllValueByColumnName("book_name", content, DN, "book", "author", String.class);
            List<String> nations = d.getAllValueByColumnName("book_name", content, DN, "book", "nation", String.class);
            List<String> availables = d.getAllValueByColumnName("book_name", content, DN, "book", "available", String.class);
            List<String> publishers = d.getAllValueByColumnName("book_name", content, DN, "book", "publisher", String.class);
            List<String> styles = d.getAllValueByColumnName("book_name", content, DN, "book", "type", String.class);
            List<String> covers=d.getAllValueByColumnName("book_name", content, DN, "book", "type", String.class);
            // 确保所有字段列表的大小一致
            int size = Math.min(Math.min(Math.min(bids.size(), authors.size()), availables.size()), Math.min(publishers.size(), styles.size()));

            for (int i = 0; i < size; i++) {
                // 将每条记录的数据依次添加到 bookInfo 中
                bookInfo.add(bids.get(i));         // 添加 bid
                bookInfo.add(content);             // 添加 bookname
                bookInfo.add(authors.get(i));      // 添加 author
                bookInfo.add(nations.get(i));      // 添加 nation
                bookInfo.add(availables.get(i));   // 添加 available
                bookInfo.add(publishers.get(i));   // 添加 publisher
                bookInfo.add(styles.get(i));       // 添加 style
            }
        }

        if (type.equals("作者")) {
            // 关键字为作者的搜索

            if(d.getAllValueByColumnName("author", content, DN, "book", "bid", String.class)==null)
                return null;
            List<String> bids = d.getAllValueByColumnName("author", content, DN, "book", "bid", String.class);
            List<String> bookname = d.getAllValueByColumnName("author", content, DN, "book", "book_name", String.class);
            List<String> nations = d.getAllValueByColumnName("author", content, DN, "book", "nation", String.class);
            List<String> availables = d.getAllValueByColumnName("author", content, DN, "book", "available", String.class);
            List<String> publishers = d.getAllValueByColumnName("author", content, DN, "book", "publisher", String.class);
            List<String> styles = d.getAllValueByColumnName("author", content, DN, "book", "type", String.class);

            // 确保所有字段列表的大小一致
            int size = Math.min(Math.min(Math.min(bids.size(), bookname.size()), availables.size()), Math.min(publishers.size(), styles.size()));

            for (int i = 0; i < size; i++) {
                // 将每条记录的数据依次添加到 bookInfo 中
                bookInfo.add(bids.get(i));         // 添加 bid
                bookInfo.add(bookname.get(i));             // 添加 bookname
                bookInfo.add(content);      // 添加 author
                bookInfo.add(nations.get(i));      // 添加 nation
                bookInfo.add(availables.get(i));   // 添加 available
                bookInfo.add(publishers.get(i));   // 添加 publisher
                bookInfo.add(styles.get(i));       // 添加 style
            }
        }

        if (type.equals("类型")) {
            if(d.getAllValueByColumnName("type", content, DN, "book", "bid", String.class)==null)
                return null;
            List<String> bids = d.getAllValueByColumnName("type", content, DN, "book", "bid", String.class);
            List<String> bookname = d.getAllValueByColumnName("type", content, DN, "book", "book_name", String.class);
            List<String> nations = d.getAllValueByColumnName("type", content, DN, "book", "nation", String.class);
            List<String> availables = d.getAllValueByColumnName("type", content, DN, "book", "available", String.class);
            List<String> publishers = d.getAllValueByColumnName("type", content, DN, "book", "publisher", String.class);
            List<String> authors = d.getAllValueByColumnName("type", content, DN, "book", "author", String.class);

            // 确保所有字段列表的大小一致
            int size = Math.min(Math.min(Math.min(bids.size(), bookname.size()), availables.size()), Math.min(publishers.size(), authors.size()));

            for (int i = 0; i < size; i++) {
                // 将每条记录的数据依次添加到 bookInfo 中
                bookInfo.add(bids.get(i));         // 添加 bid
                bookInfo.add(bookname.get(i));
                bookInfo.add(authors.get(i));       // 添加 style// 添加 bookname
                bookInfo.add(nations.get(i));
                bookInfo.add(availables.get(i));   // 添加 available
                bookInfo.add(publishers.get(i));   // 添加 publisher
                bookInfo.add(content);
            }
        }

        if (type.equals("国籍")) {
            // 关键字为国籍的搜索

            if(d.getAllValueByColumnName("book_name", content, DN, "book", "bid", String.class)==null)
                return null;

            List<String> bids = d.getAllValueByColumnName("nation", content, DN, "book", "bid", String.class);
            List<String> bookname = d.getAllValueByColumnName("nation", content, DN, "book", "book_name", String.class);
            List<String> authors = d.getAllValueByColumnName("nation", content, DN, "book", "author", String.class);
            List<String> availables = d.getAllValueByColumnName("nation", content, DN, "book", "available", String.class);
            List<String> publishers = d.getAllValueByColumnName("nation", content, DN, "book", "publisher", String.class);
            List<String> styles = d.getAllValueByColumnName("nation", content, DN, "book", "type", String.class);

            // 确保所有字段列表的大小一致
            int size = Math.min(Math.min(Math.min(bids.size(), authors.size()), availables.size()), Math.min(publishers.size(), styles.size()));

            for (int i = 0; i < size; i++) {
                // 将每条记录的数据依次添加到 bookInfo 中
                bookInfo.add(bids.get(i));         // 添加 bid
                bookInfo.add(bookname.get(i));
                bookInfo.add(content);             // 添加 bookname
                bookInfo.add(authors.get(i));      // 添加 author
                bookInfo.add(availables.get(i));   // 添加 available
                bookInfo.add(publishers.get(i));   // 添加 publisher
                bookInfo.add(styles.get(i));       // 添加 style
            }
        }
        return bookInfo;
    }

    //用户借书操作
    public static void borrow(Message m){
        String bid = (String) m.getContent().get(0);         //获取书的编号
        String uid = (String) m.getContent().get(1);         //获取学生uid
        String duration = (String) m.getContent().get(2);    //获取借阅时长（以月份为单位）

        //如果库存数量大于1，库存-1，如果库存数量少于1，发送失败
        String available=d.getValueByColumnName("bid",bid,DN,"book", "available",String.class);
        int avail = Integer.parseInt(available);
        System.out.println(available);
        if(avail>0){
            avail -= 1;
            String availStr = String.valueOf(avail);
            d.reviseStringByColumnName("bid", bid, DN, "book", "available", availStr);
        }


        //将该书籍的借阅次数+1
        String times=d.getValueByColumnName("bid",bid,DN,"book", "times",String.class);
        int timesInt = Integer.parseInt(times);

        timesInt += 1;
        String timesStr = String.valueOf(timesInt);
        d.reviseStringByColumnName("bid", bid, DN, "book", "times", timesStr);

        //将该同学借阅次数+1

        String name = d.getValueByColumnName("uid", uid, DN, "reader", "name", String.class);
        d.updateTopReader(uid, name);


        //获取当前时间
        int n = Integer.parseInt(duration);
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDateTime futureTime = nowTime.plusMonths(n);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 将当前时间和未来时间转换为字符串
        String nowStr = nowTime.format(formatter);
        String futureStr = futureTime.format(formatter);

        int newHid = d.getNextHid();
        String hidStr=Integer.toString(newHid);
        //将新借阅信息添加到History列表中
        String bookName = d.getValueByColumnName("bid", bid, DN, "book", "book_name", String.class);

        String sql = "INSERT INTO my_database2.history (hid,uid, reader_name, bid, bookname, start, end, status) VALUES (?,?, ?, ?, ?, ?, ?, ?)";
        d.addToDatabaseWithMultipleValues(sql,  hidStr,uid, name,bid,bookName,
                nowStr, futureStr, "1");//状态0归还，1借阅中，2逾期
    }


    public static void renew(Message m){
        String hid = (String) m.getContent().get(0);
        String time = (String) m.getContent().get(1);

        String end1 = d.getValueByColumnName("hid", hid, DN, "history", "end", String.class);
        System.out.println("原本结束时间"+end1);
        int monthsToAdd = Integer.parseInt(time);

        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 解析时间 a
        LocalDateTime dateTime = LocalDateTime.parse(end1, formatter);
        // 增加 b 个月
        LocalDateTime updatedDateTime = dateTime.plusMonths(monthsToAdd);
        // 格式化更新后的时间
        String updatedTime = updatedDateTime.format(formatter);
        System.out.println("更新时间"+updatedTime);
        d.reviseStringByColumnName("hid", hid, DN, "history", "end", updatedTime);
    }

    //用户还书操作
    public static void returnBooks(Message m){
        String hid = (String)m.getContent().get(0);//获取该条借阅历史的hid

        //获取当前借阅历史的书籍信息
        String bid=d.getValueByColumnName("hid", hid, DN, "history", "bid", String.class);

        //将该书的库存+1
        String available=d.getValueByColumnName("bid",bid,DN,"book", "available",String.class);
        int avail = Integer.parseInt(available);
        avail+=1;
        String availStr=String.valueOf(avail);
        d.reviseStringByColumnName("bid", bid, DN, "book", "available", availStr);

        //将该学生的借书状态设置为已经归还
        d.reviseStringByColumnName("hid", hid, DN, "history", "status", "0");
    }

    //管理员添加书籍
    public static void add(Message m){
        String bid = (String)m.getContent().get(0);
        String bookname = (String)m.getContent().get(1);
        String author = (String)m.getContent().get(2);
        String nation = (String)m.getContent().get(3);
        String available = (String)m.getContent().get(4);
        String publisher = (String)m.getContent().get(5);
        String type = (String)m.getContent().get(6);
        byte[] imageBytes = (byte[]) m.getContent().get(7);

        //获取书籍的bid
        String sql = "INSERT INTO my_database2.book (bid, book_name, author, nation,available, publisher, type,times,cover) VALUES (?, ?,?, ?, ?, ?,?, ?,?)";

        d.addToDatabaseWithMultipleValues(sql,  bid, bookname,author,nation,available, publisher, type,"0",imageBytes);
    }

    public static List<Serializable> getCover(Message m){
        String bid = (String)m.getContent().get(0);//获取到需要展示封面的书籍的bid号
        List<Serializable> cover = new ArrayList<>();

        byte[] coverByte=d.getValueByColumnName("bid", bid, DN, "book", "cover", byte[].class);
        cover.add(coverByte);
        return cover;
    }

    public static void revise(Message m){

        String bid = (String)m.getContent().get(0);
        String bookname = (String)m.getContent().get(1);
        String author = (String)m.getContent().get(2);
        String nation = (String)m.getContent().get(3);
        String available = (String)m.getContent().get(4);
        String publisher = (String)m.getContent().get(5);
        String type = (String)m.getContent().get(6);
        //获取书籍的bid

        d.reviseStringByColumnName("bid", bid, DN, "book", "book_name", bookname);
        d.reviseStringByColumnName("bid", bid, DN, "book", "author", author);
        d.reviseStringByColumnName("bid", bid, DN, "book", "nation", nation);
        d.reviseStringByColumnName("bid", bid, DN, "book", "available", available);
        d.reviseStringByColumnName("bid", bid, DN, "book", "publisher", publisher);
        d.reviseStringByColumnName("bid", bid, DN, "book", "type", type);
    }

    //管理员删除书籍
    public static void delete(Message m){
        String bid = (String)m.getContent().get(0);
        d.deleteFromDatabase("bid", bid, DN, "book");
    }

    //查看某个用户的借阅信息
    public static List<Serializable> borrowInfo(Message m){
        String uid = (String)m.getContent().get(0);

        List<Serializable> borrowInfo = new ArrayList<>();
        List<String> hids = d.getAllValueByColumnName("uid", uid, DN, "history", "hid", String.class);
        List<String> bids = d.getAllValueByColumnName("uid", uid, DN, "history", "bid", String.class);
        List<String> bookname = d.getAllValueByColumnName("uid", uid, DN, "history", "bookname", String.class);
        List<String> start = d.getAllValueByColumnName("uid", uid, DN, "history", "start", String.class);
        List<String> end = d.getAllValueByColumnName("uid", uid, DN, "history", "end", String.class);
        List<String> status = d.getAllValueByColumnName("uid", uid, DN, "history", "status", String.class);

        // 确保所有字段列表的大小一致
        int size = Math.min(Math.min(Math.min(bids.size(), bookname.size()), start.size()), Math.min(end.size(), status.size()));
        for (int i = 0; i < size; i++) { // 将每条记录的数据依次添加到 bookInfo 中
            borrowInfo.add(hids.get(i));         // 添加 bid
            borrowInfo.add(bids.get(i));         // 添加 bid
            borrowInfo.add(bookname.get(i));      // 添加 书名
            borrowInfo.add(start.get(i));   // 添加 开始时间
            borrowInfo.add(end.get(i));   // 添加 结束时间
            borrowInfo.add(status.get(i));       // 添加 借阅状态
        }
        return  borrowInfo;
    }

    //获取到最热门十本的书籍
    public static List<Serializable> topBooks(Message m) {
        List<Serializable> topBooks = new ArrayList<>();
        List<String> bids = d.getTopBooksByTimes();

        int size=bids.size();
        for (int i = 0; i < size; i++) {

            String bid=bids.get(i);
            String bookname = d.getValueByColumnName("bid", bid, DN, "book", "book_name", String.class);
            String author = d.getValueByColumnName("bid", bid, DN, "book", "author", String.class);
            String available = d.getValueByColumnName("bid", bid, DN, "book", "available", String.class);
            String publisher = d.getValueByColumnName("bid", bid, DN, "book", "publisher", String.class);
            String style = d.getValueByColumnName("bid", bid, DN, "book", "type", String.class);
            String times=d.getValueByColumnName("bid", bid, DN, "book", "times", String.class);

            topBooks.add(bid);         // 添加 bid
            topBooks.add(bookname);    // 添加 bookname
            topBooks.add(author);      // 添加 author
            topBooks.add(available);   // 添加 available
            topBooks.add(publisher);   // 添加 publisher
            topBooks.add(style);       // 添加 style
            topBooks.add(times);       // 添加 次数
        }
        return topBooks;
    }

    //获得借阅次数最多的读者
    public static List<Serializable> topReaders(Message m) {
        List<Serializable> topReaders = new ArrayList<>();
        List<String> bids = d.getTopReadersByTimes();
        int size=bids.size();

        for (int i = 0; i < size; i++) {

            String uid=bids.get(i);
            String readerName = d.getValueByColumnName("uid", uid, DN, "reader", "name", String.class);
            String count = d.getValueByColumnName("uid", uid, DN, "reader", "count", String.class);

            topReaders.add(uid);
            topReaders.add(readerName);
            topReaders.add(count);
        }
        return topReaders;
    }
    public static void recommend(Message m) {

        String bookname = (String)m.getContent().get(0);
        String author = (String)m.getContent().get(1);
        String publisher = (String)m.getContent().get(2);

        int newRid = d.getNextRid();
        String ridStr=Integer.toString(newRid);

        String sql = "INSERT INTO my_database2.bookrecommend ( rid,book_name, author, publisher,apply,exist) VALUES (?,?, ?,?,?,?)";
        d.addToDatabaseWithMultipleValues(sql,   ridStr,bookname,author, publisher,"暂无","1");
    }

    public static List<Serializable> getRecommend(Message m) {
        List<Serializable> recInfo = new ArrayList<>();

        List<String> booknames = d.getAllValueByColumnName("exist", "1", DN, "bookrecommend", "book_name", String.class);
        List<String> authors = d.getAllValueByColumnName("exist", "1", DN, "bookrecommend", "author", String.class);
        List<String> publisher = d.getAllValueByColumnName("exist", "1", DN, "bookrecommend", "publisher", String.class);
        List<String> apply = d.getAllValueByColumnName("exist", "1", DN, "bookrecommend", "apply", String.class);

        int size=booknames.size();

        for (int i = 0; i < size; i++) {

            recInfo.add(booknames.get(i));
            recInfo.add(authors.get(i));
            recInfo.add(publisher.get(i));
            recInfo.add(apply.get(i));

        }
        return recInfo;
    }

    public static void disposeRecommend(Message m){
        String bookname = (String)m.getContent().get(0);
        String dispose = (String)m.getContent().get(1);
        d.reviseStringByColumnName("book_name", bookname, DN, "bookrecommend", "apply", dispose);
    }

}
