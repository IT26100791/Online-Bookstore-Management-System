package com.bookstore.servlet;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.model.Book;
import com.bookstore.model.EbookOrder;
import com.bookstore.model.Order;
import com.bookstore.model.PhysicalOrder;
import com.bookstore.model.User;
import com.bookstore.util.DataPaths;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/order")
@MultipartConfig(fileSizeThreshold = 256 * 1024, maxFileSize = OrderServlet.MAX_RECEIPT_BYTES,
        maxRequestSize = OrderServlet.MAX_RECEIPT_BYTES + 100 * 1024)
public class OrderServlet extends HttpServlet {

    static final int MAX_RECEIPT_BYTES = 2 * 1024 * 1024;     // 2 MB
    private static final Set<String> RECEIPT_TYPES = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "pdf"));

    private OrderDAO dao;
    private BookDAO bookDao;
    private Path receiptDir;     // outside the web folder, so receipts are never public URLs

    @Override
    public void init() {
        dao = new OrderDAO();
        bookDao = new BookDAO();
        receiptDir = Paths.get(DataPaths.dataDir(), "receipts").toAbsolutePath().normalize();
        try {
            Files.createDirectories(receiptDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot prepare receipts folder: " + e.getMessage(), e);
        }
    }

    private User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        Object o = (s == null) ? null : s.getAttribute("loggedUser");
        return (o instanceof User) ? (User) o : null;
    }

    private boolean isAdmin(User u) {
        return u != null && "Admin".equalsIgnoreCase(u.getRole());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=Please+login+first");
            return;
        }
        boolean admin = isAdmin(user);

        String action = req.getParameter("action");
        if (action == null) action = "list";
        req.setAttribute("msg", req.getParameter("msg"));

        switch (action) {
            case "new":
                req.setAttribute("books", bookDao.findAll());      // catalog for the book dropdown
                req.getRequestDispatcher("/order-form.jsp").forward(req, resp);
                break;
            case "edit": {
                if (!admin) {                       // customers cannot edit orders
                    resp.sendRedirect("order?msg=Access+denied");
                    return;
                }
                Order o = dao.findById(req.getParameter("id"));
                if (o == null) {
                    resp.sendRedirect("order?msg=Order+not+found");
                    return;
                }
                req.setAttribute("order", o);
                req.setAttribute("books", bookDao.findAll());
                req.getRequestDispatcher("/order-form.jsp").forward(req, resp);
                break;
            }
            case "view": {                          // order details / invoice page
                Order o = dao.findById(req.getParameter("id"));
                if (o == null) {
                    resp.sendRedirect("order?msg=Order+not+found");
                    return;
                }
                if (!admin && !o.getCustomerName().equalsIgnoreCase(user.getUsername())) {
                    resp.sendRedirect("order?msg=Access+denied");   // customers see only their own
                    return;
                }
                req.setAttribute("order", o);
                req.getRequestDispatcher("/order-details.jsp").forward(req, resp);
                break;
            }
            case "receipt": {                       // streams the receipt to the admin or the order owner
                Order o = dao.findById(req.getParameter("id"));
                boolean allowed = o != null && o.hasReceipt()
                        && (admin || o.getCustomerName().equalsIgnoreCase(user.getUsername()));
                Path file = allowed ? receiptDir.resolve(o.getReceiptFile()).normalize() : null;
                if (file == null || !file.startsWith(receiptDir) || !Files.isRegularFile(file)) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                String name = file.getFileName().toString().toLowerCase();
                resp.setContentType(name.endsWith(".pdf") ? "application/pdf"
                        : name.endsWith(".png") ? "image/png" : "image/jpeg");
                resp.setHeader("X-Content-Type-Options", "nosniff");
                resp.setHeader("Content-Disposition", "inline; filename=\"receipt-" + name + "\"");
                resp.setContentLength((int) Files.size(file));
                Files.copy(file, resp.getOutputStream());
                return;
            }
            default: {
                List<Order> all = dao.findAll();
                if (!admin) {                       // customers see only their own orders
                    List<Order> mine = new ArrayList<>();
                    for (Order x : all) {
                        if (x.getCustomerName().equalsIgnoreCase(user.getUsername())) mine.add(x);
                    }
                    all = mine;
                }
                req.setAttribute("orders", all);
                req.getRequestDispatcher("/order-list.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        User user = currentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=Please+login+first");
            return;
        }
        if (req.getContentLength() > MAX_RECEIPT_BYTES + 100 * 1024) {
            resp.sendRedirect("order?msg=Receipt+is+too+large+(max+2+MB)");
            return;
        }
        boolean admin = isAdmin(user);

        String action = req.getParameter("action");
        String id = trim(req.getParameter("id"));
        String customerName = trim(req.getParameter("customerName"));
        String bookTitle = trim(req.getParameter("bookTitle"));
        String status = trim(req.getParameter("status"));
        String orderType = trim(req.getParameter("orderType"));
        String extra = trim(req.getParameter("extra"));
        String quantityText = trim(req.getParameter("quantity"));
        String priceText = trim(req.getParameter("unitPrice"));
        String paymentMethod = trim(req.getParameter("paymentMethod")).toUpperCase();

        switch (action == null ? "" : action) {
            case "add":
            case "update": {
                boolean isUpdate = "update".equals(action);

                if (isUpdate && !admin) {           // customers can only place orders
                    resp.sendRedirect("order?msg=Access+denied");
                    return;
                }
                if (!admin) {
                    // customer: name, status and id come from the server, not from the form
                    customerName = user.getUsername();
                    status = "Pending";
                    id = "ORD" + System.currentTimeMillis();
                    priceText = "";                 // price is set by the server for customers
                }

                String back = isUpdate ? "order?action=edit&id=" + enc(id) : "order?action=new";

                if (id.isEmpty() || customerName.isEmpty() || bookTitle.isEmpty()
                        || status.isEmpty() || extra.isEmpty()) {
                    resp.sendRedirect(back + "&msg=All+fields+are+required");
                    return;
                }
                // The chosen catalog book decides the order type and the price (the browser is not trusted)
                Book book = findBook(bookTitle);
                if (book != null) {
                    orderType = "EBOOK".equals(book.getType()) ? EbookOrder.TYPE_CODE : PhysicalOrder.TYPE_CODE;
                } else if (!admin) {
                    resp.sendRedirect(back + "&msg=Please+choose+a+book+from+the+catalog");
                    return;
                }
                if (!Order.isValidType(orderType)) {
                    resp.sendRedirect(back + "&msg=Please+choose+an+order+type");
                    return;
                }
                if (!Order.isValidPayment(paymentMethod)) {
                    resp.sendRedirect(back + "&msg=Please+choose+a+payment+method");
                    return;
                }
                if (hasComma(id, customerName, bookTitle, status, extra)) {
                    resp.sendRedirect(back + "&msg=Commas+are+not+allowed");
                    return;
                }

                int quantity;
                double unitPrice;
                try {
                    quantity = Integer.parseInt(quantityText);
                } catch (NumberFormatException e) {
                    resp.sendRedirect(back + "&msg=Quantity+must+be+a+whole+number");
                    return;
                }
                try {
                    unitPrice = !priceText.isEmpty() ? Double.parseDouble(priceText)
                            : book != null ? priceOf(book) : Order.defaultPrice(orderType);
                } catch (NumberFormatException e) {
                    resp.sendRedirect(back + "&msg=Unit+price+must+be+a+number");
                    return;
                }

                // ---- Bank deposit: decide which receipt file the order will point to ----
                Order existing = isUpdate ? dao.findById(id) : null;
                String oldReceipt = (existing == null) ? Order.NO_RECEIPT : existing.getReceiptFile();
                String receiptName = Order.NO_RECEIPT;
                Part upload = null;
                boolean hasNewUpload = false;

                if (Order.PAY_BANK.equals(paymentMethod)) {
                    try {
                        upload = req.getPart("receipt");
                    } catch (IllegalStateException e) {
                        resp.sendRedirect(back + "&msg=Receipt+is+too+large+(max+2+MB)");
                        return;
                    }
                    hasNewUpload = upload != null && upload.getSize() > 0;
                    if (hasNewUpload) {
                        String ext = extensionOf(upload.getSubmittedFileName());
                        if (!RECEIPT_TYPES.contains(ext) || !looksLikeReceipt(upload, ext)) {
                            resp.sendRedirect(back + "&msg=Receipt+must+be+a+JPG,+PNG+or+PDF+file");
                            return;
                        }
                        receiptName = safeName(id) + "_" + System.currentTimeMillis() + "." + ext;
                    } else if (!Order.NO_RECEIPT.equals(oldReceipt)) {
                        receiptName = oldReceipt;           // keep the receipt that is already saved
                    } else {
                        resp.sendRedirect(back + "&msg=Please+upload+your+bank+deposit+receipt");
                        return;
                    }
                }

                Order order;
                try {
                    // Servlet only talks to the abstract Order; Order.create picks the subclass
                    order = Order.create(orderType, id, customerName, bookTitle,
                            status, quantity, unitPrice, extra, paymentMethod, receiptName);
                } catch (IllegalArgumentException e) {
                    resp.sendRedirect(back + "&msg=" + enc(e.getMessage()));
                    return;
                }

                if (hasNewUpload) {
                    try (InputStream in = upload.getInputStream()) {
                        Files.copy(in, receiptDir.resolve(receiptName), StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                if (isUpdate) {
                    boolean ok = dao.update(order);
                    if (ok && !oldReceipt.equals(receiptName)) deleteReceipt(oldReceipt);
                    if (!ok && hasNewUpload) deleteReceipt(receiptName);
                    resp.sendRedirect("order?msg=" + (ok ? "Order+updated" : "Order+not+found"));
                } else {
                    boolean ok = dao.add(order);
                    if (!ok && hasNewUpload) deleteReceipt(receiptName);
                    resp.sendRedirect(ok ? "order?msg=Order+placed" : back + "&msg=Order+ID+already+exists");
                }
                break;
            }
            case "delete": {
                if (!admin) {
                    resp.sendRedirect("order?msg=Access+denied");
                    return;
                }
                Order existing = dao.findById(id);
                boolean deleted = dao.delete(id);
                if (deleted && existing != null) deleteReceipt(existing.getReceiptFile());
                resp.sendRedirect("order?msg=" + (deleted ? "Order+deleted" : "Order+not+found"));
                break;
            }
            default:
                resp.sendRedirect("order");
        }
    }

    // ---------- book helpers ----------

    private Book findBook(String title) throws IOException {
        for (Book b : bookDao.findAll()) {
            if (b.getTitle().equalsIgnoreCase(title)) return b;
        }
        return null;
    }

    private double priceOf(Book book) {
        return Double.parseDouble(book.getPrice().replaceAll("[^0-9.]", ""));
    }

    // ---------- receipt helpers ----------

    private String extensionOf(String fileName) {
        if (fileName == null) return "";
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase();
    }

    private String safeName(String s) {
        return s.replaceAll("[^A-Za-z0-9_-]", "_");
    }

    /** Checks the first bytes of the upload, so a renamed .exe cannot pass as a .jpg */
    private boolean looksLikeReceipt(Part part, String ext) throws IOException {
        byte[] b = new byte[4];
        try (InputStream in = part.getInputStream()) {
            if (in.read(b) < 4) return false;
        }
        boolean jpg = (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
        boolean png = (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G';
        boolean pdf = b[0] == '%' && b[1] == 'P' && b[2] == 'D' && b[3] == 'F';
        switch (ext) {
            case "jpg":
            case "jpeg": return jpg;
            case "png":  return png;
            default:     return pdf;
        }
    }

    private void deleteReceipt(String fileName) {
        if (fileName == null || Order.NO_RECEIPT.equals(fileName)) return;
        try {
            Path p = receiptDir.resolve(fileName).normalize();
            if (p.startsWith(receiptDir)) Files.deleteIfExists(p);
        } catch (IOException ignored) {
            // a leftover file is harmless; the order itself is already saved
        }
    }

    private boolean hasComma(String... values) {
        for (String v : values) {
            if (v.contains(",")) return true;
        }
        return false;
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String enc(String s) throws IOException {
        return URLEncoder.encode(s, "UTF-8");
    }
}
