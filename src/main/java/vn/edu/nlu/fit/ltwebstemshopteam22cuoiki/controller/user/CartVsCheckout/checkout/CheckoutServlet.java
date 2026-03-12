package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.controller.user.CartVsCheckout.checkout;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao.OrderDAO;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao.OrderDetailDAO;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.*;

import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();

        User user = (User) session.getAttribute("user");
        Cart cart = (Cart) session.getAttribute("cart");

        // chưa login
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/view/user/sign-in.jsp");
            return;
        }

        // giỏ hàng trống
        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        req.setAttribute("user", user);
        req.setAttribute("cart", cart);
        req.setAttribute("totalAmount", cart.getTotalPrice());

        req.getRequestDispatcher("/view/shop/checkout.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession();

            User user = (User) session.getAttribute("user");
            Cart cart = (Cart) session.getAttribute("cart");

            if (user == null || cart == null || cart.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/view/user/sign-in.jsp");
                return;
            }

            // dữ liệu form
            String receiverName = req.getParameter("receiverName");
            String receiverPhone = req.getParameter("receiverPhone");
            String address = req.getParameter("address");
            String city = req.getParameter("city");
            String note = req.getParameter("note");

            double shippingFee = 30000;

            Order order = new Order();
            order.setUserId(user.getId());
            order.setPromotionId(null);
            order.setOrderStatus("PENDING"); //mặt định là PENDING trong trang admin duyệt thì mới update đơn hàng đó thành trạng thái CONFIRMED
            order.setShippingFee(shippingFee);
            order.setTotalAmount(cart.getTotalPrice() + shippingFee);
            order.setNote(note);
            order.setShippingAddress(address + ", " + city);
            order.setReceiverName(receiverName);
            order.setReceiverPhone(receiverPhone);

            OrderDAO orderDAO = new OrderDAO();
            int orderId = orderDAO.insert(order);

            OrderDetailDAO detailDAO = new OrderDetailDAO();
            for (CartItem item : cart.getItems()) {
                OrderDetail detail = new OrderDetail(
                        orderId,
                        item.getProduct().getId(),
                        item.getQuantity(),
                        item.getProduct().getPrice()
                );
                detailDAO.insert(detail);
            }

            // clear cart
            session.removeAttribute("cart");

            resp.sendRedirect(req.getContextPath() + "/order-success");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }
}