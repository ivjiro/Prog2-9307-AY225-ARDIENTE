// Submitted By: Ardiente, Sofhia Nicole I. - 23-0328-307
// Student Record System - Java Version

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import javax.swing.filechooser.FileNameExtensionFilter;

public class StudentRecordSystem extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField idField, nameField, gradeField;
    private JTextField lab1Field, lab2Field, lab3Field, prelimField, attendanceField;
    private JTextField searchField;
    private JButton addButton, deleteButton, updateButton, clearSearchButton;
    private JButton exportButton;
    private JComboBox<String> searchColumnCombo;

    public StudentRecordSystem() {
        // Set window title with programmer identifier
        setTitle("Student Records - Ardiente, Sofhia Nicole I. - 23-0328-307");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        
        initializeComponents();
        loadDataFromCSV();
        
        setVisible(true);
    }

    private void initializeComponents() {
        // Color theme (purple)
        Color primaryPurple = new Color(102, 76, 255); // strong purple
        Color midPurple = new Color(118, 75, 162);
        Color lightPurple = new Color(230, 224, 255);
        Color accent = new Color(153, 102, 255);
        Color textColor = Color.BLACK;
        // Ensure default label foregrounds are black
        UIManager.put("Label.foreground", textColor);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(lightPurple);

        // Create search panel at the top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Students"));
        searchPanel.setBackground(new Color(250, 245, 255));
        
        searchPanel.add(new JLabel("Search by:"));
        
        String[] searchColumns = {"All Columns", "Student ID", "First Name", "Last Name"};
        searchColumnCombo = new JComboBox<>(searchColumns);
        searchPanel.add(searchColumnCombo);
        
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchTable();
            }
        });
        searchPanel.add(searchField);
        
        clearSearchButton = new JButton("Clear Search");
        clearSearchButton.addActionListener(e -> clearSearch());
        clearSearchButton.setBackground(midPurple);
        clearSearchButton.setForeground(Color.WHITE);
        clearSearchButton.setOpaque(true);
        clearSearchButton.setBorderPainted(false);
        searchPanel.add(clearSearchButton);
        
        JLabel recordCountLabel = new JLabel();
        searchPanel.add(recordCountLabel);
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Create table with column names matching CSV structure
        String[] columnNames = {"StudentID", "First Name", "Last Name", "LAB WORK 1", 
                                "LAB WORK 2", "LAB WORK 3", "PRELIM EXAM", "ATTENDANCE GRADE"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        table = new JTable(tableModel);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(Color.WHITE);
        table.setForeground(textColor);
        table.setSelectionBackground(accent);
        table.setSelectionForeground(Color.WHITE);
        // Force table header text to black
table.getTableHeader().setDefaultRenderer(
    new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            label.setForeground(Color.BLACK);     // 🔥 TEXT COLOR
            label.setBackground(new Color(230, 224, 255)); // light purple
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);

            return label;
        }
    }
);

        table.setRowHeight(28);
        
        // Add TableRowSorter for search functionality
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        // Force table cell text color to black regardless of selection/hover
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.BLACK);
                return c;
            }
        });

        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(lightPurple);
        // Style header
        table.getTableHeader().setBackground(primaryPurple);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create input panel with all fields
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        inputPanel.setBackground(new Color(250, 245, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Column 1 - StudentID, First Name, Last Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Student ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        idField = new JTextField(15);
        inputPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("First Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(15);
        // Highlight the First Name field with a blue border and subtle background
        nameField.setOpaque(true);
        nameField.setForeground(Color.BLACK);
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Last Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gradeField = new JTextField(15);
        gradeField.setForeground(Color.BLACK);
        inputPanel.add(gradeField, gbc);

        // Column 2 - Grade fields
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Lab Work 1:"), gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        lab1Field = new JTextField(10);
        lab1Field.setText("0");
        lab1Field.setForeground(Color.BLACK);
        inputPanel.add(lab1Field, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Lab Work 2:"), gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        lab2Field = new JTextField(10);
        lab2Field.setText("0");
        lab2Field.setForeground(Color.BLACK);
        inputPanel.add(lab2Field, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Lab Work 3:"), gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        lab3Field = new JTextField(10);
        lab3Field.setText("0");
        lab3Field.setForeground(Color.BLACK);
        inputPanel.add(lab3Field, gbc);

        // Column 3 - More grade fields
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Prelim Exam:"), gbc);
        
        gbc.gridx = 5;
        gbc.weightx = 1.0;
        prelimField = new JTextField(10);
        prelimField.setText("0");
        prelimField.setForeground(Color.BLACK);
        inputPanel.add(prelimField, gbc);

        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Attendance:"), gbc);
        
        gbc.gridx = 5;
        gbc.weightx = 1.0;
        attendanceField = new JTextField(10);
        attendanceField.setText("0");
        attendanceField.setForeground(Color.BLACK);
        inputPanel.add(attendanceField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        addButton = new JButton("Add Student");
        addButton.addActionListener(e -> addStudent());
        addButton.setBackground(primaryPurple);
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        buttonPanel.add(addButton);

        updateButton = new JButton("Update Student");
        updateButton.addActionListener(e -> updateStudent());
        updateButton.setBackground(midPurple);
        updateButton.setForeground(Color.WHITE);
        updateButton.setOpaque(true);
        updateButton.setBorderPainted(false);
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete Student");
        deleteButton.addActionListener(e -> deleteStudent());
        deleteButton.setBackground(new Color(130, 80, 200));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        buttonPanel.add(deleteButton);

        if (exportButton != null) {
            exportButton.setBackground(accent);
            exportButton.setForeground(Color.WHITE);
            exportButton.setOpaque(true);
            exportButton.setBorderPainted(false);
            buttonPanel.add(exportButton);
        }

        exportButton = new JButton("Export CSV");
        exportButton.addActionListener(e -> exportToCSV());
        buttonPanel.add(exportButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 6;
        inputPanel.add(buttonPanel, gbc);

        // Add selection listener to populate fields when row is selected
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFieldsFromSelection();
            }
        });

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadDataFromCSV() {
        Path csvPath = Paths.get("Prelim-Exam", "JAVA", "MOCK_DATA.csv");
        String line;

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            // Skip header line
            br.readLine();
            
            // Read data lines
            while ((line = br.readLine()) != null) {
                // Split by comma
                String[] data = line.split(",");
                
                if (data.length >= 8) {
                    // Add row to table with all 8 columns
                    tableModel.addRow(new Object[]{
                        data[0].trim(),  // StudentID
                        data[1].trim(),  // first_name
                        data[2].trim(),  // last_name
                        data[3].trim(),  // LAB WORK 1
                        data[4].trim(),  // LAB WORK 2
                        data[5].trim(),  // LAB WORK 3
                        data[6].trim(),  // PRELIM EXAM
                        data[7].trim()   // ATTENDANCE GRADE
                    });
                }
            }
            
            System.out.println("Data loaded successfully from " + csvPath.toString());
            updateRecordCount();
            
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            JOptionPane.showMessageDialog(
                this,
                "Error loading CSV file: " + e.getMessage() + "\nStarting with empty table.",
                "File Load Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addStudent() {
        String id = idField.getText().trim();
        String firstName = nameField.getText().trim();
        String lastName = gradeField.getText().trim();
        String lab1 = lab1Field.getText().trim();
        String lab2 = lab2Field.getText().trim();
        String lab3 = lab3Field.getText().trim();
        String prelim = prelimField.getText().trim();
        String attendance = attendanceField.getText().trim();

        // Validation
        if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please fill in Student ID, First Name, and Last Name.",
                "Input Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validate grades are numeric
        try {
            Integer.parseInt(lab1);
            Integer.parseInt(lab2);
            Integer.parseInt(lab3);
            Integer.parseInt(prelim);
            Integer.parseInt(attendance);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "All grade fields must be numeric values.",
                "Input Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Add to table with all grades
        tableModel.addRow(new Object[]{
            id, 
            firstName, 
            lastName, 
            lab1,
            lab2,
            lab3,
            prelim,
            attendance
        });

        // Clear input fields
        clearFields();
        
        // Update record count
        updateRecordCount();

        JOptionPane.showMessageDialog(
            this,
            "Student added successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void deleteStudent() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a student to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this student?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            clearFields();
            updateRecordCount();
            JOptionPane.showMessageDialog(
                this,
                "Student deleted successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void updateStudent() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a student to update.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String id = idField.getText().trim();
        String firstName = nameField.getText().trim();
        String lastName = gradeField.getText().trim();
        String lab1 = lab1Field.getText().trim();
        String lab2 = lab2Field.getText().trim();
        String lab3 = lab3Field.getText().trim();
        String prelim = prelimField.getText().trim();
        String attendance = attendanceField.getText().trim();

        // Validation
        if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please fill in Student ID, First Name, and Last Name.",
                "Input Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validate grades are numeric
        try {
            Integer.parseInt(lab1);
            Integer.parseInt(lab2);
            Integer.parseInt(lab3);
            Integer.parseInt(prelim);
            Integer.parseInt(attendance);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "All grade fields must be numeric values.",
                "Input Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Update the selected row
        tableModel.setValueAt(id, selectedRow, 0);
        tableModel.setValueAt(firstName, selectedRow, 1);
        tableModel.setValueAt(lastName, selectedRow, 2);
        tableModel.setValueAt(lab1, selectedRow, 3);
        tableModel.setValueAt(lab2, selectedRow, 4);
        tableModel.setValueAt(lab3, selectedRow, 5);
        tableModel.setValueAt(prelim, selectedRow, 6);
        tableModel.setValueAt(attendance, selectedRow, 7);

        JOptionPane.showMessageDialog(
            this,
            "Student updated successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void exportToCSV() {
        // Suggest default filename
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save CSV");
        chooser.setSelectedFile(new java.io.File("student_records.csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));

        int userSelection = chooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return; // user cancelled
        }

        Path savePath = chooser.getSelectedFile().toPath();
        // Ensure .csv extension
        if (!savePath.toString().toLowerCase().endsWith(".csv")) {
            savePath = Paths.get(savePath.toString() + ".csv");
        }

        try (BufferedWriter bw = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8)) {
            // write header
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                bw.write(escapeCSV(tableModel.getColumnName(c)));
                if (c < tableModel.getColumnCount() - 1) bw.write(',');
            }
            bw.newLine();

            // write rows
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    Object val = tableModel.getValueAt(r, c);
                    bw.write(escapeCSV(val == null ? "" : val.toString()));
                    if (c < tableModel.getColumnCount() - 1) bw.write(',');
                }
                bw.newLine();
            }

            bw.flush();
            JOptionPane.showMessageDialog(this, "CSV exported to: " + savePath.toString(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String escapeCSV(String s) {
        if (s.contains("\"") || s.contains(",") || s.contains("\n") || s.contains("\r")) {
            s = s.replace("\"", "\"\"");
            return '"' + s + '"';
        }
        return s;
    }

    private void populateFieldsFromSelection() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow != -1) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            gradeField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            lab1Field.setText(tableModel.getValueAt(selectedRow, 3).toString());
            lab2Field.setText(tableModel.getValueAt(selectedRow, 4).toString());
            lab3Field.setText(tableModel.getValueAt(selectedRow, 5).toString());
            prelimField.setText(tableModel.getValueAt(selectedRow, 6).toString());
            attendanceField.setText(tableModel.getValueAt(selectedRow, 7).toString());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        gradeField.setText("");
        lab1Field.setText("0");
        lab2Field.setText("0");
        lab3Field.setText("0");
        prelimField.setText("0");
        attendanceField.setText("0");
        table.clearSelection();
    }

    private void searchTable() {
        String searchText = searchField.getText().trim();
        int selectedColumn = searchColumnCombo.getSelectedIndex();
        
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                if (selectedColumn == 0) {
                    // Search all columns
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                } else {
                    // Search specific column (selectedColumn - 1 because index 0 is "All Columns")
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, selectedColumn - 1));
                }
            } catch (java.util.regex.PatternSyntaxException e) {
                // If invalid regex, just show all
                sorter.setRowFilter(null);
            }
        }
        
        // Update record count
        updateRecordCount();
    }

    private void clearSearch() {
        searchField.setText("");
        searchColumnCombo.setSelectedIndex(0);
        sorter.setRowFilter(null);
        updateRecordCount();
    }

    private void updateRecordCount() {
        int displayedRows = table.getRowCount();
        int totalRows = tableModel.getRowCount();
        
        String countText = String.format("Showing %d of %d records", displayedRows, totalRows);
        
        // Find and update the record count label
        JPanel searchPanel = (JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(0);
        for (Component comp : searchPanel.getComponents()) {
            if (comp instanceof JLabel && ((JLabel) comp).getText().contains("records")) {
                ((JLabel) comp).setText(countText);
                return;
            }
        }
        
        // If label not found, find it by position (last component)
        Component lastComp = searchPanel.getComponent(searchPanel.getComponentCount() - 1);
        if (lastComp instanceof JLabel) {
            ((JLabel) lastComp).setText(countText);
        }
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new StudentRecordSystem());
    }
}
