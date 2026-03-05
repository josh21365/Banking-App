package com.example.banking_app;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

		public static void main(String[] args) {
			Scanner scanner = new Scanner(System.in);
			userFetch fetchData = new userFetch();
			User loggedInUser = null;
			
			mainMenu(scanner, fetchData, loggedInUser);
		
			scanner.close();
			
		}



		static void mainMenu(Scanner scanner, userFetch fetchData , User loggedInUser) {
			int choice;
			boolean mainMenu = true;
			while(mainMenu) {
				try {
				System.out.println("-----Main Menu-----\n");
				System.out.println("1. Log In");
				System.out.println("2. Create an Account");
				System.out.println("3. Exit\n");
				System.out.println("-".repeat(30));
				System.out.print("Enter a number for your choice: ");
				choice = scanner.nextInt();
				scanner.nextLine();
				
				switch(choice) {
					case 1: {
						loggedInUser = fetchData.signON(scanner);
						if(loggedInUser != null) {
							logInMenu(scanner, fetchData,loggedInUser);
						}
						break;
					}
					case 2: {
						fetchData.createUser();
						break;
					}
					case 3: {
						System.out.println("Closing Application...");
						mainMenu = false;
						break;
					}
					default: System.out.println("Invalid choice, try again\n");
				}
				
				}
				catch (InputMismatchException e){
					System.out.println("Invalid input, enter an integer.\n");
					scanner.nextLine();
				}
			}
		}
		static void logInMenu(Scanner scanner, userFetch fetchData, User loggedInUser) {
			boolean userLoop = true;
			
			while(userLoop) {
			try{
				System.out.println("\n" + "-".repeat(30));
				System.out.println("1. Withdraw");
				System.out.println("2. Deposit");
				System.out.println("3. Show Balance");
				System.out.println("4. Exit to Main Menu");
				System.out.print("Enter a number here: ");
				int userChoice = scanner.nextInt();
				scanner.nextLine();
				System.out.println("\n" + "-".repeat(30));
				
				switch(userChoice) {
				case 1: {

					fetchData.withdraw(scanner, loggedInUser);
					break;
				}
					
				case 2:{
					fetchData.deposit(scanner,loggedInUser);
					break;
				}
				case 3:{
					fetchData.showUserBalance(loggedInUser);
					break;
				}
				case 4:{
					System.out.println("Returning to Main Menu...");
					userLoop = false;
					break;
				}
				default : {
					System.out.println("Invalid choice try again");
					scanner.nextLine();
					
				}
				
				}
			}
			catch(InputMismatchException e) {
				System.out.println("Invalid input, enter an integer\n");
				scanner.nextLine();
			}
			
			}
		}
}
