package view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    
    public LoginView() {
        initializeComponents();
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Healthcare Management System - Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
    }
    
    private void setupLayout() {
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setLayout(new BorderLayout(15, 15));
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        // Title
        JLabel titleLabel = new JLabel("Healthcare Management System");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username/ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);
        
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Status label
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Login button
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(100, 35));
        mainPanel.add(loginButton);
        
        contentPane.add(mainPanel, BorderLayout.CENTER);
    }
    
    public String getUsername() {
        return usernameField.getText().trim();
    }
    
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
    
    public JPasswordField getPasswordField() {
        return passwordField;
    }
    
    public JButton getLoginButton() {
        return loginButton;
    }
    
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }
    
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
}

