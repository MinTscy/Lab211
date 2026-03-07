<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="card">
  <h1>Add New Product</h1>
  <p class="muted">Shop: ${shop.name}</p>
  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>
  <form method="post" action="${pageContext.request.contextPath}/seller/product/new" class="form">
    <label>Product Name
      <input type="text" name="name" required>
    </label>
    <label>Description
      <input type="text" name="description" required>
    </label>
    <label>Base Price
      <input type="number" step="0.01" name="basePrice" required>
    </label>
    <label>Image URL
      <input type="text" name="imageUrl" placeholder="https://...">
    </label>
    <label>Variant Color
      <input type="text" name="color" placeholder="e.g. Red">
    </label>
    <label>Variant Size
      <input type="text" name="size" placeholder="e.g. M">
    </label>
    <label>Stock
      <input type="number" name="stock" value="0">
    </label>
    <label>Price Delta
      <input type="number" step="0.01" name="priceDelta" value="0">
    </label>
    <div class="detail-actions">
      <a class="btn ghost-btn" href="${pageContext.request.contextPath}/seller/dashboard">Cancel</a>
      <button type="submit" class="btn">Save Product</button>
    </div>
  </form>
</section>
</main>
</body>
</html>
