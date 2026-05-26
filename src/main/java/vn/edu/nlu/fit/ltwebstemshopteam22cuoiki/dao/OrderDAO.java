package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao;

import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.config.ConnectionDB;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.Order;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // 1. Tạo đơn hàng mới
    public int insert(Order order) throws Exception {
        String sql = "INSERT INTO orders (UserID, TotalAmount, ShippingFee, Note, ShippingAddress, ReceiverName, ReceiverPhone, OrderStatus, payment_method_id, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, order.getUserId());
            stmt.setDouble(2, order.getTotalAmount());
            stmt.setDouble(3, order.getShippingFee());
            stmt.setString(4, order.getNote());
            stmt.setString(5, order.getShippingAddress());
            stmt.setString(6, order.getReceiverName());
            stmt.setString(7, order.getReceiverPhone());
            stmt.setString(8, order.getOrderStatus());
            stmt.setInt(9, order.getPaymentMethodId());
            stmt.setString(10, order.getPaymentStatus());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        }
    }

    // 2. Lấy tổng số lượng đơn hàng (Dùng cho Admin Dashboard)
    public int countOrders() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 3. Tính tổng doanh thu (Dùng cho Admin Dashboard)
    public double getTotalRevenue() {
        // Lưu ý: Đã đổi thành 'DELIVERED' thay vì 'CONFIRMED' để tính doanh thu thực tế chính xác hơn
        String sql = "SELECT SUM(TotalAmount) FROM orders WHERE OrderStatus = 'DELIVERED'";
        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 4. Lấy danh sách toàn bộ đơn hàng (Dùng cho Admin Quản lý)
    public List<Order> getAllOrdersForAdmin() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.FullName, u.PhoneNumber " +
                "FROM orders o " +
                "JOIN users u ON o.UserID = u.ID " +
                "ORDER BY o.OrderDate DESC";

        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("ID"));
                o.setUserId(rs.getInt("UserID"));
                o.setOrderDate(rs.getTimestamp("OrderDate"));
                o.setOrderStatus(rs.getString("OrderStatus"));
                o.setShippingFee(rs.getDouble("ShippingFee"));
                o.setTotalAmount(rs.getDouble("TotalAmount"));
                o.setShippingAddress(rs.getString("ShippingAddress"));
                o.setReceiverName(rs.getString("ReceiverName"));
                o.setReceiverPhone(rs.getString("ReceiverPhone"));
                o.setUserFullName(rs.getString("FullName"));
                o.setUserPhone(rs.getString("PhoneNumber"));
                list.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 5. Cập nhật trạng thái đơn hàng
    public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET OrderStatus=? WHERE ID=?";
        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 6. Lấy CHI TIẾT 1 đơn hàng kèm danh sách sản phẩm (Dùng cho Popup Order Details)
    public Order getOrderById(int orderId) throws Exception {
        Order order = null;
        String sqlOrder = "SELECT * FROM orders WHERE ID = ?";
        String sqlItems = "SELECT od.ProductID, p.ProductName, pi.ImageURL, od.Quantity, od.Price " +
                "FROM order_detail od " +
                "JOIN products p ON od.ProductID = p.ID " +
                "LEFT JOIN product_image pi ON p.ID = pi.ProductID " +
                "WHERE od.OrderID = ? " +
                "GROUP BY p.ID";

        try (Connection conn = ConnectionDB.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        order = new Order();
                        order.setId(rs.getInt("ID"));
                        order.setUserId(rs.getInt("UserID"));
                        int promoId = rs.getInt("PromotionID");
                        order.setPromotionId(rs.wasNull() ? null : promoId);
                        order.setOrderDate(rs.getTimestamp("OrderDate"));
                        order.setOrderStatus(rs.getString("OrderStatus"));
                        order.setShippingFee(rs.getDouble("ShippingFee"));
                        order.setTotalAmount(rs.getDouble("TotalAmount"));
                        order.setNote(rs.getString("Note"));
                        order.setShippingAddress(rs.getString("ShippingAddress"));
                        order.setReceiverName(rs.getString("ReceiverName"));
                        order.setReceiverPhone(rs.getString("ReceiverPhone"));
                    }
                }
            }

            if (order != null) {
                List<OrderItem> items = new ArrayList<>();
                try (PreparedStatement psItem = conn.prepareStatement(sqlItems)) {
                    psItem.setInt(1, orderId);
                    try (ResultSet rsItem = psItem.executeQuery()) {
                        while (rsItem.next()) {
                            OrderItem item = new OrderItem(
                                    rsItem.getInt("ProductID"),
                                    rsItem.getString("ProductName"),
                                    rsItem.getString("ImageURL"),
                                    rsItem.getInt("Quantity"),
                                    rsItem.getDouble("Price")
                            );
                            items.add(item);
                        }
                    }
                }
                order.setItems(items);
            }
        }
        return order;
    }

    // 7. Lấy TOÀN BỘ đơn hàng của 1 User kèm danh sách sản phẩm (Dùng cho trang Đơn hàng của tôi)
    public List<Order> getOrdersWithItemsByUserId(int userId) {
        List<Order> orderList = new ArrayList<>();
        String sqlOrders = "SELECT * FROM orders WHERE UserID = ? ORDER BY OrderDate DESC, ID DESC";
        String sqlItems = "SELECT od.ProductID, p.ProductName, pi.ImageURL, od.Quantity, od.Price " +
                "FROM order_detail od " +
                "JOIN products p ON od.ProductID = p.ID " +
                "LEFT JOIN product_image pi ON p.ID = pi.ProductID " +
                "WHERE od.OrderID = ? " +
                "GROUP BY p.ID";

        try (Connection con = ConnectionDB.getConnection()) {
            try (PreparedStatement psOrders = con.prepareStatement(sqlOrders)) {
                psOrders.setInt(1, userId);
                try (ResultSet rsOrders = psOrders.executeQuery()) {
                    while (rsOrders.next()) {
                        Order order = new Order();
                        order.setId(rsOrders.getInt("ID"));
                        order.setOrderDate(rsOrders.getTimestamp("OrderDate"));
                        order.setOrderStatus(rsOrders.getString("OrderStatus"));
                        order.setShippingFee(rsOrders.getDouble("ShippingFee"));
                        order.setTotalAmount(rsOrders.getDouble("TotalAmount"));
                        order.setShippingAddress(rsOrders.getString("ShippingAddress"));
                        order.setReceiverName(rsOrders.getString("ReceiverName"));
                        order.setReceiverPhone(rsOrders.getString("ReceiverPhone"));

                        int promoId = rsOrders.getInt("PromotionID");
                        order.setPromotionId(rsOrders.wasNull() ? null : promoId);

                        orderList.add(order);
                    }
                }
            }

            for (Order order : orderList) {
                List<OrderItem> items = new ArrayList<>();
                try (PreparedStatement psItems = con.prepareStatement(sqlItems)) {
                    psItems.setInt(1, order.getId());
                    try (ResultSet rsItems = psItems.executeQuery()) {
                        while (rsItems.next()) {
                            OrderItem item = new OrderItem(
                                    rsItems.getInt("ProductID"),
                                    rsItems.getString("ProductName"),
                                    rsItems.getString("ImageURL"),
                                    rsItems.getInt("Quantity"),
                                    rsItems.getDouble("Price")
                            );
                            items.add(item);
                        }
                    }
                }
                order.setItems(items);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderList;
    }

    // cập nhật thông tin thánh toán (trạng thái thanh toán, id giao dịch, thời gian)
    public boolean updatePaymentInfo(int orderId, String paymentStatus, String transactionId, java.util.Date paymentTime) throws Exception {
        String sql = "UPDATE orders SET payment_status = ?, transaction_id = ?, payment_time = ? WHERE ID = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentStatus);
            stmt.setString(2, transactionId);
            stmt.setTimestamp(3, paymentTime != null ? new Timestamp(paymentTime.getTime()) : null);
            stmt.setInt(4, orderId);
            return stmt.executeUpdate() > 0;
        }
    }
    // Cập nhật cả payment_status và OrderStatus cùng lúc
    public boolean updatePaymentStatusAndOrderStatus(int orderId, String paymentStatus, String orderStatus) throws Exception {
        String sql = "UPDATE orders SET payment_status = ?, OrderStatus = ? WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentStatus);
            stmt.setString(2, orderStatus);
            stmt.setInt(3, orderId);
            return stmt.executeUpdate() > 0;
        }
    }
}