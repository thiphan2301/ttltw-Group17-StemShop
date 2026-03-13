package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.controller.user.ProductInteraction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao.CategoryDAO;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao.ProductDAO;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.Product;

import java.io.IOException;
import java.util.List;

@WebServlet("/shop")
public class ShopServlet extends HttpServlet {

    //private static final int PAGE_SIZE = 16;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductDAO productDAO = new ProductDAO();

        // Lấy tất cả danh sách sản phẩm
        List<Product> products = productDAO.getAll();

        //  Gửi sang JSP
        request.setAttribute("products", products);
        request.setAttribute("categories", CategoryDAO.getAll());

        // Forward sang shop.jsp
        request.getRequestDispatcher("/view/shop/shop.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}