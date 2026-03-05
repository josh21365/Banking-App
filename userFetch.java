package com.example.banking_app;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.InputMismatchException;
import java.util.Scanner;
public class userFetch extends DBConnection{
		Scanner scanner = new Scanner(System.in);
		
	    void displayUsers() {
	    	String sql = "select * from users;";
	    	
	    	try(
	    	Connection conn = getConnection();
	    	PreparedStatement stmt = conn.prepareStatement(sql);
	    	ResultSet rs = stmt.executeQuery()){
	    		
	    	while (rs.next()) {
	    		int id = rs.getInt("id");
	    		String email = rs.getString("email");
	    		String password = rs.getString("password_hash");
	    		Timestamp createdAt = rs.getTimestamp("created_at");
	    			
	    			System.out.println("ALL USERS:");
	    			System.out.println("-".repeat(30));
	    			System.out.println("ID: " + id +
	    							"\nEmail: " + email +
	    							"\nPassword: " + password +
	    							"\nTime Added: " + createdAt);
	    			System.out.println("-".repeat(30));
	    		}
	    	}
	    	catch(SQLException e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    User signON(Scanner scanner) {
	            boolean userNameLoop = true;

	            while (userNameLoop) {
	                System.out.print("What is your email?: ");
	                String userEmail = scanner.nextLine();
	                System.out.println("-".repeat(30));

	                String sql = "SELECT * FROM users WHERE email = ?";
	                try (
	                    Connection connection = getConnection();
	                    PreparedStatement preparedStatement = connection.prepareStatement(sql)
	                ) {
	                    preparedStatement.setString(1, userEmail);

	                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                        if (resultSet.next()) {
	                            // Email found
	                            String email = resultSet.getString("email");
	                            String storedPassword = resultSet.getString("password_hash");
	                            int id = resultSet.getInt("id");

	                            System.out.println("✅ User found: " + email);

	                            // Now ask for password
	                            System.out.print("Enter your password: ");
	                            String enteredPassword = scanner.nextLine();

	                            if (enteredPassword.equals(storedPassword)) {
	                                System.out.println("✅ Login successful!");
	                                return new User(id,email);
	                            } else {
	                                System.out.println("❌ Wrong password, try again.");
	                            }

	                        } else {
	                            System.out.println("❌ Email not found!");
	                            System.out.println("-".repeat(30));
	                        }
	                    }
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
				return null;      
	    }
	    void deposit(Scanner scanner, User currentUser) {
	        double amountDeposit = 0;

	        while (true) {
	            try {
	                System.out.print("Enter the amount you want to deposit: ");
	                amountDeposit = scanner.nextDouble();
	                scanner.nextLine(); // consume newline

	                if (amountDeposit <= 0) {
	                    System.out.println("❌ Amount must be greater than 0.");
	                    continue;
	                }

	                double balance = getCurrentBalance(currentUser.getId());
	                if (balance < 0) balance = 0; // no previous transactions

	                double newBalance = balance + amountDeposit;

	                String insertSql = "INSERT INTO transactions (id, amount, type, balance, date) VALUES (?, ?, 'deposit', ?, CURRENT_TIMESTAMP)";
	                try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(insertSql)) {
	                    ps.setInt(1, currentUser.getId());
	                    ps.setDouble(2, amountDeposit);
	                    ps.setDouble(3, newBalance);
	                    ps.executeUpdate();
	                    System.out.println("✅ Deposit successful. New balance: $" + newBalance);
	                }
	                break; // exit loop after successful deposit

	            } catch (InputMismatchException e) {
	                System.out.println("❌ Invalid input. Enter a number.");
	                scanner.nextLine();
	            } catch (SQLException e) {
	                e.printStackTrace();
	                break;
	            }
	        }
	    }
	    void withdraw(Scanner scanner, User currentUser) {
	        double amountWithdraw = 0.0;

	        // Loop until valid number entered
	        while (true) {
	            try {
	                System.out.print("Enter the amount you want to withdraw: ");
	                amountWithdraw = scanner.nextDouble();
	                scanner.nextLine(); // consume newline

	                if (amountWithdraw <= 0.0) {
	                    System.out.println("❌ Amount must be greater than 0.");
	                    continue;
	                }

	                // Fetch current balance
	                double balance = 0.0;
	                String sql = "SELECT balance FROM transactions WHERE id = ? ORDER BY date DESC LIMIT 1";


	                try (Connection connection = getConnection();
	                     PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

	                    preparedStatement.setInt(1, currentUser.getId());
	                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                        if (resultSet.next()) {
	                            balance = resultSet.getDouble("balance");
	                        } else {
	                            System.out.println("❌ No balance found for this user.");
	                            return; // exit if user has no balance
	                        }
	                    }

	                    // Check if enough funds
	                    if (amountWithdraw > balance) {
	                        System.out.println("❌ Insufficient funds. Try again.");
	                        continue; // ask again
	                    }

	                    // Deduct amount and insert transaction
	                    double newBalance = balance - amountWithdraw;
	                    String insertTransaction = "INSERT INTO transactions (id, amount, type, date, balance) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)";
	                    try (PreparedStatement ps = connection.prepareStatement(insertTransaction)) {
	                        ps.setInt(1, currentUser.getId());
	                        ps.setDouble(2, amountWithdraw);
	                        ps.setString(3, "withdraw");
	                        ps.setDouble(4, newBalance);
	                        ps.executeUpdate();
	                    }

	                    System.out.println("✅ Withdrawal successful. New balance: $" + newBalance);
	                    break; // exit loop after successful withdrawal

	                } catch (SQLException e) {
	                    e.printStackTrace();
	                    break; // exit loop if SQL fails
	                }

	            } catch (InputMismatchException e) {
	                System.out.println("❌ Invalid input. Please enter a numeric value.");
	                scanner.nextLine(); // clear invalid input
	            }
	        }
	    }


	    void createUser() {
	    	boolean loopUser = true;
	    	
	    	while(loopUser) {
		    	System.out.print("Enter your new email: ");
		    	String createdEmail = scanner.nextLine();
		    	
		    	if(createdEmail.length() < 5 || createdEmail.length() > 50) {
		    		System.out.println("This email must be between 5-50 characters.");
		    		continue;
		    	}
		    	if(!createdEmail.contains("@")) {
		    		System.out.println("Your email must contain a '@'");
		    		continue;
		    	}
		    	if(!createdEmail.contains(".")) {
		    		System.out.println("Not a valid email");
		    		continue;
		    	}
		    	
		    	System.out.print("Enter a new password: ");
		    	String createdPassword = scanner.nextLine();
		    	
		    	if(createdPassword.length() < 8) {
		    		System.out.println("Your password is too weak.");
		    	}
		    	
		    	String sql = "insert into users (email,password_hash) values (?,?)";
		    	Connection connection = getConnection();
		    	try(PreparedStatement ps = connection.prepareStatement(sql)){
		    		ps.setString(1,createdEmail);
		    		ps.setString(2,createdPassword); // #TODO replace with hash password
		    		int rows = ps.executeUpdate();
		    		if(rows > 0) {
		    			System.out.println("User created!");
		    			break;
		    		}
		    		else {
		    			System.out.println("Failed to create user");
		    			continue;
		    		}
		    	}
		    	catch (SQLException e) {
		    		e.printStackTrace();
		    	}
	    	}
	    }
	    void showUserBalance(User currentUser) {
	        double balance = getCurrentBalance(currentUser.getId());
	        if (balance < 0) {
	            System.out.println("❌ No transactions found for this user.");
	        } else {
	            System.out.println("💰 Your current balance: $" + balance);
	        }
	    }
	    private double getCurrentBalance(int userId) {
	        String sql = "SELECT balance FROM transactions WHERE id = ? ORDER BY date DESC LIMIT 1";
	        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1, userId);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    return rs.getDouble("balance");
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return -1; // indicates no transactions
	    }
	}

	

