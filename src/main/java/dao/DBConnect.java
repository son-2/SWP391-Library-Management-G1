/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author Admin
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

    private static final String URL = "jdbc:mysql://localhost:3306/LibraryManagementDB"; // đổi tên DB nếu cần
    private static final String USER = "root"; // đổi user nếu khác
    private static final String PASSWORD = "1234"; // thay bằng password thật
    //test
//    public static Connection getConnection() throws SQLException {
//        String url = "jdbc:mysql://localhost:3306/library"
//                + "?useSSL=false"
//                + "&allowPublicKeyRetrieval=true"
//                + "&serverTimezone=UTC";
//        String user = "root";
//        String pass = "1234";
//        return DriverManager.getConnection(url, user, pass);
    //}
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Đảm bảo driver MySQL đã được load
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }
    }

    // (Optional) đóng kết nối nếu cần
    public static void close(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (Connection conn = DBConnect.getConnection()) {
            if (conn != null) {
                System.out.println("Kết nối thành công đến MySQL!");
            } else {
                System.out.println("Không thể kết nối đến MySQL.");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi kết nối đến MySQL:");
            e.printStackTrace();
        }

    }
}
