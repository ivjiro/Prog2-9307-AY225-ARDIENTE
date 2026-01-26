import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PrelimGradeCalculator {

    private JTextField attendanceField;
    private JTextField lw1Field;
    private JTextField lw2Field;
    private JTextField lw3Field;
    private JTextArea outputArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrelimGradeCalculator().createAndShowGUI());
    }

    private void createAndShowGUI() {
        // Create the main window
        JFrame frame = new JFrame("Prelim Grade Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 850);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel for title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(102, 126, 234));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("PRELIM GRADE CALCULATOR");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 210), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addLabel(inputPanel, "Number of Attendances (0-100):", c, row);
        attendanceField = addTextField(inputPanel, c, row++);

        addLabel(inputPanel, "Lab Work 1 Grade (0-100):", c, row);
        lw1Field = addTextField(inputPanel, c, row++);

        addLabel(inputPanel, "Lab Work 2 Grade (0-100):", c, row);
        lw2Field = addTextField(inputPanel, c, row++);

        addLabel(inputPanel, "Lab Work 3 Grade (0-100):", c, row);
        lw3Field = addTextField(inputPanel, c, row++);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton calcButton = new JButton("Calculate Required Score");
        calcButton.setFont(new Font("Arial", Font.BOLD, 15));
        calcButton.setBackground(new Color(102, 126, 234));
        calcButton.setForeground(Color.WHITE);
        calcButton.setFocusPainted(false);
        calcButton.setPreferredSize(new Dimension(300, 40));
        calcButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(calcButton);
        
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        inputPanel.add(buttonPanel, c);

        // Output panel
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBackground(Color.WHITE);
        outputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 210), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel outputLabel = new JLabel("Results:");
        outputLabel.setFont(new Font("Arial", Font.BOLD, 14));
        outputLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        outputArea = new JTextArea(18, 60);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setBackground(new Color(250, 250, 252));
        outputArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        outputArea.setText("Enter your grades above and click 'Calculate' to see your required Prelim Exam scores.");
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230), 1));
        
        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        // Add all panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(outputPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        frame.getContentPane().add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Button action listener
        calcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                computeAndShow();
            }
        });
    }

    private void computeAndShow() {
        try {
            double attendance = Double.parseDouble(attendanceField.getText().trim());
            double labWork1 = Double.parseDouble(lw1Field.getText().trim());
            double labWork2 = Double.parseDouble(lw2Field.getText().trim());
            double labWork3 = Double.parseDouble(lw3Field.getText().trim());

            // Validate input ranges
            if (attendance < 0 || attendance > 100 || labWork1 < 0 || labWork1 > 100 ||
                labWork2 < 0 || labWork2 > 100 || labWork3 < 0 || labWork3 > 100) {
                JOptionPane.showMessageDialog(null, 
                    "All values must be between 0 and 100.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Computations
            // Lab Work Average = (LW1 + LW2 + LW3) / 3
            double labWorkAverage = (labWork1 + labWork2 + labWork3) / 3.0;
            
            // Class Standing = (40% * Attendance) + (60% * Lab Work Average)
            double classStanding = (0.40 * attendance) + (0.60 * labWorkAverage);

            // Prelim Grade = (70% * Prelim Exam) + (30% * Class Standing)
            // Required Prelim Exam = (Target Grade - 0.30 * Class Standing) / 0.70
            double requiredPrelimForPassing = (75 - (0.30 * classStanding)) / 0.70;
            double requiredPrelimForExcellent = (100 - (0.30 * classStanding)) / 0.70;

            StringBuilder out = new StringBuilder();
            out.append("═══════════════════════════════════════════════════════════════════\n");
            out.append("                       COMPUTATION RESULTS\n");
            out.append("═══════════════════════════════════════════════════════════════════\n\n");
            
            out.append("📋 INPUT SUMMARY:\n");
            out.append("───────────────────────────────────────────────────────────────────\n");
            out.append(String.format("   Attendance Score:              %.2f%%\n", attendance));
            out.append(String.format("   Lab Work 1:                    %.2f%%\n", labWork1));
            out.append(String.format("   Lab Work 2:                    %.2f%%\n", labWork2));
            out.append(String.format("   Lab Work 3:                    %.2f%%\n\n", labWork3));

            out.append("🧮 COMPUTED VALUES:\n");
            out.append("───────────────────────────────────────────────────────────────────\n");
            out.append(String.format("   Lab Work Average:              %.2f%%\n", labWorkAverage));
            out.append(String.format("   Class Standing (30%%):          %.2f%%\n\n", classStanding));

            out.append("🎯 REQUIRED PRELIM EXAM SCORES:\n");
            out.append("───────────────────────────────────────────────────────────────────\n");
            out.append(String.format("   To PASS (75%%):                 %.2f%%\n", requiredPrelimForPassing));
            out.append(String.format("   To achieve EXCELLENT (100%%):   %.2f%%\n\n", requiredPrelimForExcellent));

            out.append("═══════════════════════════════════════════════════════════════════\n");
            out.append("                            REMARKS\n");
            out.append("═══════════════════════════════════════════════════════════════════\n\n");

            out.append("📝 PASSING STATUS:\n");
            if (requiredPrelimForPassing > 100) {
                out.append("   ⚠️  Unfortunately, it is mathematically impossible to pass\n");
                out.append("      the Prelim period based on your current Class Standing.\n");
                out.append("      The required Prelim Exam score exceeds 100%.\n");
            } else if (requiredPrelimForPassing < 0) {
                out.append("   ✅ Congratulations! Your Class Standing is high enough that\n");
                out.append("      you will pass the Prelim period regardless of your\n");
                out.append("      Prelim Exam score!\n");
            } else {
                out.append(String.format("   → You need to score at least %.2f%% on the Prelim Exam\n", requiredPrelimForPassing));
                out.append("     to pass the Prelim period.\n");
            }

            out.append("\n⭐ EXCELLENT STATUS:\n");
            if (requiredPrelimForExcellent > 100) {
                out.append("   ⚠️  Achieving an Excellent grade (100%) is not possible\n");
                out.append("      based on your current Class Standing.\n");
            } else if (requiredPrelimForExcellent < 0) {
                out.append("   🌟 Your Class Standing guarantees an Excellent grade\n");
                out.append("      regardless of Prelim Exam performance!\n");
            } else {
                out.append(String.format("   → To achieve an Excellent grade, you need %.2f%%\n", requiredPrelimForExcellent));
                out.append("     on the Prelim Exam.\n");
            }

            out.append("\n═══════════════════════════════════════════════════════════════════\n");

            outputArea.setText(out.toString());
            outputArea.setCaretPosition(0);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, 
                "Please enter valid numeric values for all fields.", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addLabel(JPanel p, String text, GridBagConstraints c, int row) {
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 1;
        c.weightx = 0.3;
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        p.add(label, c);
    }

    private JTextField addTextField(JPanel p, GridBagConstraints c, int row) {
        JTextField tf = new JTextField(20);
        tf.setFont(new Font("Arial", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 220), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        c.gridx = 1;
        c.gridy = row;
        c.weightx = 0.7;
        p.add(tf, c);
        return tf;
    }
}