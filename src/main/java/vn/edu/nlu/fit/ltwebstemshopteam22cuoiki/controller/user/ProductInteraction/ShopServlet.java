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

    private static final int PAGE_SIZE = 16; // số sp / trang

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }

        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoriDao = new CategoryDAO();

        int totalProducts = productDAO.countProducts();
        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);

        // 1. Lấy danh sách sản phẩm từ DB
        int offset = (page - 1) * PAGE_SIZE;
        List<Product> products = productDAO.getProductsByPage(offset, PAGE_SIZE);

        System.out.println("ShopServlet DOGET chạy");
        System.out.println("Products size = " + products.size());
 /*       System.out.println("Categories size = " + CategoryDAO.getAll().size());*/

        // 2. Gửi sang JSP
        request.setAttribute("products", products);
        request.setAttribute("categories", CategoryDAO.getAll());
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        // 3. Forward sang shop.jsp
        request.getRequestDispatcher("/view/shop/shop.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}