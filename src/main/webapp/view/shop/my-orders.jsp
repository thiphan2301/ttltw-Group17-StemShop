<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đơn hàng của tôi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
</head>
<body>

<jsp:include page="/WEB-INF/components/header.jsp"/>

<div class="container">
    <h2 style="text-align:center; margin: 30px 0;">Đơn hàng của tôi</h2>

    <div class="order-tabs">
        <button class="tab-btn active" onclick="filterOrders('ALL', this)">Tất cả</button>
        <button class="tab-btn" onclick="filterOrders('PENDING', this)">Chờ xác nhận</button>
        <button class="tab-btn" onclick="filterOrders('CONFIRMED', this)">Đang giao hàng</button>
        <button class="tab-btn" onclick="filterOrders('DELIVERED', this)">Đã giao</button>
        <button class="tab-btn" onclick="filterOrders('RETURNED', this)">Trả hàng</button>
        <button class="tab-btn" onclick="filterOrders('CANCELLED', this)">Đã hủy</button>
    </div>

    <c:forEach var="item" items="${orders}">
        <%-- Logic phân nhóm Tab --%>
        <c:set var="tabCategory" value="${item.orderStatus}" />
        <c:if test="${item.orderStatus == 'RETURN_PENDING' || item.orderStatus == 'RETURNED'}">
            <c:set var="tabCategory" value="RETURNED" />
        </c:if>
        <c:if test="${item.orderStatus == 'CANCEL_REQUESTED'}">
            <c:set var="tabCategory" value="PENDING" />
        </c:if>

        <div class="order-box" data-status="${tabCategory}">
            <div class="order-header">
                <span><strong>Đơn hàng #${item.orderId}</strong></span>
                <span class="order-status">
                    <c:choose>
                        <c:when test="${item.orderStatus == 'PENDING'}">Chờ xác nhận</c:when>
                        <c:when test="${item.orderStatus == 'CANCEL_REQUESTED'}">Đang chờ duyệt hủy</c:when>
                        <c:when test="${item.orderStatus == 'CONFIRMED'}">Đang giao hàng</c:when>
                        <c:when test="${item.orderStatus == 'DELIVERED'}">Đã giao</c:when>
                        <c:when test="${item.orderStatus == 'RETURN_PENDING'}">Chờ duyệt trả hàng</c:when>
                        <c:when test="${item.orderStatus == 'RETURNED'}">Đã trả hàng/hoàn tiền</c:when>
                        <c:when test="${item.orderStatus == 'CANCELLED'}">Đã hủy</c:when>
                        <c:otherwise>${item.orderStatus}</c:otherwise>
                    </c:choose>
                </span>
            </div>

            <div class="product-item">
                <img src="${pageContext.request.contextPath}/${item.imageUrl}" alt="${item.productName}">
                <div class="product-info">
                    <p><strong>${item.productName}</strong></p>
                    <p class="text-muted">Số lượng: ${item.quantity}</p>
                    <p>Thành tiền: <strong style="color: var(--accent-color);">
                        <fmt:formatNumber value="${item.price * item.quantity}" type="currency" currencySymbol="₫"/>
                    </strong></p>
                </div>
            </div>

            <div class="order-actions">
                <button class="action-btn btn-blue" onclick="openDetailPopup('${pageContext.request.contextPath}/orderDetails?id=${item.orderId}')">
                    Xem chi tiết
                </button>

                <c:if test="${item.orderStatus == 'PENDING'}">
                    <button class="action-btn btn-red" onclick="cancelOrder('${item.orderId}')">Hủy đơn</button>
                </c:if>

                <c:if test="${item.orderStatus == 'CONFIRMED'}">
                    <button class="action-btn btn-green" onclick="confirmReceived('${item.orderId}')">Đã nhận được hàng</button>
                </c:if>

                <c:if test="${item.orderStatus == 'DELIVERED'}">
                    <button class="action-btn btn-gray" onclick="requestReturn('${item.orderId}')">Trả hàng / Hoàn tiền</button>
                </c:if>
            </div>
        </div>
    </c:forEach>

    <div id="empty-filter-msg" style="text-align: center; color: #888; display: none; margin-top: 50px;">
        <i class="fa-regular international fa-clipboard-list" style="font-size: 50px; margin-bottom: 10px;"></i>
        <p>Không tìm thấy đơn hàng nào phù hợp.</p>
    </div>
</div>

<div id="orderDetailModal" class="custom-modal">
    <div class="modal-content">
        <span class="close-btn" onclick="closeDetailPopup()">&times;</span>
        <iframe id="detailFrame" src=""></iframe>
    </div>
</div>

<script>
    function filterOrders(status, btn) {
        // Cập nhật trạng thái nút Active
        document.querySelectorAll('.tab-btn').forEach(t => t.classList.remove('active'));
        btn.classList.add('active');

        let hasOrder = false;
        document.querySelectorAll('.order-box').forEach(box => {
            if (status === 'ALL' || box.getAttribute('data-status') === status) {
                box.style.display = 'block';
                hasOrder = true;
            } else {
                box.style.display = 'none';
            }
        });

        // Hiển thị thông báo trống nếu không có đơn nào
        document.getElementById('empty-filter-msg').style.display = hasOrder ? 'none' : 'block';
    }

    function openDetailPopup(url) {
        document.getElementById("detailFrame").src = url;
        document.getElementById("orderDetailModal").style.display = "block";
        document.body.style.overflow = 'hidden';
    }

    function closeDetailPopup() {
        document.getElementById("orderDetailModal").style.display = "none";
        document.getElementById("detailFrame").src = "";
        document.body.style.overflow = 'auto';
    }

    function confirmReceived(id) {
        if(confirm("Bạn chắc chắn đã nhận được kiện hàng này?")) {
            window.location.href = "${pageContext.request.contextPath}/confirmOrder?id=" + id;
        }
    }

    function cancelOrder(id) {
        if(confirm("Xác nhận hủy đơn hàng này?")) {
            window.location.href = "${pageContext.request.contextPath}/cancelOrder?id=" + id + "&type=direct";
        }
    }

    function requestReturn(id) {
        let reason = prompt("Lý do bạn muốn trả hàng:");
        if(reason && reason.trim() !== "") {
            window.location.href = "${pageContext.request.contextPath}/returnOrder?id=" + id + "&reason=" + encodeURIComponent(reason);
        }
    }

    // Đóng modal khi click ra ngoài vùng trắng
    window.onclick = function(e) {
        if (e.target == document.getElementById("orderDetailModal")) closeDetailPopup();
    }
</script>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>