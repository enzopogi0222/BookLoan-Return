package com.mycompany.bookloanandreturn.View;

import java.awt.BorderLayout;
import java.awt.Color;
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

/**
 * Main menu view with options for Add Book and View Book.
 */
public class MainMenuView {
    private final JFrame frame;
    private final JButton addBookButton;
    private final JButton viewBookButton;

    public MainMenuView() {
        frame = new JFrame("Book Loan and Return - Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 280);
        frame.getContentPane().setBackground(new Color(200, 230, 201));

        Color darkGreen = new Color(27, 94, 32);
        Color buttonGreen = new Color(46, 125, 50);

        JLabel titleLabel = new JLabel("Main Menu");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(darkGreen);

        addBookButton = new JButton("Add Book");
        addBookButton.setBackground(buttonGreen);
        addBookButton.setForeground(Color.WHITE);
        addBookButton.setOpaque(true);
        addBookButton.setBorderPainted(true);
        addBookButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addBookButton.setFocusPainted(false);

        viewBookButton = new JButton("View Book");
        viewBookButton.setBackground(buttonGreen);
        viewBookButton.setForeground(Color.WHITE);
        viewBookButton.setOpaque(true);
        viewBookButton.setBorderPainted(true);
        viewBookButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewBookButton.setFocusPainted(false);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(200, 230, 201));
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
        mainPanel.setBackground(new Color(200, 230, 201));
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

    public void show() {
        frame.setVisible(true);
    }

    /** Hide this window (e.g. when opening Add Book). Call show() to display again. */
    public void hide() {
        frame.setVisible(false);
    }

    public JFrame getFrame() {
        return frame;
    }
}
