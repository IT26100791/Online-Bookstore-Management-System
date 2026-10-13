import java.util.Scanner;
<<<<<<< HEAD

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int mainChoice = -1;

        while (mainChoice != 0) {
            System.out.println("\n=========================================");
            System.out.println("       SYSTEM MAIN MENU                 ");
            System.out.println("=========================================");
            System.out.println("1. Book Inventory Management");
            System.out.println("0. Exit Application");
            System.out.print("Enter Choice: ");

            if (scanner.hasNextInt()) {
                mainChoice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear invalid token
                continue;
            }

            switch (mainChoice) {
                case 1:
                    // Delegation to the BookManagement class
                    BookManagement.showMenu();
                    break;
                case 0:
                    System.out.println("Exiting system. Goodbye!");
=======
// updagted the main menu
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n==================================");
            System.out.println("   BOOKSTORE MANAGEMENT SYSTEM   ");
            System.out.println("==================================");
            System.out.println("1. Author Management");
            System.out.println("2. Manage Orders");
            System.out.println("3. User Management");
            System.out.println("4. Publisher Management");
            System.out.println("0. Exit System");
            System.out.print("Enter Choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear the newline buffer
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    AuthorManagement.showMenu();
                    break;
                case 2:
                    OrderManagement.showMenu();
                    break;

                case 3:
                    UserManagement.showMenu();
                    break;

                case 4:
                    PublisherManagement.showMenu();
                    break;
                case 0:
                    System.out.println("Exiting System. Goodbye!");
>>>>>>> 2a1a40fcff835972fafa01e38f1c391990ad59c6
                    break;
                default:
                    System.out.println("Invalid choice! Please select a valid option.");
            }
        }

        scanner.close();
    }
}