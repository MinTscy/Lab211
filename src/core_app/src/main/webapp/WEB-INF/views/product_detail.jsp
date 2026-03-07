<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="detail card">
  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>
  <c:if test="${not empty product}">
    <div class="detail-grid">
      <div class="detail-media">
        <div class="detail-badge">FLASH SALE</div>
<<<<<<< HEAD
        <div class="detail-img" style="background-image:url('${product.imageUrl}');"></div>
=======
        <div class="detail-img"></div>
>>>>>>> 74c45db33ad1038a823f96d3912f1d93cb62d95d
      </div>
      <div class="detail-info">
        <h1>${product.name}</h1>
        <p class="muted">${product.shopName} • ⭐ ${product.shopRating}</p>
        <div class="detail-price">
          <span class="price">${product.basePrice}</span>
          <span class="detail-tag">-30%</span>
        </div>
        <div class="detail-desc">${product.description}</div>

        <form method="post" action="${pageContext.request.contextPath}/cart" class="detail-form">
          <label>Variant
            <select name="variantId">
              <c:forEach var="v" items="${variants}">
                <option value="${v.id}">${v.color} / ${v.size} (+${v.priceDelta})</option>
              </c:forEach>
            </select>
          </label>
          <label>Quantity
            <input type="number" name="quantity" value="1" min="1" class="qty">
          </label>
          <input type="hidden" name="action" value="add">
          <div class="detail-actions">
            <button type="submit" class="btn">Add to Cart</button>
            <a class="btn ghost-btn" href="${pageContext.request.contextPath}/checkout">Buy Now</a>
          </div>
        </form>
      </div>
    </div>
  </c:if>
</section>
</main>
</body>
</html>
