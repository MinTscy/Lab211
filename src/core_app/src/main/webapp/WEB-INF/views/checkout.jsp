<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="card">
  <h1>Checkout</h1>
  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>
  <c:if test="${not empty success}">
    <div class="alert success">${success}</div>
  </c:if>
  <form method="post" action="${pageContext.request.contextPath}/checkout" class="form">
    <label>Voucher Code (optional)
      <input type="text" name="voucher">
    </label>
    <button type="submit">Place Order</button>
  </form>
</section>
</main>
</body>
</html>
