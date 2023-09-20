import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.Month;
import java.util.Objects;

public class ExcelGeneratorGui extends JFrame{
    JTextField folderPathField, yearField, outputFileNameField;
    JComboBox<String> monthComboBox;

    public ExcelGeneratorGui() {

        try{
            for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()){
                if("Nimbus".equals(info.getName())){
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex){
            ex.printStackTrace();
        }

        setTitle("Excel Generator");
        setSize(700, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //setIconImage(Toolkit.getDefaultToolkit().getImage("/icon/excel.png")); // Set the application icon

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(ExcelGeneratorGui.class.getResource("/icon/excel.png")));
        setIconImage(icon.getImage());
        setResizable(false);

        // Create components
        JLabel folderLabel = new JLabel("Folder:");
        folderPathField = new JTextField(20);

        JLabel fileUploadedLabel = new JLabel("No file uploaded!");
        fileUploadedLabel.setForeground(Color.red);

        JButton uploadButton = new JButton("Upload");
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(ExcelGeneratorGui.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = fileChooser.getSelectedFile();
                folderPathField.setText(selectedFolder.getAbsolutePath());

                fileUploadedLabel.setText("File uploaded!");
                fileUploadedLabel.setForeground(new Color(34, 139, 34));
            }
        });

        JLabel yearLabel = new JLabel("Year:");
        yearField = new JTextField(20);

        JLabel monthLabel = new JLabel("Month:");
        monthComboBox = new JComboBox<>(getMonthNames());
        monthComboBox.addActionListener(e -> {
            // Nothing needed here, selection will be handled when generating Excel
        });

        JLabel outputFileName = new JLabel("Output File Name:");
        outputFileNameField = new JTextField(20);

        JButton generateButton = new JButton("Generate Excel");
        generateButton.addActionListener(e -> {
            String folderPath = folderPathField.getText();
            String year = yearField.getText();
            String selectedMonth = (String) monthComboBox.getSelectedItem(); // Get selected month from JComboBox
            String outputFileName1 = outputFileNameField.getText();

            // Call the function that generates the Excel file using Apache POI here
            // For simplicity, let's assume the function is named generateExcelFile
            if(folderPath.isEmpty() || year.isEmpty() || selectedMonth.isEmpty() || outputFileName1.isEmpty()){
                JOptionPane.showMessageDialog(ExcelGeneratorGui.this,
                        "Please complete all the fields.",
                        "Incomplete Fields",
                        JOptionPane.ERROR_MESSAGE);
            }else{
                int monthValue = getMonthValue(selectedMonth); // Convert month name to numeric value

                // Prompt the user to choose the save location
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showSaveDialog(ExcelGeneratorGui.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFolder = fileChooser.getSelectedFile();
                    String filePath = selectedFolder.getAbsolutePath() + File.separator + outputFileName1 + ".xlsx";

                    // Call the function that generates the Excel file using Apache POI here
                    // For simplicity, let's assume the function is named generateExcelFile
                    ExcelWriter.write(folderPath, Integer.parseInt(year), monthValue, filePath);

                    // Clear the text fields after successful generation
                    folderPathField.setText("");
                    yearField.setText("");
                    monthComboBox.setSelectedIndex(0); // Reset the month selection to January
                    outputFileNameField.setText("");
                    fileUploadedLabel.setText("No file uploaded!");
                    fileUploadedLabel.setForeground(Color.red);

                    // Show success message
                    JOptionPane.showMessageDialog(ExcelGeneratorGui.this,
                            "Excel file generated and saved successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }

        });

        //Create layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding around the main panel
        mainPanel.setBackground(new Color(240, 240, 240)); // Set background color

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(255, 255, 255)); // Set input panel background color
        inputPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); // Add border to the input panel

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);

        // Add components to the input panel
        inputPanel.add(folderLabel, constraints);
        constraints.gridx = 1;
        inputPanel.add(uploadButton, constraints);
        constraints.gridx = 2;
        inputPanel.add(fileUploadedLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        inputPanel.add(yearLabel, constraints);
        constraints.gridx = 1;
        inputPanel.add(yearField, constraints);

        constraints.gridy = 2;
        constraints.gridx = 0;
        inputPanel.add(monthLabel, constraints);
        constraints.gridx = 1;
        inputPanel.add(monthComboBox, constraints);

        constraints.gridy = 3;
        constraints.gridx = 0;
        inputPanel.add(outputFileName, constraints);
        constraints.gridx = 1;
        inputPanel.add(outputFileNameField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.CENTER;
        inputPanel.add(generateButton, constraints);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Add tooltips
        uploadButton.setToolTipText("Upload a folder");
        generateButton.setToolTipText("Generate Excel");
        outputFileNameField.setToolTipText("Choose the name for generated excel and put the extension for it");

        // Button hover effects and styling
        uploadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                uploadButton.setBackground(new Color(100, 180, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                uploadButton.setBackground(new Color(200, 200, 200));
            }
        });

        generateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                generateButton.setBackground(new Color(34, 139, 34));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                generateButton.setBackground(new Color(200, 200, 200));
            }
        });

        // Add the main panel to the frame
        add(mainPanel);

        // Stylish enhancements
        folderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        fileUploadedLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        yearLabel.setFont(new Font("Arial", Font.BOLD, 16));
        monthLabel.setFont(new Font("Arial", Font.BOLD, 16));
        outputFileName.setFont(new Font("Arial", Font.BOLD, 16));
        generateButton.setFont(new Font("Arial", Font.PLAIN, 14));
        uploadButton.setFont(new Font("Arial", Font.PLAIN, 14));
        monthComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add padding to components
        folderLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        yearLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        monthLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        outputFileName.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        generateButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Customize colors
        folderLabel.setForeground(new Color(51, 51, 51)); // Dark gray
        yearLabel.setForeground(new Color(51, 51, 51));   // Dark gray
        monthLabel.setForeground(new Color(51, 51, 51));  // Dark gray
        outputFileName.setForeground(new Color(51, 51, 51)); // Dark gray
        outputFileNameField.setForeground(new Color(51, 51, 51)); // Dark gray

        folderLabel.setBackground(new Color(230, 230, 230)); // Light gray background
        yearLabel.setBackground(new Color(230, 230, 230));   // Light gray background
        monthLabel.setBackground(new Color(230, 230, 230));  // Light gray background
        outputFileName.setBackground(new Color(230, 230, 230)); // Light gray background
        outputFileNameField.setBackground(new Color(255, 255, 255)); // White background

        folderLabel.setOpaque(true);
        yearLabel.setOpaque(true);
        monthLabel.setOpaque(true);
        outputFileName.setOpaque(true);
        outputFileNameField.setOpaque(true);
    }

    private String[] getMonthNames() {
        // Use the Java 8 Month enum to get the names of all months
        String[] monthNames = new String[Month.values().length];
        int i = 0;
        for (Month month : Month.values()) {
            monthNames[i] = month.toString();
            i++;
        }
        return monthNames;
    }

    private int getMonthValue(String monthName) {
        // Get the numeric value of the selected month based on the month name
        int monthValue = 1; // Default to January
        try {
            Month month = Month.valueOf(monthName.toUpperCase());
            monthValue = month.getValue();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return monthValue;
    }

}
