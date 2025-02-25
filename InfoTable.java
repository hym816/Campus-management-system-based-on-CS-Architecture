package Server;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class InfoTable extends JTable {
    private final Set<Integer> editableColumns;
    private int hoveredRow = -1;
    private int hoveredCol = -1;

    public InfoTable(int rows, int cols, Set<Integer> editableColumns) {
        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editableColumns.contains(column);
            }
        };
        setModel(model);
        this.editableColumns = editableColumns;

        DefaultTableCellRenderer renderer = new CustomCellRenderer(editableColumns);
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        setDefaultRenderer(Object.class, renderer);
        setGridColor(Color.LIGHT_GRAY);

        // 添加按钮渲染器和编辑器
        TableColumn buttonColumn = getColumnModel().getColumn(0); // 选择你希望插入按钮的列
        buttonColumn.setCellRenderer(new ButtonRenderer());
        buttonColumn.setCellEditor(new ButtonEditor(new JCheckBox())); // 使用CheckBox作为占位符

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                if (row != hoveredRow || col != hoveredCol) {
                    hoveredRow = row;
                    hoveredCol = col;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                hoveredCol = -1;
                repaint();
            }
        });
    }

    private class CustomCellRenderer extends DefaultTableCellRenderer {
        private final Set<Integer> editableColumns;

        public CustomCellRenderer(Set<Integer> editableColumns) {
            this.editableColumns = editableColumns;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (editableColumns.contains(column)) {
                c.setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            } else {
                c.setBackground(new Color(230,230,230));
                setForeground(Color.DARK_GRAY);
            }

            if ((row == hoveredRow || column == hoveredCol) && row != -1 && column != -1) {
                c.setBackground(new Color(210, 240, 255));
            }

            return c;
        }
    }

    // 自定义渲染器类，用于显示按钮
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "Button" : value.toString());
            return this;
        }
    }

    // 自定义编辑器类，用于处理按钮点击事件
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Button" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                JOptionPane.showMessageDialog(button, label + ": Clicked!");
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Info Table Example");
        Set<Integer> editableColumns = new HashSet<>();
        editableColumns.add(2);
        editableColumns.add(1);
        InfoTable table = new InfoTable(10, 3, editableColumns); // 将按钮插入第1列
        frame.add(new JScrollPane(table));
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
