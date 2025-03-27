import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        JLabel titleLabel = new JLabel("Bank System Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
    
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(250, 35));
    
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(250, 35));
    
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(120, 40));
    
        JButton registerButton = new JButton("Create New Account");
        registerButton.setPreferredSize(new Dimension(180, 40));
    
        // Layout configuration remains the same
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);
    
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        loginPanel.add(usernameLabel, gbc);
    
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);
    
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        loginPanel.add(loginButton, gbc);
    
        gbc.gridx = 1;
        loginPanel.add(registerButton, gbc);
        // Login action
        loginButton.addActionListener(e -> performLogin(usernameField, passwordField));
    
        // Add enter key listener for login
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin(usernameField, passwordField);
                }
            }
        });
    
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin(usernameField, passwordField);
                }
            }
        });
    
        registerButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Register");
        });
    
        mainPanel.add(loginPanel, "Login");
    }

    private void performLogin(JTextField usernameField, JPasswordField passwordField) {
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
    }
    
    private void createRegisterPanel() {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new GridBagLayout());
        registerPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Increase field sizes
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(20);
        firstNameField.setPreferredSize(new Dimension(250, 35));
        
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(20);
        lastNameField.setPreferredSize(new Dimension(250, 35));
        
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(250, 35));
        
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(250, 35));
        
        JButton createButton = new JButton("Create Account");
        createButton.setPreferredSize(new Dimension(150, 40));
        
        JButton backButton = new JButton("Back to Login");
        backButton.setPreferredSize(new Dimension(150, 40));
        
        // Layout configuration
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        registerPanel.add(titleLabel, gbc);
        
        // Reset gridwidth to 1
        gbc.gridwidth = 1;
        
        // First Name
        gbc.gridy = 1;
        gbc.gridx = 0;
        registerPanel.add(firstNameLabel, gbc);
        
        gbc.gridx = 1;
        registerPanel.add(firstNameField, gbc);
        
        // Last Name
        gbc.gridy = 2;
        gbc.gridx = 0;
        registerPanel.add(lastNameLabel, gbc);
        
        gbc.gridx = 1;
        registerPanel.add(lastNameField, gbc);
        
        // Username
        gbc.gridy = 3;
        gbc.gridx = 0;
        registerPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        registerPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridy = 4;
        gbc.gridx = 0;
        registerPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        registerPanel.add(passwordField, gbc);
        
        // Buttons
        gbc.gridy = 5;
        gbc.gridx = 0;
        registerPanel.add(backButton, gbc);
        
        gbc.gridx = 1;
        registerPanel.add(createButton, gbc);
        
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
    
    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }
    
    private void createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout(10, 10));
    dashboardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setPreferredSize(new Dimension(700, 400));

    // User Info Panel
    JPanel userInfoPanel = new JPanel(new GridLayout(0, 1));
    Font accountDetailsFont = new Font("Arial", Font.BOLD, 20);

    userNameLabel = new JLabel("Welcome, Suraj Gharde");
    userNameLabel.setFont(accountDetailsFont);
    
    accountNumberLabel = new JLabel("Account #: 1000101");
    accountNumberLabel.setFont(accountDetailsFont);
    
    balanceLabel = new JLabel("Balance: $160000");
    balanceLabel.setFont(accountDetailsFont);

    userInfoPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder("Account Details"),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

  
        userNameLabel = new JLabel();
        accountNumberLabel = new JLabel();
        balanceLabel = new JLabel();
        userInfoPanel.add(userNameLabel);
        userInfoPanel.add(accountNumberLabel);
        userInfoPanel.add(balanceLabel);
    
        // Transaction History Tab
        JPanel historyPanel = new JPanel(new BorderLayout());
        transactionModel = new DefaultTableModel(
            new Object[] {"Type", "From", "To", "Amount", "Date"}, 0);
        JTable transactionTable = new JTable(transactionModel);
        transactionTable.setPreferredScrollableViewportSize(new Dimension(680, 350));
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Transfer Money Tab
        JPanel transferPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel recipientLabel = new JLabel("Recipient Username:");
        JTextField recipientField = new JTextField(20);
        recipientField.setPreferredSize(new Dimension(300, 35));
        
        JLabel amountLabel = new JLabel("Amount:");
        JTextField transferAmountField = new JTextField(20);
        transferAmountField.setPreferredSize(new Dimension(300, 35));
        
        JButton transferButton = new JButton("Transfer Money");
        transferButton.setPreferredSize(new Dimension(200, 40));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        transferPanel.add(recipientLabel, gbc);
        
        gbc.gridx = 1;
        transferPanel.add(recipientField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        transferPanel.add(amountLabel, gbc);
        
        gbc.gridx = 1;
        transferPanel.add(transferAmountField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        transferPanel.add(transferButton, gbc);
        
        // Deposit/Withdraw Tab
        JPanel depositWithdrawPanel = new JPanel(new GridBagLayout());
        
        JLabel actionLabel = new JLabel("Amount:");
        JTextField actionAmountField = new JTextField(20);
        actionAmountField.setPreferredSize(new Dimension(300, 35));
        
        JButton depositButton = new JButton("Deposit");
        depositButton.setPreferredSize(new Dimension(150, 40));
        
        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setPreferredSize(new Dimension(150, 40));
    
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        depositWithdrawPanel.add(actionLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        depositWithdrawPanel.add(actionAmountField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        depositWithdrawPanel.add(depositButton, gbc);
        
        gbc.gridx = 1;
        depositWithdrawPanel.add(withdrawButton, gbc);
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(200, 40));
        
        // Tabbed Pane - Remove duplicate addTab calls
        tabbedPane.addTab("Transaction History", historyPanel);
        tabbedPane.addTab("Transfer Money", transferPanel);
        tabbedPane.addTab("Deposit/Withdraw", depositWithdrawPanel);
        
        // Add everything to dashboard
        dashboardPanel.add(userInfoPanel, BorderLayout.NORTH);
        dashboardPanel.add(tabbedPane, BorderLayout.CENTER);
        dashboardPanel.add(logoutButton, BorderLayout.SOUTH);
        
        // Setup action listeners
        setupDashboardActionListeners(
            transferButton, depositButton, withdrawButton, 
            logoutButton, recipientField, transferAmountField, 
            actionAmountField);
        
        mainPanel.add(dashboardPanel, "Dashboard");
    }
    
    private void setupDashboardActionListeners(
        JButton transferButton, JButton depositButton, 
        JButton withdrawButton, JButton logoutButton,
        JTextField recipientField, JTextField transferAmountField, 
        JTextField actionAmountField) {
        
        transferButton.addActionListener(e -> {
            String recipient = recipientField.getText().trim();
            String amountStr = transferAmountField.getText().trim();
            
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
            String amountStr = actionAmountField.getText().trim();
            
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
            String amountStr = actionAmountField.getText().trim();
            
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
    }
    
    private void updateDashboard() {
        if (currentUser == null) return;
        
        // Update user info
        userNameLabel.setText("Welcome, " + currentUser.firstName + " " + currentUser.lastName);
        accountNumberLabel.setText("Account #: " + currentUser.accountNumber);
        balanceLabel.setText("Balance: $" + currentUser.balance);
        
        // Update transaction history
        transactionModel.setRowCount(0);
        List<Transaction> userTransactions = getTransactions(currentUser.username);
        for (Transaction t : userTransactions) {
            transactionModel.addRow(new Object[]{
                t.type, t.sender, t.receiver, t.amount, 
                t.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            });
        }
    }   // Update transaction history
       
    
 
    
    
    
    


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
 
 
