package Controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "Signout", value = "/Signout")
public class Signout extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleLogout(request, response);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 1) Xoá session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("user");
            session.invalidate();
        }

        // 2) Xoá cookie remember-me nếu có (username / userName)
        deleteCookie("username", request, response);
        deleteCookie("userName", request, response);

        // 3) Điều hướng
        String contextPath = request.getContextPath();
        String redirect = request.getParameter("redirect");
        String target = (redirect == null || redirect.isBlank())
                ? contextPath + "/home.jsp"
                : safeInternalRedirect(redirect, contextPath);

        response.sendRedirect(target);
    }

    private void deleteCookie(String name, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0); // expire ngay
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        cookie.setHttpOnly(true);
        // Chỉ bật secure khi đang dùng HTTPS để tránh lỗi dev HTTP
        if (request.isSecure()) cookie.setSecure(true);
        response.addCookie(cookie);
    }

    /**
     * Chỉ cho phép redirect nội bộ để tránh open-redirect.
     */
    private String safeInternalRedirect(String redirect, String contextPath) {
        if (redirect.startsWith("http://") || redirect.startsWith("https://")) {
            return contextPath + "/home.jsp";
        }
        if (redirect.startsWith(contextPath)) {
            return redirect;
        }
        if (redirect.startsWith("/")) {
            return contextPath + redirect;
        }
        return contextPath + "/" + redirect;
    }
}