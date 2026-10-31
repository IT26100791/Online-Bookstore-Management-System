import java.io.*;
import java.util.Scanner;

public class UserManagement {
    private static final String FILE_NAME = "users.txt";

    public static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n--- USER MANAGEMENT ---");
            System.out.println("1. Register User");//option 1
            System.out.println("2. View All Users");// option 2
            System.out.println("3. Update User");//option 3
            System.out.println("4. Delete User");//option 4
            System.out.println("0. Back to Main Menu");//option 0
            System.out.print("Enter Choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Invalid input!");
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    addUser(scanner);
                    break;
                case 2:
                    viewUsers();
                    break;
                case 3:
                    updateUser(scanner);
                    break;
                case 4:
                    deleteUser(scanner);
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void addUser(Scanner scanner) {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Role (Admin/Customer): ");
        String role = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(username + "," + password + "," + role);
            writer.newLine();
            System.out.println("User registered successfully!");
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    private static void viewUsers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println("\n--- USER DIRECTORY ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    System.out.println("Username: " + data[0] + " | Role: " + data[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading records: " + e.getMessage());
        }
    }

    private static void updateUser(Scanner scanner) {
        System.out.print("Enter Username to update: ");
        String targetUser = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_users.txt");

        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(targetUser)) {
                    System.out.print("Enter New Password: ");
                    String newPass = scanner.nextLine();
                    System.out.print("Enter New Role: ");
                    String newRole = scanner.nextLine();

                    writer.write(targetUser + "," + newPass + "," + newRole);
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (updated) {
            System.out.println("User updated successfully!");
        } else {
            System.out.println("Username not found.");
        }
    }

    private static void deleteUser(Scanner scanner) {
        System.out.print("Enter Username to delete: ");
        String targetUser = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_users.txt");

        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(targetUser)) {
                    deleted = true;
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (deleted) {
            System.out.println("User deleted successfully!");
        } else {
            System.out.println("Username not found.");
        }
    }
}
