<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý vận chuyển</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .btn-success { background: #4CAF50; color: white; padding: 5px 10px; border: none; cursor: pointer; }
    </style>
</head>
<body>
<h1>Quản lý vận chuyển - Đơn hàng đang giao</h1>

<c:if test="${empty shippingOrders}">
    <p style="color:red;">Không có đơn hàng nào đang giao (shippingOrders rỗng hoặc null)</p>
</c:if>

<c:if test="${not empty shippingOrders}">
    <p><strong>Số lượng đơn hàng: ${shippingOrders.size()}</strong></p>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Người nhận</th>
            <th>SĐT</th>
            <th>PT thanh toán</th>
            <th>TT thanh toán</th>
            <th>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="o" items="${shippingOrders}">
            <tr>
                <td>${o.id}</td>
                <td>${o.receiverName}</td>
                <td>${o.receiverPhone}</td>
                <td>
                    <c:choose>
                        <c:when test="${o.paymentMethodId == 1}">COD</c:when>
                        <c:otherwise>VNPAY</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${o.paymentStatus == 'paid'}"><span style="color:green;">Đã thanh toán</span></c:when>
                        <c:when test="${o.paymentStatus == 'unpaid'}"><span style="color:orange;">Chưa thanh toán</span></c:when>
                        <c:otherwise><span style="color:red;">${o.paymentStatus}</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <form method="post" style="display:inline;">
                        <input type="hidden" name="orderId" value="${o.id}">
                        <input type="hidden" name="action" value="delivered">
                        <button type="submit" class="btn-success" onclick="return confirm('Xác nhận đã giao hàng cho đơn #${o.id}?')">Xác nhận đã giao</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>

<p><a href="${pageContext.request.contextPath}/admin/dashboard">Về Dashboard</a></p>
</body>
</html>