import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;


class UserData {
    String firstName;
    String lastName;
    String username;
    String password;
    int balance;
    int accountNumber;

    public UserData(String firstName, String lastName, String username, 
                   String password, int balance, int accountNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.accountNumber = accountNumber;
    }
}

class Transaction {
    String sender;
    String receiver;
    int amount;
    String type;
    LocalDateTime timestamp;

    public Transaction(String sender, String receiver, int amount, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
}

 public class BankSystemGUI extends JFrame {
    private static final String USERS_FILE = "users.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private List<UserData> users = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private int nextAccountNumber = 1000101;
    private UserData currentUser = null;
    
    // UI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private final HashMap<String, JPanel> panelMap = new HashMap<>();
    
     // Dashboard components
     private JLabel userNameLabel;
     private JLabel accountNumberLabel;
     private JLabel balanceLabel;
     private DefaultTableModel transactionModel;


    public BankSystemGUI() {
        // Setup the main frame
        setTitle("Bank System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
        
        // Create different screens
        createLoginPanel();
        createRegisterPanel();
        createDashboardPanel();
        
        // Load data
        loadUsers();
        loadTransactions();
        
        // Show login screen
        cardLayout.show(mainPanel, "Login");
        setVisible(true);
        
    }

    private void createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
    
        JLabel titleLabel = new JLabel("Bank System Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
    
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
    
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
    
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Create New Account");
    
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);
        formPanel.add(registerButton);
    
        loginPanel.add(Box.createVerticalGlue());
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        loginPanel.add(formPanel);
        loginPanel.add(Box.createVerticalGlue());
    
        // Login action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
    
            currentUser = login(username, password);
            if (currentUser != null) {
                updateDashboard();
                cardLayout.show(mainPanel, "Dashboard");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
            usernameField.setText("");
            passwordField.setText("");
        });
    
        registerButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Register");
        });
    
        mainPanel.add(loginPanel, "Login");
    }
    
    
    
    private void createRegisterPanel() {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
        registerPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
        
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(20);
        
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(20);
        
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        
        JButton createButton = new JButton("Create Account");
        JButton backButton = new JButton("Back to Login");
        
        formPanel.add(firstNameLabel);
        formPanel.add(firstNameField);
        formPanel.add(lastNameLabel);
        formPanel.add(lastNameField);
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(createButton);
        formPanel.add(backButton);
        
        registerPanel.add(Box.createVerticalGlue());
        registerPanel.add(titleLabel);
        registerPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        registerPanel.add(formPanel);
        registerPanel.add(Box.createVerticalGlue());
        
        // Fixed registration with trimmed inputs
        createButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            
            if (firstName.isEmpty() || lastName.isEmpty() || 
                username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "All fields are required", 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = createAccount(firstName, lastName, username, password);
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Account created successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearFields(firstNameField, lastNameField, usernameField, passwordField);
                cardLayout.show(mainPanel, "Login");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Username already exists", 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        backButton.addActionListener(e -> {
            clearFields(firstNameField, lastNameField, usernameField, passwordField);
            cardLayout.show(mainPanel, "Login");
        });
        
        mainPanel.add(registerPanel, "Register");
    }
    
    private void createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());

        userNameLabel = new JLabel();
        userNameLabel.setHorizontalAlignment(JLabel.CENTER);
        accountNumberLabel = new JLabel();
        accountNumberLabel.setHorizontalAlignment(JLabel.CENTER);
        balanceLabel = new JLabel();
        balanceLabel.setHorizontalAlignment(JLabel.CENTER);
        
        transactionModel = new DefaultTableModel(
            new Object[] {"Type", "From", "To", "Amount", "Date"}, 0);
        
        // Top panel with user info
        JPanel userInfoPanel = new JPanel();
        JLabel userNameLabel = new JLabel();
        JLabel accountNumberLabel = new JLabel();
        JLabel balanceLabel = new JLabel();
        
        // Update user info panel layout
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.add(userNameLabel);
        userInfoPanel.add(accountNumberLabel);
        userInfoPanel.add(balanceLabel);

          // Transaction history
          JTable transactionTable = new JTable(transactionModel);
          JScrollPane scrollPane = new JScrollPane(transactionTable);
          JPanel historyPanel = new JPanel(new BorderLayout());
          historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Tab panel for different features
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Transaction History Tab
        JPanel historyPanel = new JPanel(new BorderLayout());
        DefaultTableModel transactionModel = new DefaultTableModel(
            new Object[] {"Type", "From", "To", "Amount", "Date"}, 0);
        JTable transactionTable = new JTable(transactionModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Transfer Money Tab
        JPanel transferPanel = new JPanel();
        transferPanel.setLayout(new BoxLayout(transferPanel, BoxLayout.Y_AXIS));
        transferPanel.setBorder(new EmptyBorder(20, 100, 20, 100));
        
        JLabel recipientLabel = new JLabel("Recipient Username:");
        JTextField recipientField = new JTextField(20);
        
        JLabel amountLabel = new JLabel("Amount:");
        JTextField transferAmountField = new JTextField(20);
        
        JButton transferButton = new JButton("Transfer Money");
        transferButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        transferPanel.add(Box.createVerticalGlue());
        transferPanel.add(recipientLabel);
        transferPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        transferPanel.add(recipientField);
        transferPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        transferPanel.add(amountLabel);
        transferPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        transferPanel.add(transferAmountField);
        transferPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        transferPanel.add(transferButton);
        transferPanel.add(Box.createVerticalGlue());
        
        // Deposit/Withdraw Tab
        JPanel depositWithdrawPanel = new JPanel();
        depositWithdrawPanel.setLayout(new BoxLayout(depositWithdrawPanel, BoxLayout.Y_AXIS));
        depositWithdrawPanel.setBorder(new EmptyBorder(20, 100, 20, 100));
        
        JLabel actionLabel = new JLabel("Amount:");
        JTextField actionAmountField = new JTextField(20);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        
        depositWithdrawPanel.add(Box.createVerticalGlue());
        depositWithdrawPanel.add(actionLabel);
        depositWithdrawPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        depositWithdrawPanel.add(actionAmountField);
        depositWithdrawPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        depositWithdrawPanel.add(buttonPanel);
        depositWithdrawPanel.add(Box.createVerticalGlue());
        
        // Add tabs to tabbed pane
        tabbedPane.addTab("Transaction History", historyPanel);
        tabbedPane.addTab("Transfer Money", transferPanel);
        tabbedPane.addTab("Deposit/Withdraw", depositWithdrawPanel);
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        
        // Add everything to dashboard
        dashboardPanel.add(userInfoPanel, BorderLayout.NORTH);
        dashboardPanel.add(tabbedPane, BorderLayout.CENTER);
        dashboardPanel.add(logoutButton, BorderLayout.SOUTH);
        
        // Setup action listeners
        transferButton.addActionListener(e -> {
            String recipient = recipientField.getText();
            String amountStr = transferAmountField.getText();
            
            try {
                int amount = Integer.parseInt(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Amount must be positive", 
                        "Transfer Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean success = transferMoney(currentUser, recipient, amount);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Transfer successful", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    updateDashboard();
                    recipientField.setText("");
                    transferAmountField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Transfer failed. Check recipient and balance.", 
                        "Transfer Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid amount", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        depositButton.addActionListener(e -> {
            String amountStr = actionAmountField.getText();
            
            try {
                int amount = Integer.parseInt(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Amount must be positive", 
                        "Deposit Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean success = creditMoney(currentUser, amount);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Deposit successful", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    updateDashboard();
                    actionAmountField.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid amount", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        withdrawButton.addActionListener(e -> {
            String amountStr = actionAmountField.getText();
            
            try {
                int amount = Integer.parseInt(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Amount must be positive", 
                        "Withdrawal Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean success = debitMoney(currentUser, amount);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Withdrawal successful", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    updateDashboard();
                    actionAmountField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Insufficient balance", 
                        "Withdrawal Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid amount", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        logoutButton.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(mainPanel, "Login");
        });
        
        // Store references
        dashboardPanel.putClientProperty("userNameLabel", userNameLabel);
        dashboardPanel.putClientProperty("accountNumberLabel", accountNumberLabel);
        dashboardPanel.putClientProperty("balanceLabel", balanceLabel);
        dashboardPanel.putClientProperty("transactionModel", transactionModel);
        
        mainPanel.add(dashboardPanel, "Dashboard");

                // Add to panel map
                panelMap.put("Dashboard", dashboardPanel);
                mainPanel.add(dashboardPanel, "Dashboard");
    }
    
    private void updateDashboard() {
        if (currentUser == null) return;

         // Update using instance variables
         userNameLabel.setText("Welcome, " + currentUser.firstName + " " + currentUser.lastName);
         accountNumberLabel.setText("Account #: " + currentUser.accountNumber);
         balanceLabel.setText("Balance: $" + currentUser.balance);
        
        JPanel dashboardPanel = (JPanel) getCardPanel("Dashboard");
        JLabel userNameLabel = (JLabel) dashboardPanel.getClientProperty("userNameLabel");
        JLabel accountNumberLabel = (JLabel) dashboardPanel.getClientProperty("accountNumberLabel");
        JLabel balanceLabel = (JLabel) dashboardPanel.getClientProperty("balanceLabel");
        DefaultTableModel transactionModel = (DefaultTableModel) 
            dashboardPanel.getClientProperty("transactionModel");
        
     // Update user info
    userNameLabel.setText("Welcome, " + currentUser.firstName + " " + currentUser.lastName);
    accountNumberLabel.setText("Account #: " + currentUser.accountNumber);
    balanceLabel.setText("Balance: $" + currentUser.balance);

        
    }    // Update transaction history
       
    
 
    
    
    // File handling methods
    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            writer.write("AccountNumber|FirstName|LastName|UserName|Password|Balance\n");
            for (UserData user : users) {
                writer.write(String.format("%d|%s|%s|%s|%s|%d\n",
                    user.accountNumber, user.firstName, user.lastName,
                    user.username, user.password, user.balance));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving users.", 
                "File Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveTransactions() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            writer.write("Sender|Receiver|Amount|Type|Timestamp\n");
            for (Transaction t : transactions) {
                writer.write(String.format("%s|%s|%d|%s|%s\n",
                    t.sender, t.receiver, t.amount, t.type,
                    t.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving transactions.", 
                "File Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
                    writer.write("AccountNumber|FirstName|LastName|UserName|Password|Balance\n");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error creating users file.", 
                    "File Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 6) continue;
                users.add(new UserData(
                    parts[1], parts[2], parts[3], parts[4],
                    Integer.parseInt(parts[5]), Integer.parseInt(parts[0])
                ));
                nextAccountNumber = Math.max(nextAccountNumber, 
                    Integer.parseInt(parts[0]) + 1);
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading users.", 
                "File Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTransactions() {
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
                    writer.write("Sender|Receiver|Amount|Type|Timestamp\n");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error creating transactions file.", 
                    "File Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;
                Transaction t = new Transaction(
                    parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]
                );
                t.timestamp = LocalDateTime.parse(parts[4], 
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                transactions.add(t);
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading transactions.", 
                "File Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Core banking operations
    public UserData login(String username, String password) {
        for (UserData user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                return user;
            }
        }
        return null;
    }

    public boolean createAccount(String firstName, String lastName, 
                               String username, String password) {
        for (UserData user : users) {
            if (user.username.equals(username)) return false;
        }
        users.add(new UserData(firstName, lastName, username, password, 
                             120000, nextAccountNumber++));
        saveUsers();
        return true;
    }

    public boolean creditMoney(UserData user, int amount) {
        user.balance += amount;
        transactions.add(new Transaction("Bank", user.username, amount, "Credit"));
        saveTransactions();
        saveUsers();
        return true;
    }

    public boolean debitMoney(UserData user, int amount) {
        if (user.balance < amount) return false;
        user.balance -= amount;
        transactions.add(new Transaction(user.username, "Bank", amount, "Debit"));
        saveTransactions();
        saveUsers();
        return true;
    }

    public boolean transferMoney(UserData sender, String recipientUsername, int amount) {
        if (sender.balance < amount) return false;
        UserData recipient = users.stream()
            .filter(u -> u.username.equals(recipientUsername))
            .findFirst()
            .orElse(null);
        if (recipient == null) return false;
        
        sender.balance -= amount;
        recipient.balance += amount;
        transactions.add(new Transaction(sender.username, recipient.username, 
                                      amount, "Transfer"));
        saveTransactions();
        saveUsers();
        return true;
    }

    public List<Transaction> getTransactions(String username) {
        List<Transaction> userTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.sender.equals(username) || t.receiver.equals(username)) {
                userTransactions.add(t);
            }
        }
        return userTransactions;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new BankSystemGUI();
        });
    }
}
