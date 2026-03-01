package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/** Main menu view with options for Add Book and View Book. */
public class MainMenuView {
    private final JFrame frame;
    private final JButton addBookButton;
    private final JButton viewBookButton;

    public MainMenuView() {
        frame = new JFrame("Book Loan and Return - Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 360);
        frame.getContentPane().setBackground(ViewStyles.BACKGROUND);

        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        JLabel titleLabel = new JLabel("Main Menu");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ViewStyles.LABEL);

        addBookButton = new JButton("Add Book");
        ViewStyles.styleGreenButton(addBookButton, font);

        viewBookButton = new JButton("View Book");
        ViewStyles.styleGreenButton(viewBookButton, font);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(ViewStyles.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(titleLabel, gbc);
        gbc.gridy = 1;
        centerPanel.add(addBookButton, gbc);
        gbc.gridy = 2;
        centerPanel.add(viewBookButton, gbc);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 20));
        mainPanel.setBackground(ViewStyles.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
    }

    public void addAddBookListener(ActionListener listener) {
        addBookButton.addActionListener(listener);
    }
    public void addViewBookListener(ActionListener listener) {
        viewBookButton.addActionListener(listener);
    }
    public void show() { frame.setVisible(true); }
    public void hide() { frame.setVisible(false); }
    public JFrame getFrame() { return frame; }
}
