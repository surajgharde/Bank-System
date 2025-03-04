import java.io.*;
import java.util.*;

class UserData {
    String firstName, lastName, username, password;
    int balance;
    int accountNumber;

    public UserData(String firstName, String lastName, String username, String password, int balance, int accountNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.accountNumber = accountNumber;
    }
}

class Transaction {
    String sender, receiver;
    int amount;
    String type;

    public Transaction(String sender, String receiver, int amount, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.type = type;
    }
}

public class BankSystem {
    private static final String USERS_FILE = "users.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private Scanner sc = new Scanner(System.in);
    private List<UserData> users = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private int nextAccountNumber = 1000101;

    public BankSystem() {
        loadUsers();
        loadTransactions();
    }

    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            writer.write("AccountNumber|FirstName|LastName|Username|Password|Balance");
            writer.newLine();
            writer.write("----------------------------------------------------------");
            writer.newLine();

            for (UserData user : users) {
                writer.write(user.accountNumber + "|" + user.firstName + "|" + user.lastName + "|" + user.username + "|" + user.password + "|" + user.balance);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users.");
        }
    }

    private void saveTransactions() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            writer.write("Sender|Receiver|Amount|Type");
            writer.newLine();
            writer.write("-----------------------------");
            writer.newLine();

            for (Transaction transaction : transactions) {
                writer.write(transaction.sender + "|" + transaction.receiver + "|" + transaction.amount + "|" + transaction.type);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions.");
        }
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 6) continue;
                int accountNumber = Integer.parseInt(parts[0].trim());
                String firstName = parts[1].trim();
                String lastName = parts[2].trim();
                String username = parts[3].trim();
                String password = parts[4].trim();
                int balance = Integer.parseInt(parts[5].trim());
                users.add(new UserData(firstName, lastName, username, password, balance, accountNumber));

                if (accountNumber >= nextAccountNumber) {
                    nextAccountNumber = accountNumber + 1;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading user data.");
        }
    }

    private void loadTransactions() {
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 4) continue;
                String sender = parts[0].trim();
                String receiver = parts[1].trim();
                int amount = Integer.parseInt(parts[2].trim());
                String type = parts[3].trim();
                transactions.add(new Transaction(sender, receiver, amount, type));
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading transaction data.");
        }
    }

    private void createAccount() {
        System.out.print("Enter first name: ");
        String firstName = sc.next();
        System.out.print("Enter last name: ");
        String lastName = sc.next();
        System.out.print("Enter username: ");
        String username = sc.next();

        for (UserData user : users) {
            if (user.username.equals(username)) {
                System.out.println("Username already exists! Choose a different one.");
                return;
            }
        }

        System.out.print("Enter password: ");
        String password = sc.next();
        int balance = 120000;
        int accountNumber = nextAccountNumber++;
        users.add(new UserData(firstName, lastName, username, password, balance, accountNumber));
        saveUsers();
        System.out.println("Account created successfully! Your account number is: " + accountNumber);
    }

    private void login(String username, String password) {
        for (UserData user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                displayMenu(user);
                return;
            }
        }
        System.out.println("Invalid username or password.");
    }

    private void displayMenu(UserData currentUser) {
        while (true) {
            System.out.println("1. Check Balance");
            System.out.println("2. Credit Money");
            System.out.println("3. Debit Money");
            System.out.println("4. Transfer Money");
            System.out.println("5. View Transaction History");
            System.out.println("6. View User Details");
            System.out.println("7. Logout");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Your balance: " + currentUser.balance);
                    break;
                case 2:
                    creditMoney(currentUser);
                    break;
                case 3:
                    debitMoney(currentUser);
                    break;
                case 4:
                    transferMoney(currentUser);
                    break;
                case 5:
                    displayTransactionHistory(currentUser.username);
                    break;
                case 6:
                    displayUserDetails(currentUser);
                    break;
                case 7:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void creditMoney(UserData currentUser) {
        int amount = getValidAmount("Enter amount to credit: ");
        currentUser.balance += amount;
        transactions.add(new Transaction("Bank", currentUser.username, amount, "Credit"));
        saveTransactions();
        saveUsers(); // Save updated user data
        System.out.println("Amount credited successfully.");
    }

    private void debitMoney(UserData currentUser) {
        int amount = getValidAmount("Enter amount to debit: ");
        if (amount > currentUser.balance) {
            System.out.println("Insufficient balance.");
            return;
        }
        currentUser.balance -= amount;
        transactions.add(new Transaction(currentUser.username, "Bank", amount, "Debit"));
        saveTransactions();
        saveUsers(); // Save updated user data
        System.out.println("Amount debited successfully.");
    }

    private void transferMoney(UserData currentUser) {
        System.out.print("Enter recipient username: ");
        String recipientUsername = sc.next();
        UserData recipient = null;
        for (UserData user : users) {
            if (user.username.equals(recipientUsername)) {
                recipient = user;
                break;
            }
        }

        if (recipient == null) {
            System.out.println("Recipient not found.");
            return;
        }
        int amount = getValidAmount("Enter amount to transfer: ");
        if (amount > currentUser.balance) {
            System.out.println("Insufficient balance.");
            return;
        }
        currentUser.balance -= amount;
        recipient.balance += amount;
        transactions.add(new Transaction(currentUser.username, recipient.username, amount, "Transfer"));
        saveTransactions();
        saveUsers(); // Save updated user data
        System.out.println("Transfer of " + amount + " to " + recipientUsername + " successful.");
        System.out.println("Your new balance: " + currentUser.balance);
    }

    private void displayTransactionHistory(String username) {
        System.out.println("Transaction History:");
        for (Transaction transaction : transactions) {
            if (transaction.sender.equals(username) || transaction.receiver.equals(username)) {
                System.out.println(transaction.sender + " -> " + transaction.receiver + " : " + transaction.amount + " (" + transaction.type + ")");
            }
        }
    }

    private void displayUserDetails(UserData user) {
        System.out.println("User Details:");
        System.out.println("Account Number: " + user.accountNumber);
        System.out.println("First Name: " + user.firstName);
        System.out.println("Last Name: " + user.lastName);
        System.out.println("Username: " + user.username);
        System.out.println("Balance: " + user.balance);
    }

    private int getValidAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int amount = sc.nextInt();
                if (amount > 0) {
                    return amount;
                } else {
                    System.out.println("Amount must be positive.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); // Clear invalid input
            }
        }
    }

    public static void main(String[] args) {
        BankSystem bankSystem = new BankSystem();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    bankSystem.createAccount();
                    break;
                case 2:
                    System.out.print("Enter username: ");
                    String username = sc.next();
                    System.out.print("Enter password: ");
                    String password = sc.next();
                    bankSystem.login(username, password);
                    break;
                case 3:
                    bankSystem.saveUsers();
                    bankSystem.saveTransactions();
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}