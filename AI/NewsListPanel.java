package Server.AI;

import Server.Public.ContentPanel;
import Server.Public.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NewsListPanel implements ContentPanel {

    private JPanel panel;

    public NewsListPanel() {
        // Initialize the main panel with BorderLayout
        panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20)); // 设置20像素的边距

        // 顶部面板显示加粗的“新闻”字样
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("新闻");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel);
        panel.add(topPanel, BorderLayout.NORTH);

        // 创建新闻列表面板
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 示例新闻数据
        String[][] newsData = {
                {"习近平和彭丽媛为出席中非合作论坛北京峰会的国际贵宾举行欢迎宴会", "新华社", "　　新华社北京9月4日电（记者温馨、董雪）9月4日晚，国家主席习近平和夫人彭丽媛在北京人民大会堂举行宴会，欢迎来华出席中非合作论坛北京峰会的非方及国际贵宾。中共中央政治局常委李强、赵乐际、王沪宁、蔡奇、丁薛祥、李希出席。9月的北京，秋高气爽。雄伟的人民大会堂内，中国国旗和中非合作论坛成员国国旗以及非洲联盟旗帜交相辉映。出席论坛峰会的51个非洲国家元首、政府首脑及配偶，2位总统代表，非盟委员会主席和联合国秘书长等贵宾陆续抵达人民大会堂。习近平和彭丽媛热情迎接，同他们分别握手问候，并集体合影留念。活泼可爱的少年儿童唱起优美的歌曲，打起欢快的非洲鼓，载歌载舞，对远道而来的国际贵宾表示热烈欢迎。在悠扬的迎宾曲中，习近平和彭丽媛同贵宾们共同步入宴会厅。习近平发表致辞，代表中国政府和中国人民热烈欢迎各位嘉宾。习近平指出，每次同非洲朋友见面，我都倍感亲切，尤其深切感受到，中非命运共同体建设基础牢、起点高、前景广，为构建人类命运共同体树立了光辉典范。中非命运共同体根植于传统友好，彰显于合作共赢，壮大于与时偕行。中国同非洲国家在反帝反殖反霸的斗争中并肩奋斗，在发展振兴、逐梦现代化的道路上携手同行，在抗击新冠疫情中守望相助，在重大国际和地区问题上通力协作。不论国际形势如何变化，中非友谊赓续传承、历久弥坚。习近平强调，构建命运共同体是人类的共同梦想，现代化是梦想连接现实的必由之路。无论过去还是现在，中非都是构建命运共同体的先行者，未来也必将携手走在现代化进程的前列。我相信，只要28亿多中非人民同心同向，就一定能在现代化道路上共创辉煌，引领全球南方现代化事业蓬勃发展，为构建人类命运共同体作出更大贡献。论坛共同主席国塞内加尔总统法耶代表非方领导人感谢友好的中国人民给予的盛情款待和周到安排，表示非中友好根基深厚、源远流长，双方理念相近，平等相待、相互尊重、互利共赢、团结合作，都主张建设一个更加平等有序、普惠包容的世界。在习近平主席领导下，中国不仅实现自身快速发展，而且为促进世界和平与增长作出重要贡献。非方高度赞赏习近平主席提出的系列重要倡议以及为推动中非合作包括论坛机制建设作出的重要贡献，愿同中方加强团结合作，深化非中全面战略合作伙伴关系，更好维护共同利益。宴会后，习近平和彭丽媛同贵宾们共同观看《携手同行，共筑未来》文艺演出。演出展现了中非多彩文明美美与共、中非人民相知相亲，赢得阵阵热烈掌声。王毅、尹力、李书磊、何立峰、王小洪等出席上述活动。"},
                {"机器学习的基本原理", "李四", "机器学习是一种..."},
                {"大数据分析与应用", "王五", "大数据是指..."}
        };

        // 创建每条新闻的自定义按钮
        for (String[] news : newsData) {
            String title = news[0];
            String author = news[1];
            String content = news[2];

            NewsButton newsButton = new NewsButton(title, () -> {
                // 点击标题后打开对应的新闻阅读窗口
                NewsReader reader = new NewsReader(title, author, content);
                reader.setVisible(true);
            });

            newsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // 占满整行
            listPanel.add(newsButton);
            listPanel.add(Box.createVerticalStrut(10)); // 添加间隔
        }
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}
