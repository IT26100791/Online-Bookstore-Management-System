package com.bookstore;

import com.bookstore.dao.AuthorDAO;
import com.bookstore.dao.BookDAO;
import com.bookstore.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BookModuleVerification {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("RUNNING BOOK MODULE VERIFICATION CHECKS");
        System.out.println("==================================================");

        try {
            testPrintedBookAndEBookPolymorphism();
            testBookAuthorObjectRelationship();
            testLegacyDataCompatibility();
            testBookDAOCRUDAndSearch();
            testBookDAOSafeMigration();
            testAuthorValidationLogic();

            System.out.println("==================================================");
            System.out.println("VERIFICATION SUMMARY: " + testsPassed + " / " + testsRun + " passed.");
            System.out.println("==================================================");

            if (testsPassed != testsRun) {
                System.exit(1);
            }
        } catch (Throwable t) {
            System.err.println("Verification suite encountered an exception: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static void assertTrue(String message, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + message);
        } else {
            System.err.println("  [FAIL] " + message);
            throw new AssertionError("Test assertion failed: " + message);
        }
    }

    private static void assertEquals(String message, Object expected, Object actual) {
        testsRun++;
        boolean eq = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        if (eq) {
            testsPassed++;
            System.out.println("  [PASS] " + message);
        } else {
            System.err.println("  [FAIL] " + message + " (Expected: " + expected + ", Actual: " + actual + ")");
            throw new AssertionError("Test assertion failed: " + message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void testPrintedBookAndEBookPolymorphism() {
        System.out.println("\nTesting PrintedBook and EBook polymorphism:");
        Author author = new Author("A001", "James Clear", "james@example.com", "International");

        Book printed = new PrintedBook("B001", "Atomic Habits", "Self-Help", "2500", "cover.jpg", author.getId(), "Desc", "Hardcover");
        Book ebook = new EBook("B002", "Digital Minimalism", "Self-Help", "1500", "", author.getId(), "Desc", "EPUB");

        assertEquals("PrintedBook getType()", "PRINTED", printed.getType());
        assertEquals("PrintedBook getTypeLabel()", "Printed book", printed.getTypeLabel());
        assertEquals("PrintedBook getFormatLabel()", "Binding", printed.getFormatLabel());
        assertEquals("PrintedBook getFormatValue()", "Hardcover", printed.getFormatValue());
        assertTrue("PrintedBook hasImage()", printed.hasImage());

        assertEquals("EBook getType()", "EBOOK", ebook.getType());
        assertEquals("EBook getTypeLabel()", "E-book", ebook.getTypeLabel());
        assertEquals("EBook getFormatLabel()", "File format", ebook.getFormatLabel());
        assertEquals("EBook getFormatValue()", "EPUB", ebook.getFormatValue());
        assertTrue("EBook delivery note contains download", ebook.getDeliveryNote().contains("download"));
    }

    private static void testBookAuthorObjectRelationship() {
        System.out.println("\nTesting Book-Author OOP relationship:");
        Author author = new Author("A001", "James Clear", "james@example.com", "International");
        Book book = new PrintedBook("B001", "Atomic Habits", "Self-Help", "2500", "cover.jpg", author.getId(), "Desc", "Hardcover");

        assertTrue("Book hasAuthor() is true", book.hasAuthor());
        assertEquals("Book author ID matches author.getId()", "A001", book.getAuthorId());
        assertEquals("Book getAuthor() returns resolved Author object", author, book.getAuthor());
        assertEquals("Book getAuthorName() returns author name", "James Clear", book.getAuthorName());
        assertEquals("Author email accessible via book.getAuthor()", "james@example.com", book.getAuthor().getEmail());
        assertEquals("Author type accessible via book.getAuthor()", "International", book.getAuthor().getAuthorType());
    }

    private static void testLegacyDataCompatibility() throws IOException {
        System.out.println("\nTesting backward-compatible loading of legacy data:");
        Path tempDir = Files.createTempDirectory("bookstore-test-legacy");
        try {
            // Create authors.txt with James Clear
            Path authorsFile = tempDir.resolve("authors.txt");
            Files.writeString(authorsFile, "A001,James Clear,james@example.com,International\nA002,Chandana Mendis,cm@example.com,Local\n");

            // Create books.txt with:
            // 1. 4-column legacy line (no author)
            // 2. 5-column legacy line (image, no author)
            // 3. 9-column line with Author ID
            // 4. 9-column line with legacy Author NAME that matches registered author
            // 5. 9-column line with legacy Author NAME that does NOT match registered author
            Path booksFile = tempDir.resolve("books.txt");
            String sampleData = "123,Old 4-Column Book,Fiction,1200\n"
                    + "ID01,Old 5-Column Book,Comic,1500,cover5.jpg\n"
                    + "B001,Book With ID,Self-Help,2000,cov1.jpg,A001,Description 1,PRINTED,Hardcover\n"
                    + "B002,Book With Name,Self-Help,2200,cov2.jpg,James Clear,Description 2,PRINTED,Paperback\n"
                    + "B003,Book With Unknown Name,History,1800,cov3.jpg,Ancient Historian,Description 3,EBOOK,PDF\n";
            Files.writeString(booksFile, sampleData);

            AuthorDAO authorDao = new AuthorDAO(tempDir.toString());
            BookDAO bookDao = new BookDAO(tempDir.toString(), authorDao);

            List<Book> loaded = bookDao.findAll();
            assertEquals("All 5 books loaded safely without discarding", 5, loaded.size());

            // 1. 4-column book
            Book b1 = bookDao.findById("123");
            assertTrue("4-column book exists", b1 != null);
            assertTrue("4-column book has unassigned/unresolved author", b1.isAuthorUnresolved());
            assertEquals("4-column book getAuthorName() is empty", "", b1.getAuthorName());

            // 2. 5-column book
            Book b2 = bookDao.findById("ID01");
            assertTrue("5-column book exists", b2 != null);
            assertEquals("5-column book image preserved", "cover5.jpg", b2.getImage());
            assertTrue("5-column book has unassigned author", b2.isAuthorUnresolved());

            // 3. 9-column book with author ID A001
            Book b3 = bookDao.findById("B001");
            assertTrue("Author ID book resolved author object", b3.getAuthor() != null);
            assertEquals("Author ID book matched James Clear", "James Clear", b3.getAuthor().getName());
            assertEquals("Author ID book authorId is A001", "A001", b3.getAuthorId());

            // 4. 9-column book with legacy author name 'James Clear'
            Book b4 = bookDao.findById("B002");
            assertTrue("Legacy author name resolved safely to Author object", b4.getAuthor() != null);
            assertEquals("Resolved author ID is A001", "A001", b4.getAuthorId());
            assertEquals("Resolved author name is James Clear", "James Clear", b4.getAuthorName());

            // 5. 9-column book with unmatched legacy author name 'Ancient Historian'
            Book b5 = bookDao.findById("B003");
            assertTrue("Unmatched author is NOT silently assigned uncertain author", b5.getAuthor() == null);
            assertTrue("Unmatched author flagged as isAuthorUnresolved", b5.isAuthorUnresolved());
            assertEquals("Legacy author name preserved", "Ancient Historian", b5.getLegacyAuthorName());
            assertEquals("getAuthorName returns preserved legacy name", "Ancient Historian", b5.getAuthorName());
        } finally {
            deleteRecursively(tempDir.toFile());
        }
    }

    private static void testBookDAOCRUDAndSearch() throws IOException {
        System.out.println("\nTesting BookDAO CRUD, search, and one-to-many author lookups:");
        Path tempDir = Files.createTempDirectory("bookstore-test-crud");
        try {
            Path authorsFile = tempDir.resolve("authors.txt");
            Files.writeString(authorsFile, "A001,James Clear,james@example.com,International\n"
                    + "A002,Chandana Mendis,cm@example.com,Local\n");

            AuthorDAO authorDao = new AuthorDAO(tempDir.toString());
            BookDAO bookDao = new BookDAO(tempDir.toString(), authorDao);

            Author a1 = authorDao.findById("A001");
            Author a2 = authorDao.findById("A002");

            // Add two books for author A001 and one for author A002
            Book book1 = BookFactory.create("PRINTED", "BK01", "Atomic Habits", "Self-Help", "2500", "cov1.jpg", a1.getId(), "Habits guide", "Hardcover");
            Book book2 = BookFactory.create("EBOOK", "BK02", "Clear Thinking", "Self-Help", "1800", "", a1.getId(), "Mindset", "EPUB");
            Book book3 = BookFactory.create("PRINTED", "BK03", "Sherlock Holmes Sinhala", "Mystery", "1600", "cov3.jpg", a2.getId(), "Sinhala translation", "Paperback");

            assertTrue("Add book1 succeeded", bookDao.add(book1));
            assertTrue("Add book2 succeeded", bookDao.add(book2));
            assertTrue("Add book3 succeeded", bookDao.add(book3));
            assertTrue("Duplicate ID add rejected", !bookDao.add(book1));

            // Verify one-to-many: one author has many books
            List<Book> a1Books = bookDao.findByAuthorId("A001");
            assertEquals("Author A001 has 2 books linked", 2, a1Books.size());

            List<Book> a2Books = bookDao.findByAuthorId("A002");
            assertEquals("Author A002 has 1 book linked", 1, a2Books.size());

            // Search by author name
            List<Book> searchByName = bookDao.search("James Clear");
            assertEquals("Search by author name found 2 books", 2, searchByName.size());

            // Search by genre
            List<Book> searchByGenre = bookDao.search("Mystery");
            assertEquals("Search by genre found 1 book", 1, searchByGenre.size());
            assertEquals("Mystery book is BK03", "BK03", searchByGenre.get(0).getId());

            // Update book
            Book updatedBook1 = BookFactory.create("PRINTED", "BK01", "Atomic Habits 2nd Edition", "Self-Help", "2700", "cov1_v2.jpg", a1.getId(), "Updated edition", "Hardcover");
            assertTrue("Update book1 succeeded", bookDao.update(updatedBook1));
            Book fetched = bookDao.findById("BK01");
            assertEquals("Title updated", "Atomic Habits 2nd Edition", fetched.getTitle());
            assertEquals("Price updated", "2700", fetched.getPrice());

            // Delete book
            assertTrue("Delete book3 succeeded", bookDao.delete("BK03"));
            assertTrue("Deleted book no longer found", bookDao.findById("BK03") == null);
            assertEquals("Total books now 2", 2, bookDao.findAll().size());
        } finally {
            deleteRecursively(tempDir.toFile());
        }
    }

    private static void testBookDAOSafeMigration() throws IOException {
        System.out.println("\nTesting BookDAO safe migration of legacy author names:");
        Path tempDir = Files.createTempDirectory("bookstore-test-migration");
        try {
            Path authorsFile = tempDir.resolve("authors.txt");
            Files.writeString(authorsFile, "A001,James Clear,james@example.com,International\n");

            Path booksFile = tempDir.resolve("books.txt");
            // BK01 has legacy author name "James Clear" (should migrate to A001)
            // BK02 has unknown author "Mystery Author" (should NOT migrate, stay preserved)
            Files.writeString(booksFile,
                    "BK01,Habits,Genre,1000,,James Clear,Desc,PRINTED,Paperback\n"
                            + "BK02,Mystery,Genre,1200,,Mystery Author,Desc,PRINTED,Paperback\n");

            AuthorDAO authorDao = new AuthorDAO(tempDir.toString());
            BookDAO bookDao = new BookDAO(tempDir.toString(), authorDao);

            int migrated = bookDao.migrateLegacyAuthorRecords();
            assertEquals("1 record migrated safely", 1, migrated);

            // Re-read file lines directly from disk to verify disk contents
            List<String> lines = Files.readAllLines(booksFile);
            assertTrue("BK01 persisted with A001 on disk", lines.get(0).contains(",A001,"));
            assertTrue("BK02 preserved Mystery Author on disk", lines.get(1).contains(",Mystery+Author,") || lines.get(1).contains(",Mystery Author,"));
        } finally {
            deleteRecursively(tempDir.toFile());
        }
    }

    private static void testAuthorValidationLogic() throws IOException {
        System.out.println("\nTesting Author validation rules for saving books:");
        Path tempDir = Files.createTempDirectory("bookstore-test-validation");
        try {
            Path authorsFile = tempDir.resolve("authors.txt");
            Files.writeString(authorsFile, "A001,James Clear,james@example.com,International\n");
            AuthorDAO authorDao = new AuthorDAO(tempDir.toString());

            Author existing = authorDao.findById("A001");
            assertTrue("A001 exists", existing != null);

            Author nonExistent = authorDao.findById("NON_EXISTENT_ID");
            assertTrue("NON_EXISTENT_ID does not exist", nonExistent == null);

            Author empty = authorDao.findById("");
            assertTrue("Empty author ID returns null", empty == null);
        } finally {
            deleteRecursively(tempDir.toFile());
        }
    }

    private static void deleteRecursively(File file) {
        if (file == null || !file.exists()) return;
        File[] children = file.listFiles();
        if (children != null) {
            for (File c : children) {
                deleteRecursively(c);
            }
        }
        file.delete();
    }
}