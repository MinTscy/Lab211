<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="card">
  <h1>Register</h1>
  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>
  <form method="post" action="${pageContext.request.contextPath}/register" class="form">
    <label>Name
      <input type="text" name="name" required>
    </label>
    <label>Email
      <input type="email" name="email" required>
    </label>
    <label>Password
      <input type="password" name="password" required>
    </label>
    <button type="submit">Create Account</button>
  </form>
<<<<<<< HEAD
  <div class="seller-cta">
    <span>Want to sell on Shoppe?</span>
    <a class="btn seller-btn" href="${pageContext.request.contextPath}/seller/register">Register as Seller</a>
  </div>
=======
>>>>>>> 74c45db33ad1038a823f96d3912f1d93cb62d95d
</section>
</main>
</body>
</html>
