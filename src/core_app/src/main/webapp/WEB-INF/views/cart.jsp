<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="card cart-card">
  <div class="page-header">
    <div>
      <h1>Your Cart</h1>
      <p class="muted">Review items, apply vouchers, and get ready for checkout.</p>
    </div>
    <div class="pill-row">
      <span class="pill">Flash Sale</span>
      <span class="pill">Free Returns</span>
      <span class="pill">24/7 Support</span>
    </div>
  </div>
  <c:if test="${empty sessionScope.cart}">
    <div class="empty-state">
      <div class="empty-visual">
        <svg width="220" height="170" viewBox="0 0 220 170" aria-hidden="true">
          <rect x="18" y="36" width="184" height="100" rx="18" fill="#F2F6FF"/>
          <rect x="40" y="60" width="140" height="52" rx="12" fill="#D9E6FF"/>
          <circle cx="76" cy="126" r="10" fill="#6A8BFF"/>
          <circle cx="150" cy="126" r="10" fill="#6A8BFF"/>
          <path d="M65 60L55 38H28" stroke="#6A8BFF" stroke-width="6" stroke-linecap="round"/>
          <path d="M110 76h48" stroke="#6A8BFF" stroke-width="6" stroke-linecap="round"/>
          <path d="M110 94h32" stroke="#6A8BFF" stroke-width="6" stroke-linecap="round"/>
        </svg>
      </div>
      <div>
        <h2>Cart is empty</h2>
        <p class="muted">Browse the marketplace and add your favorite variants.</p>
        <a class="btn" href="${pageContext.request.contextPath}/products">Explore Products</a>
      </div>
    </div>
  </c:if>
  <c:if test="${not empty sessionScope.cart}">
    <c:set var="total" value="0" />
    <div class="order-grid">
      <c:forEach var="item" items="${sessionScope.cart.values()}">
        <c:set var="line" value="${item.unitPrice * item.quantity}" />
        <c:set var="total" value="${total + line}" />
        <div class="order-card">
          <div class="order-img"></div>
          <div class="order-badge">-10%</div>
          <div class="order-name">${item.productName}</div>
          <div class="order-meta">${item.color} / ${item.size}</div>
          <div class="order-price">${item.unitPrice}</div>
          <div class="order-actions">
            <form method="post" action="${pageContext.request.contextPath}/cart" class="inline">
              <input type="hidden" name="action" value="update">
              <input type="hidden" name="variantId" value="${item.variantId}">
              <input type="number" name="quantity" value="${item.quantity}" min="0" class="qty">
              <button type="submit">Update</button>
            </form>
            <form method="post" action="${pageContext.request.contextPath}/cart" class="inline">
              <input type="hidden" name="action" value="remove">
              <input type="hidden" name="variantId" value="${item.variantId}">
              <button type="submit" class="danger">Remove</button>
            </form>
          </div>
          <div class="order-subtotal">Subtotal: ${line}</div>
        </div>
      </c:forEach>
    </div>
    <div class="total">Total: ${total}</div>
    <a class="btn" href="${pageContext.request.contextPath}/checkout">Proceed to Checkout</a>
  </c:if>
</section>
</main>
</body>
</html>
