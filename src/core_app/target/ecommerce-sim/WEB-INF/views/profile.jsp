<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="card">
  <div class="page-header">
    <div>
      <h1>My Profile</h1>
      <p class="muted">Update your contact information and password.</p>
    </div>
    <span class="pill">${profileUser.role}</span>
  </div>

  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>
  <c:if test="${not empty success}">
    <div class="alert success">${success}</div>
  </c:if>

  <form method="post" action="${pageContext.request.contextPath}/profile" class="form profile-form">
    <label>Name
      <input type="text" name="name" value="${profileUser.name}" required>
    </label>
    <label>Email
      <input type="email" name="email" value="${profileUser.email}" required>
    </label>
    <label>Phone
      <input type="text" name="phone" value="${profileUser.phone}">
    </label>
    <label>Address
      <input type="text" name="address" value="${profileUser.address}">
    </label>
    <label>Current Password
      <input type="password" name="currentPassword" placeholder="Required only when changing password">
    </label>
    <label>New Password
      <input type="password" name="newPassword" placeholder="Leave blank to keep current password">
    </label>
    <button type="submit">Save Profile</button>
  </form>
</section>
</main>
</body>
</html>
