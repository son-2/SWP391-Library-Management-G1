package dao;

import java.sql.*;
import model.User;
import model.Role;


public class UserDAO {

    // Thêm người dùng mới
    public boolean insertUser(User user) {
        String sql = "INSERT INTO Users (Username, Email, PasswordHash, FullName, Phone, Address, RoleID, Status, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 'Active', NOW())";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAddress());
            ps.setInt(7, user.getRole().getRoleId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm User: " + e.getMessage());
            return false;
        }
    }

    // Kiểm tra trùng username hoặc email
    public boolean isDuplicateUser(String username, String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ? OR Email = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra trùng User: " + e.getMessage());
        }
        return false;
    }

    // Đăng nhập (trả về User nếu đúng)
    public User login(String username, String password) {
        String sql = "SELECT u.*, r.RoleName, r.RoleCode FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID "
                + "WHERE u.Username = ? AND u.PasswordHash = ? AND u.Status = 'Active'";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                Role role = new Role();

                user.setUserId(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setFullName(rs.getString("FullName"));
                user.setPhone(rs.getString("Phone"));
                user.setAddress(rs.getString("Address"));
                user.setStatus(User.Status.valueOf(rs.getString("Status")));
                user.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                user.setUpdatedAt(rs.getTimestamp("UpdatedAt") != null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null);
                user.setLastLogin(rs.getTimestamp("LastLogin") != null ? rs.getTimestamp("LastLogin").toLocalDateTime() : null);

                role.setRoleId(rs.getInt("RoleID"));
                role.setRoleName(rs.getString("RoleName"));
                role.setRoleCode(rs.getString("RoleCode"));
                user.setRole(role);

                return user;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi đăng nhập: " + e.getMessage());
        }
        return null;
    }

    // Cập nhật thời gian đăng nhập cuối
    public void updateLastLogin(int userId) {
        String sql = "UPDATE Users SET LastLogin = NOW() WHERE UserID = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật LastLogin: " + e.getMessage());
        }
    }

    // Lấy thông tin user theo ID
    public User getUserById(int userId) {
        String sql = "SELECT u.*, r.RoleName, r.RoleCode FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID WHERE u.UserID = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                Role role = new Role();

                user.setUserId(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                user.setFullName(rs.getString("FullName"));
                user.setPhone(rs.getString("Phone"));
                user.setAddress(rs.getString("Address"));
                user.setStatus(User.Status.valueOf(rs.getString("Status")));
                role.setRoleId(rs.getInt("RoleID"));
                role.setRoleName(rs.getString("RoleName"));
                role.setRoleCode(rs.getString("RoleCode"));
                user.setRole(role);

                return user;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy User theo ID: " + e.getMessage());
        }
        return null;
    }
}