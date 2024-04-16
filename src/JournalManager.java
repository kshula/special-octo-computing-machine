import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class JournalManager extends JFrame {
    private JTextField bookNameField, conclusionField, citationField;
    private JButton addButton, deleteButton;
    private JTextArea resultArea;

    private Connection connection;
    private PreparedStatement insertStatement;
    private PreparedStatement deleteStatement;

    public JournalManager() {
        setTitle("Journal Manager");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        bookNameField = new JTextField(20);
        conclusionField = new JTextField(20);
        citationField = new JTextField(20);

        addButton = new JButton("Add Journal");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addJournal();
            }
        });

        deleteButton = new JButton("Delete Journal");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteJournal();
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Book Name:"));
        inputPanel.add(bookNameField);
        inputPanel.add(new JLabel("Conclusion:"));
        inputPanel.add(conclusionField);
        inputPanel.add(new JLabel("Citation:"));
        inputPanel.add(citationField);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);
        add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);

        // Connect to SQLite database
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:db/scholar.db");
            insertStatement = connection.prepareStatement("INSERT INTO Journals (book_name, conclusion, citation) VALUES (?, ?, ?)");
            deleteStatement = connection.prepareStatement("DELETE FROM Journals WHERE id = ?");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void addJournal() {
        String bookName = bookNameField.getText();
        String conclusion = conclusionField.getText();
        String citation = citationField.getText();

        try {
            insertStatement.setString(1, bookName);
            insertStatement.setString(2, conclusion);
            insertStatement.setString(3, citation);
            insertStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Journal added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding journal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteJournal() {
        String idStr = JOptionPane.showInputDialog(this, "Enter the ID of the journal to delete:");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                deleteStatement.setInt(1, id);
                int rowsAffected = deleteStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Journal deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Journal not found with ID " + id, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException | SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting journal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JournalManager manager = new JournalManager();
                manager.setVisible(true);
            }
        });
    }
}
