<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="card">
  <h1>Seller Dashboard</h1>
  <p class="muted">Shop: ${shop.name}</p>
  <div class="seller-cta">
    <a class="btn" href="${pageContext.request.contextPath}/seller/product/new">Add New Product</a>
  </div>
</section>

<section class="card">
  <h2>Your Products</h2>
  <div class="product-grid">
    <c:forEach var="p" items="${products}">
      <div class="product-card">
        <div class="product-card-top">
          <div>
            <h3>${p.name}</h3>
            <p class="muted">${p.description}</p>
          </div>
          <div class="price">${p.basePrice}</div>
        </div>
      </div>
    </c:forEach>
  </div>
</section>
</main>
</body>
</html>
