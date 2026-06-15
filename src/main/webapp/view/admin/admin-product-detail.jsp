<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<html>
<head>
    <title>Chi tiết sản phẩm</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/product-detail.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
</head>

<body>
<style>
    /* CSS được giữ nguyên từ code cũ của bạn, không thay đổi */
    .rating { display: flex; flex-direction: column; width: 30%; }
    .rating i { font-size: 2rem; color: #ec8b0b; }
    .rating label input { font-size: 2rem; }
    .form-reviews { display: flex; }
    .review__rate { width: 50%; }
    .review__comment { flex: 1; }
    .review__comment textarea { width: 100%; height: 35%; }
    .star i { color: #ec8b0b; }
    :root {
        --primary: #e91e63; --primary-hover: #c2185b; --secondary: #ff9800; --accent: #9c27b0;
        --success: #4caf50; --warning: #ff9800; --danger: #f44336; --text-dark: #333;
        --text-muted: #666; --text-light: #999; --bg-light: #f5f5f5; --bg-white: #fff;
        --border-color: #e0e0e0; --shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        --shadow-hover: 0 4px 16px rgba(0, 0, 0, 0.15); --radius: 8px; --radius-lg: 12px;
    }
    .reviews-section { padding: 60px 0; background: var(--bg-light); }
    .section-title { font-size: 28px; font-weight: 700; margin-bottom: 30px; color: var(--text-dark); }
    .reviews-grid { display: grid; grid-template-columns: 350px 1fr; gap: 40px; }
    .rating-summary { background: var(--bg-white); border-radius: var(--radius-lg); padding: 30px; box-shadow: var(--shadow); height: fit-content; position: sticky; top: 100px; }
    .rating-overview { text-align: center; padding-bottom: 25px; border-bottom: 1px solid var(--border-color); margin-bottom: 25px; }
    .average-rating { margin-bottom: 10px; }
    .rating-number { font-size: 48px; font-weight: 700; color: var(--text-dark); }
    .rating-max { font-size: 24px; color: var(--text-muted); }
    .average-stars { margin-bottom: 10px; }
    .average-stars .star { font-size: 24px; }
    .total-reviews { color: var(--text-muted); font-size: 14px; }
    .review-content { display: flex; flex-direction: column; gap: 30px; }
    .review-form-container { background: var(--bg-white); border-radius: var(--radius-lg); padding: 30px; box-shadow: var(--shadow); }
    .review-form-container h3 { font-size: 18px; font-weight: 600; margin-bottom: 20px; color: var(--text-dark); }
    .review-form { display: flex; flex-direction: column; gap: 20px; }
    .form-group { display: flex; flex-direction: column; gap: 8px; }
    .form-group label { font-size: 14px; font-weight: 500; color: var(--text-dark); }
    .form-group input, .form-group textarea { padding: 12px 15px; border: 1px solid var(--border-color); border-radius: var(--radius); font-size: 14px; font-family: inherit; transition: 0.3s; }
    .form-group input:focus, .form-group textarea:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px rgba(233, 30, 99, 0.1); }
    .form-group textarea { resize: vertical; min-height: 100px; }
    .star-rating-input { display: flex; gap: 5px; }
    .star-input { font-size: 28px; color: #ddd; cursor: pointer; transition: 0.2s; }
    .star-input:hover, .star-input.active { color: #ffc107; transform: scale(1.1); }
    .star-input.hover-fill { color: #ffc107; }
    .btn-submit-review { align-self: flex-start; margin: auto; padding: 10px; border-radius: 25px; }
    .btn-submit-review:hover { background-color: var(--primary); transition: 0.3s; }
    .review-list { background: var(--bg-white); border-radius: var(--radius-lg); padding: 30px; box-shadow: var(--shadow); }
    .review-list h3 { font-size: 18px; font-weight: 600; margin-bottom: 25px; color: var(--text-dark); }
    .review-item { padding: 20px 0; border-bottom: 1px solid var(--border-color); }
    .review-item:last-of-type { border-bottom: none; }
    .review-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
    .reviewer-info { display: flex; align-items: center; gap: 12px; }
    .reviewer-avatar { width: 44px; height: 44px; background: linear-gradient(135deg, var(--primary), var(--accent)); color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 600; font-size: 14px; }
    .reviewer-details { display: flex; flex-direction: column; }
    .reviewer-name { font-weight: 600; color: var(--text-dark); }
    .review-date { font-size: 12px; color: var(--text-light); }
    .review-rating .star { font-size: 14px; }
    .review-text { color: var(--text-muted); line-height: 1.7; margin-bottom: 12px; }
    .btn-load-more { width: 100%; margin-top: 20px; }
    .average-stars span i { color: #FFC107; }
    .active-heart { color: red !important; }
</style>

<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="back">
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <span>/</span>
        <a href="${pageContext.request.contextPath}/shop">Cửa hàng</a>
        <span>/</span>
        <a href="#">Chi tiết sản phẩm</a>
    </div>

    <div class="product-detail">
        <div class="product-images">
            <img class="main-image" id="main-product-img"
                 src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.productName}">

            <div class="thumbnail-images" style="display: flex; gap: 10px; margin-top: 15px;">
                <c:if test="${not empty product.subImages}">
                    <c:forEach var="imgUrl" items="${product.subImages}">
                        <img src="${pageContext.request.contextPath}/${imgUrl}"
                             class="thumb-item" onclick="changeImage(this.src)" onerror="this.style.display='none'"
                             style="width: 80px; height: 80px; object-fit: contain; cursor: pointer; border: 1px solid #ddd;">
                    </c:forEach>
                </c:if>
            </div>
        </div>

        <div class="product-info">
            <h1>${product.productName}</h1>
            <div class="product-price">
                <fmt:formatNumber value="${product.price}" pattern="#,###"/> Đ
            </div>
            <div class="product-description">
                <ul>
                    <li><i class="fa-solid fa-check-double"></i> Hàng chính hãng</li>
                    <li><i class="fa-solid fa-check-double"></i> Miễn phí giao hàng đơn trên 500K</li>
                    <li><i class="fa-solid fa-check-double"></i> Giao hàng hỏa tốc 4 tiếng</li>
                </ul>
            </div>

            <div class="product-actions">
                <button class="add-to-cart" type="button" onclick="addToCart(${product.id})">
                    Thêm vào giỏ hàng <i class="fa-solid fa-cart-plus"></i>
                </button>
                <button type="button" class="buyNow" onclick="buyNow(${product.id})"> Mua ngay </button>
            </div>

            <div class="product-meta">
                <p><strong>Thương hiệu:</strong> ${product.brandName}</p>
                <p><strong>Số lượng:</strong> ${product.quantity}</p>
                <p><strong>ID sản phẩm:</strong> ${product.id}</p>
            </div>
        </div>
    </div>

    <div class="productDecription">
        <div class="tabContent">
            <h2>Mô tả sản phẩm</h2>
            <p>${product.description}</p>
        </div>
    </div>

    <section class="reviews-section">
        <div class="container">
            <h2 class="section-title">Đánh giá sản phẩm</h2>
            <div class="reviews-grid">

                <div class="rating-summary">
                    <div class="rating-overview">
                        <div class="average-rating">
                            <span class="rating-number">
                                <fmt:formatNumber value="${avgRating}" maxFractionDigits="1"/>
                            </span>
                            <span class="rating-max">/ 5</span>
                        </div>
                        <div class="average-stars">
                            <c:forEach begin="1" end="5" var="i">
                                <span class="star ${i <= avgRating + 0.5 ? 'filled' : ''}"><i class="fa-solid fa-star"></i></span>
                            </c:forEach>
                        </div>
                        <p class="total-reviews">
                            Đánh giá trung bình: <fmt:formatNumber value="${avgRating}" maxFractionDigits="1"/> / 5
                            (${totalReviews} đánh giá)
                        </p>
                    </div>
                </div>

                <div class="review-content">
                    <div class="review-form-container">
                        <h3>Bình luận sản phẩm:</h3>
                        <c:choose>
                            <c:when test="${not empty sessionScope.user}">
                                <form class="review-form" action="${pageContext.request.contextPath}/add-review" method="post">
                                    <input type="hidden" name="productId" value="${product.id}">
                                    <input type="hidden" name="rating" id="ratingValue">
                                    <div class="form-group">
                                        <label>Đánh giá của bạn:</label>
                                        <div class="star-rating-input">
                                            <c:forEach begin="1" end="5" var="i">
                                                <span class="star-input" data-value="${i}"><i class="fa-solid fa-star"></i></span>
                                            </c:forEach>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label>Nhận xét của bạn:</label>
                                        <textarea name="comment" rows="4" placeholder="Chia sẻ cảm nhận của bạn..." required></textarea>
                                    </div>
                                    <button class="btn btn-primary btn-submit-review">Viết đánh giá</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <p>Vui lòng <a href="${pageContext.request.contextPath}/view/user/sign-in.jsp">đăng nhập</a> để đánh giá.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="review-list">
                        <h3>Đánh giá từ khách hàng (<span>${fn:length(reviews)}</span>)</h3>
                        <c:forEach var="r" items="${reviews}">
                            <div class="review-item">
                                <div class="review-header">
                                    <div class="reviewer-info">
                                        <c:choose>
                                            <c:when test="${empty r.avatar}">
                                                <div class="reviewer-avatar">
                                                        ${fn:toUpperCase(fn:substring(r.userName, 0, 2))}
                                                </div>
                                            </c:when>
                                            <c:when test="${fn:startsWith(r.avatar, 'http')}">
                                                <div class="reviewer-avatar" style="overflow: hidden; padding: 0;">
                                                    <img src="${r.avatar}" alt="avatar" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="reviewer-avatar" style="overflow: hidden; padding: 0;">
                                                    <img src="${pageContext.request.contextPath}/avatar/${r.avatar}" alt="avatar" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">
                                                </div>
                                            </c:otherwise>
                                        </c:choose>

                                        <div class="reviewer-details">
                                            <span class="reviewer-name">${r.userName}</span>
                                            <span class="review-date">
                                                <fmt:formatDate value="${r.createDate}" pattern="dd/MM/yyyy HH:mm" />
                                            </span>
                                        </div>
                                    </div>

                                    <div class="review-rating">
                                        <c:forEach begin="1" end="5" var="i">
                                            <span class="star">
                                                <i class="${i <= r.rating ? 'fa-solid' : 'fa-regular'} fa-star"></i>
                                            </span>
                                        </c:forEach>
                                    </div>
                                </div>
                                <p class="review-text">${r.comment}</p>
                            </div>
                        </c:forEach>
                        <c:if test="${fn:length(reviews) > 5}">
                            <button class="btn btn-outline btn-load-more">Xem thêm đánh giá</button>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>

<script>
    function changeImage(newSrc) {
        document.getElementById('main-product-img').src = newSrc;
    }

    const stars = document.querySelectorAll('.star-input');
    const ratingInput = document.getElementById('ratingValue');

    stars.forEach(star => {
        star.addEventListener('click', () => {
            const value = star.dataset.value;
            ratingInput.value = value;
            stars.forEach(s => s.classList.toggle('active', s.dataset.value <= value));
        });
        star.addEventListener('mouseover', () => {
            const value = star.dataset.value;
            stars.forEach(s => s.classList.toggle('hover-fill', s.dataset.value <= value));
        });
        star.addEventListener('mouseout', () => {
            stars.forEach(s => s.classList.remove('hover-fill'));
        });
    });

    function buyNow(productId) {
        window.location.href = "${pageContext.request.contextPath}/BuyNowServlet?productId=" + productId;
    }
</script>
</body>
</html>