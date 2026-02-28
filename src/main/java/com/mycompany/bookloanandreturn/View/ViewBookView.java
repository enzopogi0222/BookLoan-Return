package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.Models.Book;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ViewBookView {
    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private static final String[] COLUMN_NAMES = {"Book Name", "Author", "Genre", "Published Year", "Stock"};

    public ViewBookView(){
        frame = new JFrame("View Books");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 400);
        frame.getContentPane().setBackground(new Color(200, 230, 201));

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        table.setRowHeight(22);
        table.getTableHeader().setBackground(new Color(27, 94, 32));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(new Color(200, 230, 201));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(46, 125, 50));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(true);
        refreshButton.addActionListener(e -> fireRefreshRequested());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(200, 230, 201));
        bottomPanel.add(refreshButton);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(new Color(200, 230, 201));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
    }

private ActionListener refreshListener;

public void addRefreshListener(ActionListener listener){
    this.refreshListener = listener;
}

private void fireRefreshRequested(){
    if (refreshListener != null) {
        refreshListener.actionPerformed(null);
    }
}

public void displayBooks(List<Book> books) {
    tableModel.setRowCount(0);
    for (Book b : books) {
        tableModel.addRow(new Object[]{
            b.getBookName(),
            b.getAuthor(),
            b.getGenre(),
            b.getPublishedYear(),
            b.getStock()
        });
    }
}

public void show() {
    frame.setVisible(true);
}

public void showError(String message) {
    JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
}

}

