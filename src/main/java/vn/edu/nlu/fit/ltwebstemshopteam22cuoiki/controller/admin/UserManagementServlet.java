package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.dao.UserDAO;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/admin-user")
public class UserManagementServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
/*
        //check bên filterAdmin

        // Kiểm tra xem admin
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");

        if (admin == null || !"admin".equals(admin.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dang-nhap");
            return;
        }*/

        try {
            // Lấy danh sách
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);

            request.getRequestDispatcher("/view/admin/admin-user.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Có lỗi");
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("update".equals(action)) {
                // Lấy thông tin từ form
                User user = new User();
                user.setId(Integer.parseInt(request.getParameter("id")));
                user.setFullName(request.getParameter("fullName"));
                user.setEmail(request.getParameter("email"));
                user.setPhoneNumber(request.getParameter("phoneNumber"));
                user.setAddress(request.getParameter("address"));
                user.setRole(request.getParameter("role"));
                user.setStatus(request.getParameter("status"));
                user.setUserName(request.getParameter("userName"));

                userDAO.updateUser(user);
            }
            //Các thao tác quản lí của admin: chỉnh sửa, khóa người dùng
            if ("editUser".equals(action){
                String paramId = request.getParameter("id");
                int id = Interger.parseInt(paramId);
                for (User user: users){
                    if (id== user.id){
                        user.setRole(request.getParameter("role"));
                        user.setActive(request.getParameter("active"));
                    }
                }
            }
            if ("lockUser".equals(action)) {
                String paramId = request.getParameter("id");
                int id = Integer.parseInt(paramId);
                userDAO.lockUser(id);
            }

            response.sendRedirect(request.getContextPath() + "/admin/admin-user");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Có lỗi ");
        }
    }


}
