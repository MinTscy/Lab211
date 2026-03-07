<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="card">
  <h1>Seller Registration</h1>
  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>
  <form method="post" action="${pageContext.request.contextPath}/seller/register" class="form">
    <label>Name
      <input type="text" name="name" required>
    </label>
    <label>Email
      <input type="email" name="email" required>
    </label>
    <label>Password
      <input type="password" name="password" required>
    </label>
    <label>Shop Name
      <input type="text" name="shopName" required>
    </label>
    <button type="submit">Create Seller Account</button>
  </form>
</section>
</main>
</body>
</html>
