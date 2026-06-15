
// Tự động cập nhật phsi ship khi chọn Tỉnh/Thành phố
document.addEventListener("DOMContentLoaded", function () {
    // Lấy thẻ select chọn thành phố
    const citySelect = document.getElementById("city");

    // Lắng nghe sự kiện khi người dùng đổi Tỉnh/Thành phố
    if (citySelect) {
        citySelect.addEventListener("change", function () {
            // Tự động thêm một input ẩn báo cho Servlet biết đây là hành động cập nhật phí ship
            const form = document.querySelector(".checkout-form");
            const hiddenAction = document.createElement("input");
            hiddenAction.type = "hidden";
            hiddenAction.name = "action";
            hiddenAction.value = "updateShipping";
            form.appendChild(hiddenAction);

            // Submit form
            form.submit();
        });
    }
});
