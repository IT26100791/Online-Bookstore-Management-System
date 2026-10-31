import java.io.*;
import java.util.Scanner;
public class ReviewManagement {

    private static final String FILE_NAME = "reviews.txt";

    public static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n--- REVIEW & RATING MANAGEMENT ---");
            System.out.println("1. Add Book Review");
            System.out.println("2. View All Reviews");
            System.out.println("3. Update Review");
            System.out.println("4. Delete Review");
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
                    addReview(scanner);
                    break;
                case 2:
                    viewReviews();
                    break;
                case 3:
                    updateReview(scanner);
                    break;
                case 4:
                    deleteReview(scanner);
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void addReview(Scanner scanner) {
        System.out.print("Enter Review ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Book Title: ");
        String book = scanner.nextLine();
        System.out.print("Enter Rating (1-5 Stars): ");
        String rating = scanner.nextLine();
        System.out.print("Enter Comment: ");
        String comment = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(id + "," + book + "," + rating + "," + comment);
            writer.newLine();
            System.out.println("Review added successfully!");
        } catch (IOException e) {
            System.out.println("Error saving review: " + e.getMessage());
        }
    }

    private static void viewReviews() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println("\n--- BOOK REVIEWS LOG ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    System.out.println("Review ID: " + data[0] + " | Book: " + data[1] + " | Rating: " + data[2] + "/5 | Comment: " + data[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading records: " + e.getMessage());
        }
    }

    private static void updateReview(Scanner scanner) {
        System.out.print("Enter Review ID to update: ");
        String targetId = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_reviews.txt");

        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(targetId)) {
                    System.out.print("Enter Book Title: ");
                    String newBook = scanner.nextLine();
                    System.out.print("Enter New Rating (1-5 Stars): ");
                    String newRating = scanner.nextLine();
                    System.out.print("Enter New Comment: ");
                    String newComment = scanner.nextLine();

                    writer.write(targetId + "," + newBook + "," + newRating + "," + newComment);
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating review: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (updated) {
            System.out.println("Review updated successfully!");
        } else {
            System.out.println("Review ID not found.");
        }
    }

    private static void deleteReview(Scanner scanner) {
        System.out.print("Enter Review ID to delete: ");
        String targetId = scanner.nextLine();

        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_reviews.txt");

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
            System.out.println("Error deleting review: " + e.getMessage());
        }

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }

        if (deleted) {
            System.out.println("Review deleted successfully!");
        } else {
            System.out.println("Review ID not found.");
        }
    }
}


