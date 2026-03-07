<%@ include file="/WEB-INF/views/navbar.jspf" %>
<section class="hero hero-banner">
  <div class="hero-left">
    <h1>Super Sale Day</h1>
    <p>Up to 50% off + free shipping across the marketplace.</p>
    <div class="hero-actions">
      <a class="btn" href="${pageContext.request.contextPath}/products">Flash Sale 12H</a>
      <a class="btn ghost-btn" href="${pageContext.request.contextPath}/products?q=voucher">Voucher 200K</a>
    </div>
  </div>
  <div class="hero-right">
    <div class="hero-percent">50%</div>
    <div class="hero-sub">OFF</div>
  </div>
</section>

<section class="flash-sale">
  <div class="flash-title">FLASH SALE</div>
  <div class="flash-timer">
    <span>01</span><span>:</span><span>59</span><span>:</span><span>47</span>
  </div>
  <a class="flash-link" href="${pageContext.request.contextPath}/products?q=flash">See all →</a>
</section>

<section class="card">
  <div class="section-head">
    <div>
      <h2>Featured Products</h2>
      <c:if test="${not empty searchQuery}">
        <p class="muted">Search results for "${searchQuery}" (${searchCount})</p>
      </c:if>
      <c:if test="${empty searchQuery}">
        <p class="muted">Top-rated products first. Search to view all products.</p>
      </c:if>
    </div>
  </div>
  <c:if test="${not empty error}">
    <div class="alert error">${error}</div>
  </c:if>
  <div class="product-grid">
    <c:forEach var="p" items="${products}">
      <c:set var="variantJson" value="[" />
      <c:forEach var="v" items="${variantsByProduct[p.id]}" varStatus="vs">
        <c:set var="variantJson" value='${variantJson}{"id":${v.id},"label":"${v.color} / ${v.size}","delta":${v.priceDelta},"stock":${v.stock}}${vs.last ? "]" : ","}' />
      </c:forEach>
      <div class="product-card"
           data-id="${p.id}"
           data-name="${p.name}"
           data-shop="${p.shopName}"
           data-rating="${p.shopRating}"
           data-price="${p.basePrice}"
           data-image="${p.imageUrl}"
           data-variants='${variantJson}'>
        <div class="product-link quick-open">
          <div class="product-img" style="background-image:url('${p.imageUrl}');"></div>
          <div class="card-badge">-15%</div>
        </div>
        <div class="product-card-top">
          <div>
            <h3>${p.name}</h3>
            <p class="muted">${p.shopName}</p>
            <p class="rating">⭐ ${p.shopRating}</p>
          </div>
          <div class="price">Base ${p.basePrice}</div>
        </div>
        <p>${p.description}</p>
        <div class="product-actions">
          <button type="button" class="btn ghost-btn quick-open">Quick View</button>
        </div>
      </div>
    </c:forEach>
  </div>
</section>

<div id="quick-overlay" class="overlay hidden">
  <div class="overlay-card">
    <button type="button" class="close-btn" id="closeOverlay">×</button>
    <div class="overlay-grid">
      <div class="overlay-img"></div>
      <div class="overlay-info">
        <h2 id="ov-name"></h2>
        <p class="muted" id="ov-shop"></p>
        <div class="detail-price">
          <span class="price" id="ov-price"></span>
          <span class="detail-tag">-30%</span>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/cart" class="detail-form" id="ov-form">
          <label>Variant
            <select name="variantId" id="ov-variant"></select>
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
  </div>
</div>
</main>
<script>
  (function() {
    const overlay = document.getElementById('quick-overlay');
    const closeBtn = document.getElementById('closeOverlay');
    const ovName = document.getElementById('ov-name');
    const ovShop = document.getElementById('ov-shop');
    const ovPrice = document.getElementById('ov-price');
    const ovVariant = document.getElementById('ov-variant');
    const ovImg = document.querySelector('.overlay-img');

    function openOverlay(card) {
      ovName.textContent = card.dataset.name;
      ovShop.textContent = card.dataset.shop + ' • ⭐ ' + card.dataset.rating;
      ovPrice.textContent = card.dataset.price;
      while (ovVariant.firstChild) ovVariant.removeChild(ovVariant.firstChild);
      try {
        const variants = JSON.parse(card.dataset.variants || '[]');
        variants.forEach(v => {
          const opt = document.createElement('option');
          opt.value = v.id;
          opt.textContent = v.label + ' (+' + v.delta + ')';
          ovVariant.appendChild(opt);
        });
      } catch(e) {}
      if (ovImg) {
        ovImg.style.backgroundImage = card.dataset.image ? `url('${card.dataset.image}')` : '';
      }
      overlay.classList.remove('hidden');
    }

    document.addEventListener('click', (e) => {
      const btn = e.target.closest('.quick-open');
      if (btn) {
        const card = btn.closest('.product-card');
        if (card) openOverlay(card);
      }
    });

    closeBtn.addEventListener('click', () => overlay.classList.add('hidden'));
    overlay.addEventListener('click', (e) => {
      if (e.target === overlay) overlay.classList.add('hidden');
    });
  })();
</script>
