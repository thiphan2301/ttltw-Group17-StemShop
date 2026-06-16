package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.config.ConnectionDB;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/admin-product-import")
public class AdminProductImportServlet extends HttpServlet {

    // HÀM 1: Bấm vào nút "Nhập hàng" thì Servlet CHUYỂN HƯỚNG SANG TRANG FORM
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String[]> products = new ArrayList<>();
        
        // Lấy danh sách sản phẩm từ DB để hiển thị trong thẻ <select>
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT ID, ProductName FROM products");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(new String[]{String.valueOf(rs.getInt("ID")), rs.getString("ProductName")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Đẩy dữ liệu qua trang mới
        request.setAttribute("products", products);
        
        // CHUYỂN HƯỚNG (FORWARD) SANG TRANG JSP MỚI
        request.getRequestDispatcher("/view/admin/admin-product-import.jsp").forward(request, response);
    }

    // HÀM 2: XỬ LÝ LƯU DỮ LIỆU KHI BẤM "XÁC NHẬN" TRÊN FORM MỚI
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        BigDecimal importPrice = new BigDecimal(request.getParameter("importPrice"));
        String lifespanStr = request.getParameter("lifespanMonths");
        String note = request.getParameter("note");

        Timestamp damagedDate = null;
        if (lifespanStr != null && !lifespanStr.trim().isEmpty()) {
            int months = Integer.parseInt(lifespanStr);
            damagedDate = Timestamp.valueOf(LocalDateTime.now().plusMonths(months));
        }

        Connection conn = null;
        try {
            conn = ConnectionDB.getConnection();
            conn.setAutoCommit(false); // Bật Transaction

            // Bước 2.1: Lưu vào lô hàng
            String sqlBatch = "INSERT INTO product_batches (ProductID, ImportPrice, InitialQuantity, UsableQuantity, ImportDate, BatchStatus, DamagedDate, Note) VALUES (?, ?, ?, ?, NOW(), 'AVAILABLE', ?, ?)";
            PreparedStatement psBatch = conn.prepareStatement(sqlBatch, Statement.RETURN_GENERATED_KEYS);
            psBatch.setInt(1, productId);
            psBatch.setBigDecimal(2, importPrice);
            psBatch.setInt(3, quantity);
            psBatch.setInt(4, quantity);
            psBatch.setTimestamp(5, damagedDate);
            psBatch.setString(6, note);
            psBatch.executeUpdate();

            ResultSet rsKeys = psBatch.getGeneratedKeys();
            int batchId = 0;
            if (rsKeys.next()) batchId = rsKeys.getInt(1);
            psBatch.close();

            // Bước 2.2: Lưu vào lịch sử
            String sqlHistory = "INSERT INTO import_history (ProductID, BatchID, Quantity, ImportPrice, ImportDate, Note) VALUES (?, ?, ?, ?, NOW(), ?)";
            PreparedStatement psHistory = conn.prepareStatement(sqlHistory);
            psHistory.setInt(1, productId);
            psHistory.setInt(2, batchId);
            psHistory.setInt(3, quantity);
            psHistory.setBigDecimal(4, importPrice);
            psHistory.setString(5, note);
            psHistory.executeUpdate();
            psHistory.close();

            // Bước 2.3: Cộng số lượng sản phẩm tổng
            String sqlProduct = "UPDATE products SET Quantity = Quantity + ? WHERE ID = ?";
            PreparedStatement psProduct = conn.prepareStatement(sqlProduct);
            psProduct.setInt(1, quantity);
            psProduct.setInt(2, productId);
            psProduct.executeUpdate();
            psProduct.close();

            conn.commit(); // Chốt sổ thành công

            // CHUYỂN HƯỚNG QUAY VỀ TRANG DANH SÁCH CHÍNH
            response.sendRedirect(request.getContextPath() + "/admin/admin-products?success=true");

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) { try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); } }
            response.sendRedirect(request.getContextPath() + "/admin/admin-products?error=true");
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) { e.printStackTrace(); } }
        }
    }
}