package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao;

import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.config.ConnectionDB;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.CartItem;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.Order;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.OrderItemView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDAO {

    public int insert(Order order) throws Exception {

        String sql = "INSERT INTO orders (UserID, PromotionID, OrderDate, OrderStatus, ShippingFee, TotalAmount, Note, ShippingAddress, ReceiverName, ReceiverPhone) VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getUserId());

            if (order.getPromotionId() != null)
                ps.setInt(2, order.getPromotionId());
            else
                ps.setNull(2, Types.INTEGER);

            ps.setString(3, order.getOrderStatus());
            ps.setDouble(4, order.getShippingFee());
            ps.setDouble(5, order.getTotalAmount());
            ps.setString(6, order.getNote());
            ps.setString(7, order.getShippingAddress());
            ps.setString(8, order.getReceiverName());
            ps.setString(9, order.getReceiverPhone());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }

        return -1;
    }

    // hàm láy toàn bộ lịch sử mua hàng của user
    public List<OrderItemView> getOrderHistoryByUser(int userId) {
        List<OrderItemView> list = new ArrayList<>();

        String sql =
                "SELECT o.ID AS orderId, o.OrderStatus, " +
                        "p.ProductName, pi.ImageURL, od.Quantity, od.Price " +
                        "FROM orders o " +
                        "JOIN order_detail od ON o.ID = od.OrderID " +
                        "JOIN products p ON od.ProductID = p.ID " +
                        "LEFT JOIN product_image pi ON p.ID = pi.ProductID " +
                        "WHERE o.UserID = ? " +
                        "GROUP BY o.ID, p.ID " +
                        "ORDER BY o.OrderDate DESC, o.ID DESC";

        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new OrderItemView(
                        rs.getInt("orderId"),
                        rs.getString("ProductName"),
                        rs.getString("ImageURL"),
                        rs.getInt("Quantity"),
                        rs.getDouble("Price"),
                        rs.getString("OrderStatus")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE UserID=? ORDER BY OrderDate DESC";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("ID"));
                order.setOrderDate(rs.getTimestamp("OrderDate"));
                order.setTotalAmount(rs.getDouble("TotalAmount"));


                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    //lấy tổng số lương đơn hàng
    public int countOrders(){
        String sql = "SELECT COUNT(*) FROM orders";
        try(
                Connection con = ConnectionDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                ){
            if(rs.next()) return rs.getInt(1);

        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    //Tính tổng doanh thu (doanh thu được tính ở những đơn hàng thành công)
    public double getTotalRevenue() {
        String sql = "SELECT SUM(TotalAmount) FROM orders WHERE OrderStatus = 'CONFIRMED'";
        try (Connection con = ConnectionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // hàm lấy danh sách đơn hàng
    public List<Order> getAllOrdersForAdmin() {
        List<Order> list = new ArrayList<>();

        String sql = "SELECT o.*, u.FullName, u.PhoneNumber "+
        "FROM orders o "+
        "JOIN users u ON o.UserID = u.ID "+
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

    // cập nhật trạng thái đơn hàng
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
    //------------------------------//

    // LẤY CHI TIẾT 1 ĐƠN HÀNG DỰA VÀO ID
    public Order getOrderById(int orderId) throws Exception {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE ID = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("ID"));
                order.setUserId(rs.getInt("UserID"));

                // Xử lý an toàn cho PromotionID (vì có thể null)
                int promoId = rs.getInt("PromotionID");
                if (!rs.wasNull()) {
                    order.setPromotionId(promoId);
                } else {
                    order.setPromotionId(null);
                }

                order.setOrderDate(rs.getTimestamp("OrderDate"));
                order.setOrderStatus(rs.getString("OrderStatus"));
                order.setShippingFee(rs.getDouble("ShippingFee"));
                order.setTotalAmount(rs.getDouble("TotalAmount"));
                order.setNote(rs.getString("Note"));
                order.setShippingAddress(rs.getString("ShippingAddress"));
                order.setReceiverName(rs.getString("ReceiverName"));
                order.setReceiverPhone(rs.getString("ReceiverPhone"));

                // Lưu ý: Trong Order.java của bạn KHÔNG CÓ trường paymentMethod
                // Nên trên JSP đoạn kiểm tra thanh toán nó sẽ luôn tự động hiện là "Thanh toán khi nhận hàng (COD)"
            }
        }
        return order;
    }

}