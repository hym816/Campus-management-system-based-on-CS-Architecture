package Server.Library;

import Server.Public.ContentPanel;
import Server.Public.TransparentButton;

import javax.swing.*;
import java.awt.*;

public class searchFailure implements ContentPanel {

    private JPanel panel;

    public searchFailure() {
        panel = new JPanel(new BorderLayout()); // Use BorderLayout for the main panel
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add some space around the panel

        // Create the back button with fixed size and align it to the left
        JButton backButton = new TransparentButton("< 返回");
        backButton.setPreferredSize(new Dimension(100, 30)); // Set a fixed size for the button
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Align to the left
        backButtonPanel.add(backButton);
        panel.add(backButtonPanel, BorderLayout.NORTH); // Add the button panel to the top



        // Create the rounded panel for the message, with tighter content fitting
        RoundedPanel roundedPanel = new RoundedPanel(10, new Color(255, 255, 255, 120)); // Semi-transparent white background
        roundedPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center layout with minimal padding



        // Create the message label with HTML for line breaks and larger font
        JLabel label = new JLabel("<html>抱歉，图书馆库存中暂无这本书。<br/>可以去读者荐购处向管理员推荐书籍");
        label.setFont(new Font("Serif", Font.BOLD, 20)); // Set the font
        roundedPanel.add(label); // Add the label to the rounded panel

        // Adjust size of the rounded panel based on the preferred size of the label
        roundedPanel.setPreferredSize(new Dimension(label.getPreferredSize().width + 20, label.getPreferredSize().height + 20));
        panel.add(roundedPanel, BorderLayout.CENTER); // Add the rounded panel to the center

        backButton.addActionListener(e -> switchToSearch()); // Add action listener to the back button
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    private void switchToSearch() {
        // Switch to the search panel
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
        if (topFrame instanceof MainWindowStu) {
            ((MainWindowStu) topFrame).switchContent("搜索书籍");
        }
    }
}
