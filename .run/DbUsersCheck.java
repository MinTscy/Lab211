public class DbUsersCheck {
  public static void main(String[] args) throws Exception {
    Class.forName("com.mysql.cj.jdbc.Driver");
    try (java.sql.Connection conn = java.sql.DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/lab211_ecommerce?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf8",
        "root",
        "123456")) {
      try (java.sql.PreparedStatement ps = conn.prepareStatement(
          "SELECT id, name, email, password_hash, role FROM users WHERE email IN (?, ?) ORDER BY id")) {
        ps.setString(1, "admin@local");
        ps.setString(2, "user@local");
        try (java.sql.ResultSet rs = ps.executeQuery()) {
          boolean found = false;
          while (rs.next()) {
            found = true;
            System.out.println("ID=" + rs.getInt("id"));
            System.out.println("NAME=" + rs.getString("name"));
            System.out.println("EMAIL=" + rs.getString("email"));
            System.out.println("ROLE=" + rs.getString("role"));
            System.out.println("HASH=" + rs.getString("password_hash"));
            System.out.println("---");
          }
          if (!found) {
            System.out.println("NO_MATCHING_USERS");
          }
        }
      }
      try (java.sql.Statement st = conn.createStatement(); java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
        if (rs.next()) System.out.println("TOTAL_USERS=" + rs.getInt(1));
      }
    }
  }
}
