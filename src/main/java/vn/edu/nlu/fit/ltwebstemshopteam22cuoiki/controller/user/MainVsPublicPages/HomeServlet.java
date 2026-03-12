package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.controller.user.MainVsPublicPages;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao.ProductDAO;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.Product;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = { "/index", "/home"})
public class HomeServlet extends HttpServlet {
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            System.out.println("=== HomeServlet được gọi! ===");

            // Lấy 6 sản phẩm đầu tiên
            List<Product> products = productDAO.getFirst6Products();
            System.out.println("Số sản phẩm: " + products.size());



            request.setAttribute("products", products);
            request.getRequestDispatcher("/index.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("LỖI trong HomeServlet:");
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}