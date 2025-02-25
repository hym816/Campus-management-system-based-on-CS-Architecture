package Server.Library;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Server.Public.Button;

// 自定义渲染器类
public class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setOpaque(true); // 使按钮的背景不透明
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Button) { // 确保 value 是自定义按钮类型
            Button btn = (Button) value;
            this.setText(btn.getText()); // 直接使用按钮对象的文本
            this.setBackground(btn.getBackground());
            this.setForeground(btn.getForeground());
            this.setFont(btn.getFont());
        }
        return this;
    }
}
