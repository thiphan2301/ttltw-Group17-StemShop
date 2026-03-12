package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.controller.user.ProductInteraction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao.ReviewDAO;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.Reviews;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.User;

import java.io.IOException;

@WebServlet("/add-review")
public class AddReviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =  request.getSession();
        //
        if(session == null || session.getAttribute("user") == null){
            response.sendRedirect("sign-in.jsp");
            return;
        }
        User user  = (User)session.getAttribute("user");

        int productId = Integer.parseInt(request.getParameter("productId"));
        double rating =  Double.parseDouble(request.getParameter("rating"));
        String comment  = request.getParameter("comment");

        Reviews r = new Reviews();
        r.setUserID(user.getId());
        r.setProductID(productId);
        r.setRating(rating);
        r.setComment(comment);

        ReviewDAO.addReview(r);

        //quay lại trang chi tiết sp rồi load lại danh sách review
        response.sendRedirect("product-detail?id=" + productId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}