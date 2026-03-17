package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.controller.user.CartVsCheckout.checkout;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao.OrderDAO;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao.OrderDetailDAO;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao.Promotions.PromotionDAO;
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

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/view/user/sign-in.jsp");
            return;
        }

        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        double subTotal = cart.getTotalPrice();
        double baseShippingFee = 30000;

        req.setAttribute("user", user);
        req.setAttribute("cart", cart);

        // Truyền đầy đủ các biến sang JSP
        req.setAttribute("subTotal", subTotal);
        req.setAttribute("productDiscount", 0.0);
        req.setAttribute("finalShippingFee", baseShippingFee);
        req.setAttribute("finalTotalAmount", subTotal + baseShippingFee);

        // Xóa các mã cũ trong session nếu người dùng refresh lại trang mới hoàn toàn
        session.removeAttribute("savedVoucherProduct");
        session.removeAttribute("savedProductDiscount");
        session.removeAttribute("savedVoucherShip");
        session.removeAttribute("savedShipDiscount");

        req.getRequestDispatcher("/view/shop/checkout.jsp").forward(req, resp);
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

            // Giữ lại form data
            req.setAttribute("receiverName", req.getParameter("receiverName"));
            req.setAttribute("receiverPhone", req.getParameter("receiverPhone"));
            req.setAttribute("address", req.getParameter("address"));
            req.setAttribute("city", req.getParameter("city"));
            req.setAttribute("note", req.getParameter("note"));

            String percent = "percent";
            String amount = "amount";
            PromotionDAO promotionDAO = new PromotionDAO();

            String action = req.getParameter("action");
            if (action == null) action = "order";

            // ÁP DỤNG MÃ SẢN PHẨM
            if ("applyVoucherProduct".equals(action)) {
                String codeProduct = req.getParameter("voucherCodeProduct");
                double productDiscount = 0;

                if (promotionDAO.getCode().contains(codeProduct)) {
                    String discountType = promotionDAO.getDiscountType(codeProduct);
                    double val = promotionDAO.getDiscountValue(codeProduct, discountType);

                    productDiscount = percent.equals(discountType) ? cart.getTotalPrice() * (val / 100) : val;
                }
                session.setAttribute("savedVoucherProduct", codeProduct);
                session.setAttribute("savedProductDiscount", productDiscount);

                // ÁP DỤNG MÃ VẬN CHUYỂN
            } else if ("applyVoucherShip".equals(action)) {
                String codeShip = req.getParameter("voucherCodeShip");
                double shipDiscount = 0;

                if (promotionDAO.getCode().contains(codeShip)) {
                    String discountType = promotionDAO.getDiscountType(codeShip);
                    double val = promotionDAO.getDiscountValue(codeShip, discountType);
                    double baseShippingFee = 30000;

                    shipDiscount = percent.equals(discountType) ? baseShippingFee * (val / 100) : val;
                    if (shipDiscount > baseShippingFee) shipDiscount = baseShippingFee;
                }
                session.setAttribute("savedVoucherShip", codeShip);
                session.setAttribute("savedShipDiscount", shipDiscount);
            }

            // TÍNH TOÁN TIỀN
            Double pd = (Double) session.getAttribute("savedProductDiscount");
            double finalProductDiscount = (pd != null) ? pd : 0;

            Double sd = (Double) session.getAttribute("savedShipDiscount");
            double finalShipDiscount = (sd != null) ? sd : 0;

            double subTotal = cart.getTotalPrice();
            double baseShippingFee = 30000;
            double finalShippingFee = baseShippingFee - finalShipDiscount;

            double finalTotalAmount = (subTotal - finalProductDiscount) + finalShippingFee;

            // ĐẶT HÀNG
            if ("order".equals(action)) {
                Order order = new Order();
                order.setUserId(user.getId());
                order.setOrderStatus("PENDING");
                order.setShippingFee(finalShippingFee);
                order.setTotalAmount(finalTotalAmount);
                order.setNote(req.getParameter("note"));
                order.setShippingAddress(req.getParameter("address") + ", " + req.getParameter("city"));
                order.setReceiverName(req.getParameter("receiverName"));
                order.setReceiverPhone(req.getParameter("receiverPhone"));

                OrderDAO orderDAO = new OrderDAO();
                int orderId = orderDAO.insert(order);

                OrderDetailDAO detailDAO = new OrderDetailDAO();
                for (CartItem item : cart.getItems()) {
                    OrderDetail detail = new OrderDetail(
                            orderId, item.getProduct().getId(),
                            item.getQuantity(), item.getProduct().getPrice()
                    );
                    detailDAO.insert(detail);
                }

                session.removeAttribute("cart");
                session.removeAttribute("savedVoucherProduct");
                session.removeAttribute("savedProductDiscount");
                session.removeAttribute("savedVoucherShip");
                session.removeAttribute("savedShipDiscount");

                resp.sendRedirect(req.getContextPath() + "/order-success");
                return;
            }

            // Render lại JSP
            req.setAttribute("user", user);
            req.setAttribute("cart", cart);
            req.setAttribute("subTotal", subTotal);
            req.setAttribute("productDiscount", finalProductDiscount);
            req.setAttribute("finalShippingFee", finalShippingFee);
            req.setAttribute("finalTotalAmount", finalTotalAmount);

            req.getRequestDispatcher("/view/shop/checkout.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }
}