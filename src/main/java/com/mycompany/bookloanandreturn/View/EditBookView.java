package com.mycompany.bookloanandreturn.View;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * View for the Edit Book screen. Displays form fields and notifies listeners on save.
 */
public class EditBookView {
    private final JFrame frame;
    /** Id of the book being edited (0 = new/no book). */
    private int currentBookId;
    private final JTextField bookNameField;
    private final JTextField authorField;
    private final JTextField genreField;
    private final JTextField publishedYearField;
    private final JTextField stockField;

    public EditBookView() {
        frame = new JFrame("Edit Book");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(520, 420);
        frame.getContentPane().setBackground(new Color(200, 230, 201));

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 10));
        panel.setBackground(new Color(200, 230, 201));
        panel.setOpaque(true);

        Color labelColor = new Color(27, 94, 32);
        Color fieldBg = Color.WHITE;

        bookNameField = new JTextField(20);
        authorField = new JTextField(20);
        genreField = new JTextField(20);
        publishedYearField = new JTextField(20);
        stockField = new JTextField(20);

        JLabel l1 = new JLabel("Book Name:");
        l1.setForeground(labelColor);
        panel.add(l1);
        bookNameField.setBackground(fieldBg);
        panel.add(bookNameField);
        JLabel l2 = new JLabel("Author:");
        l2.setForeground(labelColor);
        panel.add(l2);
        authorField.setBackground(fieldBg);
        panel.add(authorField);
        JLabel l3 = new JLabel("Genre:");
        l3.setForeground(labelColor);
        panel.add(l3);
        genreField.setBackground(fieldBg);
        panel.add(genreField);
        JLabel l4 = new JLabel("Published Year:");
        l4.setForeground(labelColor);
        panel.add(l4);
        publishedYearField.setBackground(fieldBg);
        panel.add(publishedYearField);
        JLabel l5 = new JLabel("Stock:");
        l5.setForeground(labelColor);
        panel.add(l5);
        stockField.setBackground(fieldBg);
        panel.add(stockField);

        JButton updateButton = new JButton("Update Book");
        updateButton.setBackground(new Color(46, 125, 50));
        updateButton.setForeground(Color.WHITE);
        updateButton.setOpaque(true);
        updateButton.setBorderPainted(true);
        panel.add(new JLabel(""));
        panel.add(updateButton);

        updateButton.addActionListener(e -> fireEditBookRequested());

        frame.add(panel);
        frame.setLocationRelativeTo(null);
    }

    /**
     * Register a callback to run when this window is closed (X or dispose).
     */
    public void setOnWindowClose(Runnable onClose) {
        if (onClose != null) {
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    onClose.run();
                }
            });
        }
    }

    private ActionListener editBookListener;

    /**
     * Register a listener to be called when the user clicks "Update Book".
     */
    public void addEditBookListener(ActionListener listener) {
        this.editBookListener = listener;
    }

    private void fireEditBookRequested() {
        if (editBookListener != null) {
            editBookListener.actionPerformed(null);
        }
    }

    // --- Getters for controller to read form values ---
    public String getBookName() {
        return bookNameField.getText().trim();
    }

    /** Author field value (controller uses getAuthorName). */
    public String getAuthorName() {
        return authorField.getText().trim();
    }

    public String getGenre() {
        return genreField.getText().trim();
    }

    public String getPublishedYear() {
        return publishedYearField.getText().trim();
    }

    public String getStockText() {
        return stockField.getText().trim();
    }

    /** Set the id of the book being edited (call before or with setBook). */
    public void setBookId(int id) {
        this.currentBookId = id;
    }

    /** Id of the book being edited (0 if none). */
    public int getBookId() {
        return currentBookId;
    }

    /** Pre-fill the form with book data (e.g. when editing a selected book). */
    public void setBook(String bookName, String author, String genre, String publishedYear, int stock) {
        bookNameField.setText(bookName != null ? bookName : "");
        authorField.setText(author != null ? author : "");
        genreField.setText(genre != null ? genre : "");
        publishedYearField.setText(publishedYear != null ? publishedYear : "");
        stockField.setText(String.valueOf(stock));
    }

    public JFrame getFrame() {
        return frame;
    }

    /** Show the window (call on EDT). */
    public void show() {
        frame.setVisible(true);
    }

    /** Show success message (matches controller spelling). */
    public void showSucess(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    /** Show error message. */
    public void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Clear all text fields. */
    public void clearFields() {
        bookNameField.setText("");
        authorField.setText("");
        genreField.setText("");
        publishedYearField.setText("");
        stockField.setText("");
    }
}
