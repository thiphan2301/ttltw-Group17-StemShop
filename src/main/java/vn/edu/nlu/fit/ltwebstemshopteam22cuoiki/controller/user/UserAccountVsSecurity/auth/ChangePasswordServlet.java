package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.controller.user.UserAccountVsSecurity.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao.UserDAO;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.User;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.util.PasswordUtil;

import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/dang-nhap");
            return;
        }

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        UserDAO dao = new UserDAO();

        // 1. check không để trống phần đổi mật khẩu
        if (oldPassword == null || oldPassword.trim().isEmpty()
                || newPassword == null || newPassword.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()) {

            request.setAttribute("error1", "Vui lòng nhập password để đổi");
            request.getRequestDispatcher("/view/user/profile.jsp").forward(request, response);
            return;
        }
        // 2. Check mật khẩu cũ
        String oldHash = PasswordUtil.md5(oldPassword);
        if (!dao.checkPassword(user.getId(), oldHash)) {
            request.setAttribute("error1", "Mật khẩu hiện tại không đúng");
            request.getRequestDispatcher("/view/user/profile.jsp").forward(request, response);
            return;
        }

        // 3. Check mật khẩu mới khớp
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error1", "Xác nhận mật khẩu không khớp");
            request.getRequestDispatcher("/view/user/profile.jsp").forward(request, response);
            return;
        }

        // 4. check validation
        if (!PasswordUtil.isValidPassword(newPassword)) {
            request.setAttribute("error1",
                    "Mật khẩu phải ≥8 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt");
            request.getRequestDispatcher("/view/user/profile.jsp").forward(request, response);
            return;
        }

        // 5. Hash mật khẩu mới và update mật khẩu mới
        String newHash = PasswordUtil.md5(newPassword);
        dao.updatePassword(user.getId(), newHash);

        user.setPassword(newHash);
        session.setAttribute("user", user);

        request.setAttribute("message", "Đổi mật khẩu thành công");
        request.getRequestDispatcher("/view/user/profile.jsp").forward(request, response);


    }
}
