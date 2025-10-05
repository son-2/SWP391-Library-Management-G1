package Controller;

import dao.UserDAO;
import model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(name = "Signin", value = "/Signin")
public class Signin extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu đã đăng nhập thì quay về trang chủ
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/home.jsp");
            return;
        }
        request.getRequestDispatcher("/signin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = safeTrim(request.getParameter("username"));
        String password = safeTrim(request.getParameter("password"));
        String remember = request.getParameter("rememberMe");

        if (username.isEmpty() || password.isEmpty()) {
            request.setAttribute("result", "Please enter both username and password.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/signin.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.login(username, password);

        if (user != null) {

            // Kiểm tra trạng thái tài khoản
            if (user.getStatus() == User.Status.Suspended) {
                request.setAttribute("result", "Your account has been suspended. Please contact admin.");
                request.getRequestDispatcher("/signin.jsp").forward(request, response);
                return;
            } else if (user.getStatus() == User.Status.Inactive) {
                request.setAttribute("result", "Your account is not active yet.");
                request.getRequestDispatcher("/signin.jsp").forward(request, response);
                return;
            }



            // Tạo session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60); // 30 phút

            // Remember Me
            if ("on".equalsIgnoreCase(remember)) {
                Cookie cookie = new Cookie("username", user.getUsername());
                cookie.setMaxAge(60 * 60 * 24 * 30); // 30 ngày
                cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                cookie.setHttpOnly(true);
                if (request.isSecure()) cookie.setSecure(true);
                response.addCookie(cookie);
            }

            // Chuyển hướng theo vai trò
            String redirectUrl = switch (user.getRole().getRoleCode()) {
                case "ADMIN" -> "admin/dashboard.jsp";
                case "LIBRARIAN" -> "librarian/home.jsp";
                default -> "home.jsp";
            };

            response.sendRedirect(request.getContextPath() + "/" + redirectUrl);
        } else {
            request.setAttribute("result", "Invalid username or password");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/signin.jsp").forward(request, response);
        }
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    @Override
    public String getServletInfo() {
        return "Handles user login using model.User";
    }
}