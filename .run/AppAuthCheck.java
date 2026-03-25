import com.lab211.ecommerce.dao.UserDAO;
import com.lab211.ecommerce.model.User;
import com.lab211.ecommerce.util.PasswordUtil;
public class AppAuthCheck {
  public static void main(String[] args) throws Exception {
    UserDAO dao = new UserDAO();
    User user = dao.findByEmail("user@local");
    System.out.println("USER_FOUND=" + (user != null));
    if (user != null) {
      System.out.println("EMAIL=" + user.getEmail());
      System.out.println("ROLE=" + user.getRole());
      System.out.println("DB_HASH=" + user.getPasswordHash());
      String inputHash = PasswordUtil.sha256("123456");
      System.out.println("INPUT_HASH=" + inputHash);
      System.out.println("MATCH=" + user.getPasswordHash().equals(inputHash));
    }
  }
}
