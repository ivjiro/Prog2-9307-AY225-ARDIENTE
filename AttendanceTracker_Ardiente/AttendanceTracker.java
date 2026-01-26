import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class AttendanceTracker extends JFrame {
    private JTextField nameField;
    private JTextField courseField;
    private JTextField timeField;
    private JTextField signatureField;
    
    public AttendanceTracker() {
        // Set up the main frame
        setTitle("Attendance Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Title
        JLabel titleLabel = new JLabel("Attendance Tracker", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        
        // Attendance Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel nameLabel = new JLabel("Attendance Name:");
        mainPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);
        
        // Course / Year
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel courseLabel = new JLabel("Course / Year:");
        mainPanel.add(courseLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        courseField = new JTextField(20);
        mainPanel.add(courseField, gbc);
        
        // Time In
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel timeLabel = new JLabel("Time In:");
        mainPanel.add(timeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        timeField = new JTextField(20);
        timeField.setEditable(false);
        timeField.setBackground(Color.WHITE);
        mainPanel.add(timeField, gbc);
        
        // E-Signature
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel signatureLabel = new JLabel("E-Signature:");
        mainPanel.add(signatureLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        signatureField = new JTextField(20);
        signatureField.setEditable(false);
        signatureField.setBackground(Color.WHITE);
        mainPanel.add(signatureField, gbc);
        
        // Submit Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        JButton submitButton = new JButton("Record Attendance");
        submitButton.setFont(new Font("Arial", Font.BOLD, 12));
        submitButton.addActionListener(e -> recordAttendance());
        mainPanel.add(submitButton, gbc);
        
        add(mainPanel);
        
        // Generate initial values
        generateTimeIn();
        generateSignature();
    }
    
    private void generateTimeIn() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        timeField.setText(now.format(formatter));
    }
    
    private void generateSignature() {
        // Generate E-Signature based on timestamp and UUID
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String signature = "BSCS-" + timestamp + "-" + uuid.toUpperCase();
        signatureField.setText(signature);
    }
    
    private void recordAttendance() {
        String name = nameField.getText().trim();
        String course = courseField.getText().trim();
        
        if (name.isEmpty() || course.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields (Name and Course/Year)",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Refresh time and signature
        generateTimeIn();
        generateSignature();
        
        String message = String.format(
            "Attendance Recorded Successfully!\n\n" +
            "Name: %s\n" +
            "Course/Year: %s\n" +
            "Time In: %s\n" +
            "E-Signature: %s",
            nameField.getText(),
            courseField.getText(),
            timeField.getText(),
            signatureField.getText()
        );
        
        JOptionPane.showMessageDialog(this,
            message,
            "Attendance Recorded",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AttendanceTracker frame = new AttendanceTracker();
            frame.setVisible(true);
        });
    }
}