<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="card">
  <h1>Admin - Vouchers</h1>
  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>

  <div class="grid admin-grid">
    <div>
      <h2>Create Voucher</h2>
      <form method="post" action="${pageContext.request.contextPath}/admin/vouchers" class="form">
        <input type="hidden" name="action" value="create">
        <label>Code
          <input type="text" name="code" required>
        </label>
        <label>Apply to Product
          <select name="productId">
            <option value="0">All products</option>
            <c:forEach var="product" items="${products}">
              <option value="${product.id}">${product.name}</option>
            </c:forEach>
          </select>
        </label>
        <label>Discount Type
          <select name="discountType">
            <option value="PERCENT">Percent</option>
            <option value="FIXED">Fixed</option>
          </select>
        </label>
        <label>Discount Value
          <input type="number" step="0.01" name="discountValue" required>
        </label>
        <label>Max Discount
          <input type="number" step="0.01" name="maxDiscount" value="0">
        </label>
        <label>Min Order
          <input type="number" step="0.01" name="minOrder" value="0">
        </label>
        <label>Start At
          <input type="datetime-local" name="startAt">
        </label>
        <label>End At
          <input type="datetime-local" name="endAt">
        </label>
        <button type="submit">Create Voucher</button>
      </form>
    </div>

    <div>
      <h2>Voucher List</h2>
      <c:forEach var="voucher" items="${vouchers}">
        <div class="admin-item">
          <form method="post" action="${pageContext.request.contextPath}/admin/vouchers" class="form">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" value="${voucher.id}">
            <label>Code
              <input type="text" name="code" value="${voucher.code}" required>
            </label>
            <label>Apply to Product
              <select name="productId">
                <option value="0">All products</option>
                <c:forEach var="product" items="${products}">
                  <option value="${product.id}" <c:if test="${voucher.productId == product.id}">selected</c:if>>${product.name}</option>
                </c:forEach>
              </select>
            </label>
            <div class="inline">
              <label>Type
                <select name="discountType">
                  <option value="PERCENT" <c:if test="${voucher.discountType == 'PERCENT'}">selected</c:if>>Percent</option>
                  <option value="FIXED" <c:if test="${voucher.discountType == 'FIXED'}">selected</c:if>>Fixed</option>
                </select>
              </label>
              <label>Value
                <input type="number" step="0.01" name="discountValue" value="${voucher.discountValue}" required>
              </label>
              <label>Max
                <input type="number" step="0.01" name="maxDiscount" value="${voucher.maxDiscount}">
              </label>
              <label>Min
                <input type="number" step="0.01" name="minOrder" value="${voucher.minOrder}">
              </label>
            </div>
            <div class="inline">
              <label>Start
                <input type="datetime-local" name="startAt">
              </label>
              <label>End
                <input type="datetime-local" name="endAt">
              </label>
            </div>
            <label class="check">
              <input type="checkbox" name="active" <c:if test="${voucher.active}">checked</c:if>> Active
            </label>
            <button type="submit">Save Voucher</button>
          </form>
        </div>
      </c:forEach>
    </div>
  </div>
</section>
</main>
</body>
</html>
