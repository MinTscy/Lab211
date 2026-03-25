<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="card">
  <h1>Admin - Products</h1>
  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>

  <div class="grid">
    <div>
      <h2>Create Product</h2>
      <form method="post" action="${pageContext.request.contextPath}/admin/products" class="form">
        <input type="hidden" name="action" value="create">
        <label>Shop ID
          <input type="number" name="shopId" value="1" min="1" required>
        </label>
        <label>Name
          <input type="text" name="name" required>
        </label>
        <label>Description
          <input type="text" name="description" required>
        </label>
        <label>Image URL
          <input type="text" name="imageUrl" placeholder="https://...">
        </label>
        <label>Base Price
          <input type="number" step="0.01" name="basePrice" required>
        </label>
        <button type="submit">Create</button>
      </form>
    </div>

    <div>
      <h2>Products List</h2>
      <c:forEach var="p" items="${products}">
        <div class="admin-item">
          <form method="post" action="${pageContext.request.contextPath}/admin/products" class="form inline">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" value="${p.id}">
            <input type="number" name="shopId" value="${p.shopId}" min="1" class="short">
            <input type="text" name="name" value="${p.name}">
            <input type="text" name="description" value="${p.description}">
            <input type="text" name="imageUrl" value="${p.imageUrl}" placeholder="Image URL">
            <input type="number" step="0.01" name="basePrice" value="${p.basePrice}">
            <label class="check">
              <input type="checkbox" name="active" <c:if test="${p.active}">checked</c:if>> Active
            </label>
            <button type="submit">Save</button>
          </form>
          <form method="post" action="${pageContext.request.contextPath}/admin/products" class="form inline">
            <input type="hidden" name="action" value="addVariant">
            <input type="hidden" name="productId" value="${p.id}">
            <input type="text" name="color" placeholder="Color">
            <input type="text" name="size" placeholder="Size">
            <input type="number" name="stock" placeholder="Stock">
            <input type="number" step="0.01" name="priceDelta" placeholder="Price Delta">
            <button type="submit">Add Variant</button>
          </form>
        </div>
      </c:forEach>
    </div>
  </div>
</section>
</main>
</body>
</html>
