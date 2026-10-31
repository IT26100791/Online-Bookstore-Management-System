import java.io.*;
import java.util.Scanner;

public class AuthorManagement {
    private static final String FILE_NAME = "authors.txt";

    public static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n--- AUTHOR MANAGEMENT ---");
            System.out.println("1. Add Author");
            System.out.println("2. View Authors");
            System.out.println("3. Update Author");
            System.out.println("4. Delete Author");
            System.out.println("0. Back to Main Menu");
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
                    addAuthor(scanner);
                    break;
                case 2:
                    viewAuthors();
                    break;
                case 3:
                    updateAuthor(scanner);
                    break;
                case 4:
                    deleteAuthor(scanner);
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void addAuthor(Scanner scanner) {
        System.out.print("Enter Author ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Author Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(id + "," + name + "," + email);
            writer.newLine();
            System.out.println("Author registered successfully!");
        } catch (IOException e) {
            System.out.println("Error saving author: " + e.getMessage());
        }
    }

    private static void viewAuthors() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println("\n--- AUTHOR DIRECTORY ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    System.out.println("Author ID: " + data[0] + " | Name: " + data[1] + " | Email: " + data[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading records: " + e.getMessage());
        }
    }

    private static void updateAuthor(Scanner scanner) {
        System.out.print("Enter Author ID to update: ");
        String targetId = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_authors.txt");

        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(targetId)) {
                    System.out.print("Enter New Name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter New Email: ");
                    String newEmail = scanner.nextLine();

                    writer.write(targetId + "," + newName + "," + newEmail);
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating author: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (updated) {
            System.out.println("Author updated successfully!");
        } else {
            System.out.println("Author ID not found.");
        }
    }

    private static void deleteAuthor(Scanner scanner) {
        System.out.print("Enter Author ID to delete: ");
        String targetId = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_authors.txt");

        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(targetId)) {
                    deleted = true;
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error deleting author: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (deleted) {
            System.out.println("Author record deleted successfully!");
        } else {
            System.out.println("Author ID not found.");
        }
    }
}