package Server.AI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class LlamaComponent extends JPanel {
    private JTextArea textArea;  // 添加文本区域作为类的成员变量
    private JPopupMenu popupMenu;
    private String lastSelectedText = "";
    private Stack<String> undoStack = new Stack<>(); // Stack for undo functionality
    private Llama llama = new Llama(); // Llama instance

    public LlamaComponent() {
        setLayout(new BorderLayout());

        // Text area for user input
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Context menu
        popupMenu = new JPopupMenu();
        JMenuItem makePoliteItem = new JMenuItem("变得更礼貌");
        JMenuItem makeConciseItem = new JMenuItem("变得更简洁");
        popupMenu.add(makePoliteItem);
        popupMenu.add(makeConciseItem);

        // Undo button
        JButton undoButton = new JButton("撤回");

        // Panel to hold the undo button and text area
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(undoButton, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);

        // Context menu action for "Make Polite"
        makePoliteItem.addActionListener(e -> {
            handleTextTransformation("polite");
        });

        // Context menu action for "Make Concise"
        makeConciseItem.addActionListener(e -> {
            handleTextTransformation("concise");
        });

        // Undo action
        undoButton.addActionListener(e -> {
            if (!undoStack.isEmpty()) {
                textArea.setText(undoStack.pop());
            }
        });

        // Show context menu on right-click
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopupIfTriggered(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupIfTriggered(e);
            }

            private void showPopupIfTriggered(MouseEvent e) {
                // 确保右键点击且选中了文本
                if (e.isPopupTrigger() && textArea.getSelectedText() != null) {
                    lastSelectedText = textArea.getSelectedText();
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    // Handles text transformation by making it polite or concise
    private void handleTextTransformation(String transformationType) {
        if (lastSelectedText == null || lastSelectedText.isEmpty()) return;

        // Save the current state for undo
        undoStack.push(textArea.getText());

        // Callback to update text in the text area
        java.util.function.Consumer<String> updateText = transformedText -> {
            SwingUtilities.invokeLater(() -> {
                String currentText = textArea.getText();
                String selectedText = lastSelectedText;

                // Replace only the first occurrence of the selected text
                String newText = currentText.replaceFirst(selectedText, transformedText);
                textArea.setText(newText);
            });
        };

        // Call the appropriate Llama function
        if (transformationType.equals("polite")) {
            llama.makePolite(lastSelectedText, updateText);
        } else if (transformationType.equals("concise")) {
            llama.makeConcise(lastSelectedText, updateText);
        }
    }

    // 新增 getTextArea 方法
    public JTextArea getTextArea() {
        return textArea;
    }
}
