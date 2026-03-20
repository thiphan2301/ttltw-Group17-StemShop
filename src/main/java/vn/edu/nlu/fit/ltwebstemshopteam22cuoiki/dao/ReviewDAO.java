package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao;

import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.config.ConnectionDB;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.Reviews;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    //lấy danh sách đánh gía theo id sản phẩm
    public static List<Reviews> getByProductId(int productId) {
        List<Reviews> list = new ArrayList<>();
        String sql = "SELECT r.*, u.UserName FROM reviews r JOIN users u ON r.UserID = u.ID "+
                "WHERE r.ProductID = ? "+
                "ORDER BY r.CreateDate DESC";

        try (
                Connection con = ConnectionDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Reviews r = new Reviews();
                r.setId(rs.getInt("ID"));
                r.setUserID(rs.getInt("UserID"));
                r.setProductID(rs.getInt("ProductID"));
                r.setRating(rs.getDouble("Rating"));
                r.setComment(rs.getString("Comment"));
                r.setCreateDate(rs.getTimestamp("CreateDate"));
                r.setUserName(rs.getString("UserName"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //Thêm review
    public static boolean addReview(Reviews r){
        String sql = "INSERT INTO reviews(UserID, ProductID, Rating, Comment, CreateDate)"+
                "VALUES(?,?,?,?,NOW())";
        try(
            Connection con = ConnectionDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ){
            ps.setInt(1, r.getUserID());
            ps.setInt(2, r.getProductID());
            ps.setDouble(3, r.getRating());
            ps.setString(4, r.getComment());

            return ps.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    // Lấy điểm trung bình
    public static double getAverageRating(int productId) {
        String sql = "SELECT AVG(Rating) FROM reviews WHERE ProductID = ?";
        try (Connection c = ConnectionDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy tổng số lượt đánh giá
    public static int getTotalReviews(int productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE ProductID = ?";
        try (Connection c = ConnectionDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
