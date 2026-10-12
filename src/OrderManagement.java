import java.io.*;
import java.util.Scanner;

public class OrderManagement {
    private static final String FILE_NAME = "orders.txt";

    public static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n--- ORDER MANAGEMENT ---");
            System.out.println("1. Place New Order");
            System.out.println("2. View All Orders");
            System.out.println("3. Update Order Status");
            System.out.println("4. Cancel Order");
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
                    addOrder(scanner);
                    break;
                case 2:
                    viewOrders();
                    break;
                case 3:
                    updateOrder(scanner);
                    break;
                case 4:
                    deleteOrder(scanner);
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void addOrder(Scanner scanner) {
        System.out.print("Enter Order ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Book Title: ");
        String book = scanner.nextLine();
        System.out.print("Enter Status (Pending/Shipped/Delivered): ");
        String status = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(id + "," + name + "," + book + "," + status);
            writer.newLine();
            System.out.println("Order placed successfully!");
        } catch (IOException e) {
            System.out.println("Error saving order record: " + e.getMessage());
        }
    }

    private static void viewOrders() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println("\n--- ORDER LIST ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    System.out.println("Order ID: " + data[0] + " | Customer: " + data[1] + " | Book: " + data[2] + " | Status: " + data[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading records: " + e.getMessage());
        }
    }

    private static void updateOrder(Scanner scanner) {
        System.out.print("Enter Order ID to update: ");
        String targetId = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_orders.txt");

        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(targetId)) {
                    System.out.print("Enter Customer Name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter Book Title: ");
                    String newBook = scanner.nextLine();
                    System.out.print("Enter New Status: ");
                    String newStatus = scanner.nextLine();

                    writer.write(targetId + "," + newName + "," + newBook + "," + newStatus);
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating order: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (updated) {
            System.out.println("Order updated successfully!");
        } else {
            System.out.println("Order ID not found.");
        }
    }

    private static void deleteOrder(Scanner scanner) {
        System.out.print("Enter Order ID to cancel/delete: ");
        String targetId = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_orders.txt");

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
            System.out.println("Error deleting order: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (deleted) {
            System.out.println("Order deleted successfully!");
        } else {
            System.out.println("Order ID not found.");
        }
    }
}