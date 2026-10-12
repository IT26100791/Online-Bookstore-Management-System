import java.util.Scanner;

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
                    break;
                default:
                    System.out.println("Invalid choice! Please select a valid option.");
            }
        }

        scanner.close();
    }
}