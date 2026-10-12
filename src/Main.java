import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n==============================");
            System.out.println("   LIBRARY MANAGEMENT SYSTEM  ");
            System.out.println("==============================");
            System.out.println("1. Author Management");
            // Add other modules here as you build them:
            // System.out.println("2. Book Management");
            System.out.println("\n==================================");
            System.out.println("   BOOKSTORE MANAGEMENT SYSTEM   ");
            System.out.println("==================================");
            System.out.println("1. Manage Orders");
            System.out.println("0. Exit System");
            System.out.print("Enter Choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear the newline buffer
                scanner.nextLine(); // Clear newline character
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    AuthorManagement.showMenu();
                    OrderManagement.showMenu();
                    break;
                case 0:
                    System.out.println("Exiting application. Goodbye!");
                    System.out.println("Exiting System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice! Please select an option from the menu.");
                    System.out.println("Invalid choice! Please select a valid option.");
            }
        }

        scanner.close();
    }
}