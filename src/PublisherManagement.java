import java.io.*;
import java.util.Scanner;

public class PublisherManagement {
    private static final String FILE_NAME = "publishers.txt";

    public static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n--- PUBLISHER MANAGEMENT ---");
            System.out.println("1. Add Publisher");
            System.out.println("2. View Publishers");
            System.out.println("3. Update Publisher");
            System.out.println("4. Delete Publisher");
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
                    addPublisher(scanner);
                    break;
                case 2:
                    viewPublishers();
                    break;
                case 3:
                    updatePublisher(scanner);
                    break;
                case 4:
                    deletePublisher(scanner);
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void addPublisher(Scanner scanner) {
        System.out.print("Enter Publisher ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Company Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(id + "," + name + "," + phone);
            writer.newLine();
            System.out.println("Publisher added successfully!");
        } catch (IOException e) {
            System.out.println("Error saving publisher: " + e.getMessage());
        }
    }

    private static void viewPublishers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println("\n--- PUBLISHER LIST ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    System.out.println("Publisher ID: " + data[0] + " | Company: " + data[1] + " | Phone: " + data[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading records: " + e.getMessage());
        }
    }

    private static void updatePublisher(Scanner scanner) {
        System.out.print("Enter Publisher ID to update: ");
        String targetId = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_publishers.txt");

        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(targetId)) {
                    System.out.print("Enter New Company Name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter New Phone: ");
                    String newPhone = scanner.nextLine();

                    writer.write(targetId + "," + newName + "," + newPhone);
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating publisher: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (updated) {
            System.out.println("Publisher updated successfully!");
        } else {
            System.out.println("Publisher ID not found.");
        }
    }

    private static void deletePublisher(Scanner scanner) {
        System.out.print("Enter Publisher ID to delete: ");
        String targetId = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_publishers.txt");

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
            System.out.println("Error deleting publisher: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (deleted) {
            System.out.println("Publisher deleted successfully!");
        } else {
            System.out.println("Publisher ID not found.");
        }
    }
}
