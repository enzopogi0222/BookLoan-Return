package com.mycompany.bookloanandreturn.View.common;

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

/** Base for Add/Edit Book views: same form layout, colors, and window-close behavior. */
public abstract class BookFormView {
    protected final JFrame frame;
    protected final JTextField bookNameField;
    protected final JTextField authorField;
    protected final JTextField genreField;
    protected final JTextField publishedYearField;
    protected final JTextField stockField;
    protected final JTextField[] allFields;
    private ActionListener saveListener;

    public BookFormView(String title, String buttonText) {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(520, 420);
        frame.getContentPane().setBackground(ViewStyles.BACKGROUND);

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 10));
        panel.setBackground(ViewStyles.BACKGROUND);
        panel.setOpaque(true);

        bookNameField = new JTextField(20);
        authorField = new JTextField(20);
        genreField = new JTextField(20);
        publishedYearField = new JTextField(20);
        stockField = new JTextField(20);
        allFields = new JTextField[]{bookNameField, authorField, genreField, publishedYearField, stockField};

        addRow(panel, "Book Name:", bookNameField);
        addRow(panel, "Author:", authorField);
        addRow(panel, "Genre:", genreField);
        addRow(panel, "Published Year:", publishedYearField);
        addRow(panel, "Stock:", stockField);

        JButton btn = new JButton(buttonText);
        ViewStyles.styleGreenButton(btn);
        panel.add(new JLabel(""));
        panel.add(btn);
        btn.addActionListener(e -> {
            if (saveListener != null) saveListener.actionPerformed(null);
        });

        frame.add(panel);
        frame.setLocationRelativeTo(null);
    }

    private static void addRow(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setForeground(ViewStyles.LABEL);
        field.setBackground(Color.WHITE);
        panel.add(label);
        panel.add(field);
    }

    protected void addSaveListener(ActionListener listener) {
        this.saveListener = listener;
    }

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

    public String getBookName() { return bookNameField.getText().trim(); }
    public String getAuthor() { return authorField.getText().trim(); }
    public String getGenre() { return genreField.getText().trim(); }
    public String getPublishedYear() { return publishedYearField.getText().trim(); }
    public String getStockText() { return stockField.getText().trim(); }
    public JFrame getFrame() { return frame; }
    public void show() { frame.setVisible(true); }

    public void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void clearFields() {
        for (JTextField f : allFields) f.setText("");
    }
}
