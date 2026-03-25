<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="detail card">
  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>
  <c:if test="${param.review == 'success'}">
    <div class="alert success">Your review has been saved.</div>
  </c:if>
  <c:if test="${not empty param.reviewError}">
    <div class="alert error">${param.reviewError}</div>
  </c:if>
  <c:if test="${not empty product}">
    <div class="detail-grid">
      <div class="detail-media">
        <div class="detail-badge">Flash Deal</div>
        <div class="detail-img"></div>
      </div>

      <div class="detail-info">
        <h1>${product.name}</h1>
        <p class="muted">${product.shopName} | Rating: ${product.shopRating}</p>
        <div class="detail-price">
          <span class="price">${product.basePrice}</span>
          <span class="detail-tag">Hot</span>
        </div>
        <div class="pill-row">
          <span class="pill">Official Store</span>
          <span class="pill">7-Day Return</span>
          <span class="pill">COD Available</span>
        </div>
        <div class="detail-desc">${product.description}</div>

        <form method="post" action="${pageContext.request.contextPath}/cart" class="detail-form">
          <label>Variant
            <select name="variantId">
              <c:forEach var="v" items="${variants}">
                <option value="${v.id}">${v.color} / ${v.size} (+${v.priceDelta}) - Stock: ${v.stock}</option>
              </c:forEach>
            </select>
          </label>

          <div class="stock-list">
            <c:forEach var="v" items="${variants}">
              <div class="stock-row">${v.color} / ${v.size}: <strong>${v.stock}</strong></div>
            </c:forEach>
          </div>

          <label>Quantity
            <input type="number" name="quantity" value="1" min="1" class="qty">
          </label>

          <input type="hidden" name="action" value="add">
          <div class="detail-actions">
            <button type="submit" class="btn">Add to Cart</button>
            <a class="btn ghost-btn" href="${pageContext.request.contextPath}/checkout">Buy Now</a>
          </div>
        </form>

        <c:if test="${not empty vouchers}">
          <div class="voucher-panel">
            <h2>Available Vouchers</h2>
            <c:forEach var="voucher" items="${vouchers}">
              <div class="voucher-item">
                <div>
                  <strong>${voucher.code}</strong>
                  <p class="muted">
                    <c:choose>
                      <c:when test="${voucher.discountType == 'PERCENT'}">
                        Discount ${voucher.discountValue}% up to ${voucher.maxDiscount}
                      </c:when>
                      <c:otherwise>
                        Discount ${voucher.discountValue}
                      </c:otherwise>
                    </c:choose>
                    | Min order ${voucher.minOrder}
                  </p>
                </div>
                <a class="btn ghost-btn" href="${pageContext.request.contextPath}/checkout?voucher=${voucher.code}">Use at checkout</a>
              </div>
            </c:forEach>
          </div>
        </c:if>
      </div>
    </div>

    <section class="review-section">
      <div class="section-head">
        <h2>Ratings & Comments</h2>
        <p class="muted">Only customers who purchased this product can leave a review.</p>
      </div>

      <c:if test="${sessionScope.user != null && canReview}">
        <div class="card inset-card">
          <h2>${myReview != null ? 'Update your review' : 'Write a review'}</h2>
          <form method="post" action="${pageContext.request.contextPath}/review" class="form">
            <input type="hidden" name="productId" value="${product.id}">
            <label>Rating
              <select name="rating">
                <option value="5" ${myReview != null && myReview.rating == 5 ? 'selected' : ''}>5 - Excellent</option>
                <option value="4" ${myReview != null && myReview.rating == 4 ? 'selected' : ''}>4 - Good</option>
                <option value="3" ${myReview != null && myReview.rating == 3 ? 'selected' : ''}>3 - Normal</option>
                <option value="2" ${myReview != null && myReview.rating == 2 ? 'selected' : ''}>2 - Weak</option>
                <option value="1" ${myReview != null && myReview.rating == 1 ? 'selected' : ''}>1 - Poor</option>
              </select>
            </label>
            <label>Comment
              <input type="text" name="comment" value="${myReview != null ? myReview.comment : ''}" maxlength="500" required>
            </label>
            <button type="submit">Save Review</button>
          </form>
        </div>
      </c:if>

      <c:if test="${sessionScope.user != null && !canReview}">
        <div class="alert">Buy this product first to leave a verified review.</div>
      </c:if>

      <c:choose>
        <c:when test="${not empty reviews}">
          <div class="review-list">
            <c:forEach var="review" items="${reviews}">
              <article class="review-card">
                <div class="review-head">
                  <strong>${review.userName}</strong>
                  <span class="pill">${review.rating}/5</span>
                </div>
                <p>${review.comment}</p>
                <p class="muted">${review.updatedAt}</p>
              </article>
            </c:forEach>
          </div>
        </c:when>
        <c:otherwise>
          <div class="alert">No reviews yet.</div>
        </c:otherwise>
      </c:choose>
    </section>
  </c:if>
</section>
</main>
</body>
</html>
