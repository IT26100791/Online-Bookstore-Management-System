import java.io.*;
import java.util.Scanner;

public class BookManagement {
    private static final String FILE_NAME = "books.txt";

    public static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n--- BOOK INVENTORY MANAGEMENT ---");
            System.out.println("1. Add New Book");
            System.out.println("2. View All Books");
            System.out.println("3. Update Book Details");
            System.out.println("4. Delete Book");
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
                    addBook(scanner);
                    break;
                case 2:
                    viewBooks();
                    break;
                case 3:
                    updateBook(scanner);
                    break;
                case 4:
                    deleteBook(scanner);
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void addBook(Scanner scanner) {
        System.out.print("Enter Book ISBN/ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Genre: ");
        String genre = scanner.nextLine();
        System.out.print("Enter Price: ");
        String price = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(id + "," + title + "," + genre + "," + price);
            writer.newLine();
            System.out.println("Book added successfully!");
        } catch (IOException e) {
            System.out.println("Error saving book record: " + e.getMessage());
        }
    }

    private static void viewBooks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println("\n--- BOOK CATALOG ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    System.out.println("ISBN: " + data[0] + " | Title: " + data[1] + " | Genre: " + data[2] + " | Price: $" + data[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading records: " + e.getMessage());
        }
    }

    private static void updateBook(Scanner scanner) {
        System.out.print("Enter Book ISBN to update: ");
        String targetId = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_books.txt");

        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(targetId)) {
                    System.out.print("Enter New Title: ");
                    String newTitle = scanner.nextLine();
                    System.out.print("Enter New Genre: ");
                    String newGenre = scanner.nextLine();
                    System.out.print("Enter New Price: ");
                    String newPrice = scanner.nextLine();

                    writer.write(targetId + "," + newTitle + "," + newGenre + "," + newPrice);
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error during update: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (updated) {
            System.out.println("Book updated successfully!");
        } else {
            System.out.println("Book ISBN not found.");
        }
    }

    private static void deleteBook(Scanner scanner) {
        System.out.print("Enter Book ISBN to delete: ");
        String targetId = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_books.txt");

        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(targetId)) {
                    deleted = true;
                    continue; // Skip writing to delete
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error during deletion: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (deleted) {
            System.out.println("Book record deleted successfully!");
        } else {
            System.out.println("Book ISBN not found.");
        }
    }
}