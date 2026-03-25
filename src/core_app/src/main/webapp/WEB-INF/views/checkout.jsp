<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="card checkout-card">
  <div class="page-header">
    <div>
      <h1>Checkout</h1>
      <p class="muted">Confirm voucher and place your order securely.</p>
    </div>
    <div class="pill-row">
      <span class="pill">Secure Payment</span>
      <span class="pill">Fast Delivery</span>
      <span class="pill">Buyer Protection</span>
    </div>
  </div>

  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>
  <c:if test="${not empty success}">
    <div class="alert success">${success}</div>
  </c:if>

  <div class="checkout-grid">
    <div class="checkout-main">
      <form method="post" action="${pageContext.request.contextPath}/checkout" class="form">
        <label>Voucher Code (optional)
          <input type="text" name="voucher" placeholder="e.g. FLASH10" value="${voucherCode}">
        </label>
        <button type="submit">Place Order</button>
      </form>
      <c:if test="${not empty summaryError}">
        <div class="alert error">${summaryError}</div>
      </c:if>
      <c:if test="${summary != null && summary.appliedVoucher != null && summary.discount > 0}">
        <div class="alert success">
          Voucher <strong>${summary.appliedVoucher.code}</strong> applied successfully. You saved
          <strong>${summary.discount}</strong> on this order.
        </div>
      </c:if>
      <c:if test="${summary != null && summary.appliedVoucher != null}">
        <div class="card inset-card">
          <h2>Applied Voucher</h2>
          <div class="summary-row">
            <span>${summary.appliedVoucher.code}</span>
            <strong>
              <c:choose>
                <c:when test="${summary.appliedVoucher.productId != null}">
                  Product voucher
                </c:when>
                <c:otherwise>
                  Order voucher
                </c:otherwise>
              </c:choose>
            </strong>
          </div>
          <div class="summary-row">
            <span>Eligible subtotal</span>
            <strong>${summary.eligibleSubtotal}</strong>
          </div>
          <div class="summary-total">
            <span>Saved by voucher</span>
            <strong>- ${summary.discount}</strong>
          </div>
        </div>
      </c:if>
    </div>

    <aside class="checkout-side">
      <h2>Order Summary</h2>
      <c:forEach var="item" items="${sessionScope.cart.values()}">
        <c:set var="line" value="${item.unitPrice * item.quantity}" />
        <div class="summary-row">
          <span>${item.productName} x${item.quantity}</span>
          <strong>${line}</strong>
        </div>
      </c:forEach>
      <div class="summary-total">
        <span>Total</span>
        <strong>${summary != null ? summary.total : 0}</strong>
      </div>
      <div class="summary-row">
        <span>Discount</span>
        <strong>- ${summary != null ? summary.discount : 0}</strong>
      </div>
      <div class="summary-total">
        <span>Final</span>
        <strong>${summary != null ? summary.finalAmount : 0}</strong>
      </div>
    </aside>
  </div>
</section>
</main>
</body>
</html>
