<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="card">
  <h1>Login</h1>
  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>
  <form method="post" action="${pageContext.request.contextPath}/login" class="form">
    <label>Email
      <input type="email" name="email" required>
    </label>
    <label>Password
      <input type="password" name="password" required>
    </label>
    <button type="submit">Login</button>
  </form>
</section>
</main>
</body>
</html>
