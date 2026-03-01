package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.Models.Book;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class ViewBookView {

    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private static final String[] COLUMN_NAMES = {"Book Name", "Author", "Genre", "Published Year", "Stock","Actions"};
    private static final int EDIT_COLUMN_INDEX = 5;

    public ViewBookView() {
        frame = new JFrame("View Books");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 520);
        frame.getContentPane().setBackground(new Color(200, 230, 201));

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(27, 94, 32));
        table.getTableHeader().setForeground(Color.WHITE);
        Color buttonGreen = new Color(46, 125, 50);
        table.getColumnModel().getColumn(EDIT_COLUMN_INDEX).setCellRenderer(createEditButtonRenderer(buttonGreen));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0 && col == EDIT_COLUMN_INDEX) {
                    table.getSelectionModel().setSelectionInterval(row, row);
                    fireEditRequested();
                }
            }
        });
        table.getColumnModel().getColumn(EDIT_COLUMN_INDEX).setPreferredWidth(70);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(new Color(200, 230, 201));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.setBackground(new Color(200, 230, 201));
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(new Color(27, 94, 32));
        searchField = new JTextField(25);
        searchField.setBackground(Color.WHITE);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fireFilterRequested();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fireFilterRequested();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fireFilterRequested();
            }
        });
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(46, 125, 50));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(true);
        refreshButton.addActionListener(e -> fireRefreshRequested());

        JButton editButton = new JButton("Edit");
        editButton.setBackground(new Color(46, 125, 50));
        editButton.setForeground(Color.WHITE);
        editButton.setOpaque(true);
        editButton.setBorderPainted(true);
        editButton.addActionListener(e -> fireEditRequested());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(200, 230, 201));
        bottomPanel.add(refreshButton);
        bottomPanel.add(editButton);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(new Color(200, 230, 201));
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
    }

    private ActionListener refreshListener;
    private ActionListener filterListener;
    private ActionListener editListener;

    public void addRefreshListener(ActionListener listener) {
        this.refreshListener = listener;
    }

    public void addFilterListener(ActionListener listener) {
        this.filterListener = listener;
    }

    public String getSearchText() {
        return searchField.getText().trim();
    }

    /** Returns the table row index that is selected, or -1 if none. */
    public int getSelectedRowIndex() {
        return table.getSelectedRow();
    }

    private void fireRefreshRequested() {
        if (refreshListener != null) {
            refreshListener.actionPerformed(null);
        }
    }

    private void fireFilterRequested() {
        if (filterListener != null) {
            filterListener.actionPerformed(null);
        }
    }

    public void addEditListener(ActionListener listener) {
        this.editListener = listener;
    }

    private void fireEditRequested() {
        if (editListener != null) {
            editListener.actionPerformed(null);
        }
    }

    private static TableCellRenderer createEditButtonRenderer(Color buttonGreen) {
        Border buttonBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(2, 10, 2, 10));
        return new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                setBackground(buttonGreen);
                setForeground(Color.WHITE);
                setHorizontalAlignment(JLabel.CENTER);
                setText("Edit");
                setBorder(buttonBorder);
                setOpaque(true);
                return this;
            }
        };
    }

    public void displayBooks(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                b.getBookName(),
                b.getAuthor(),
                b.getGenre(),
                b.getPublishedYear(),
                b.getStock(),
                "Edit"
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
