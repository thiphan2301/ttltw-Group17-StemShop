// file: assets/js/admin-orders.js

document.addEventListener("DOMContentLoaded", function() {
    // ==========================================
    // KHAI BÁO BIẾN
    // ==========================================
    const searchInput = document.getElementById('searchInput');
    const statusFilter = document.getElementById('statusFilter');
    const sortFilter = document.getElementById('sortFilter');
    const tableBody = document.getElementById('orderTableBody');
    const actionForms = document.querySelectorAll('.admin-table form');

    // ==========================================
    // 1. CHỨC NĂNG TÌM KIẾM, LỌC VÀ SẮP XẾP
    // ==========================================

    // Lắng nghe sự kiện khi người dùng nhập hoặc chọn
    searchInput.addEventListener('input', applyFilters);
    statusFilter.addEventListener('change', applyFilters);
    sortFilter.addEventListener('change', applyFilters);

    function applyFilters() {
        const searchValue = searchInput.value.toLowerCase();
        const statusValue = statusFilter.value;
        const sortValue = sortFilter.value;

        // Lấy tất cả các hàng trong bảng thành 1 mảng để dễ xử lý
        let rows = Array.from(tableBody.querySelectorAll('tr'));

        // 1.1 Thực hiện LỌC (Filter)
        rows.forEach(row => {
            const idText = row.cells[0].textContent.toLowerCase();
            const nameText = row.cells[1].textContent.toLowerCase();
            const statusText = row.cells[3].textContent.trim(); // Lấy text trạng thái

            // Kiểm tra tìm kiếm
            const matchSearch = idText.includes(searchValue) || nameText.includes(searchValue);

            // Kiểm tra trạng thái
            let matchStatus = false;
            if (statusValue === 'ALL') {
                matchStatus = true;
            } else if (statusValue === 'PENDING' && statusText.includes('Chờ xác nhận')) {
                matchStatus = true;
            } else if (statusValue === 'SHIPPING' && statusText.includes('Đang giao')) {
                matchStatus = true;
            } else if (statusValue === 'DELIVERED' && statusText.includes('Đã giao')) {
                matchStatus = true;
            } else if (statusValue === 'CANCELLED' && statusText.includes('Đã hủy')) {
                matchStatus = true;
            }

            // Ẩn/Hiện dòng dựa trên kết quả lọc
            if (matchSearch && matchStatus) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        // 1.2 Thực hiện SẮP XẾP (Sort)
        rows.sort((a, b) => {
            // Tách số ID (Bỏ dấu #)
            const idA = parseInt(a.cells[0].textContent.replace('#', '').trim());
            const idB = parseInt(b.cells[0].textContent.replace('#', '').trim());

            if (sortValue === 'asc') {
                return idA - idB; // Cũ nhất -> Mới nhất (ID nhỏ -> lớn)
            } else {
                return idB - idA; // Mới nhất -> Cũ nhất (ID lớn -> nhỏ)
            }
        });

        // Gắn lại các hàng đã sắp xếp vào bảng
        rows.forEach(row => tableBody.appendChild(row));
    }

    // Chạy hàm 1 lần lúc trang vừa load xong để đảm bảo thứ tự ban đầu đúng thiết lập
    applyFilters();


    // ==========================================
    // 2. CHỨC NĂNG XỬ LÝ NÚT BẤM (KHÔNG RESET TRANG)
    // ==========================================
    actionForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            // Ngăn chặn hành vi reload trang mặc định của thẻ <form>
            e.preventDefault();

            // Lấy dữ liệu từ các input ẩn trong form vừa bấm (orderId, action)
            const formData = new FormData(this);
            const action = formData.get('action');
            const orderId = formData.get('orderId');

            // --- Xử lý riêng cho nút "Chi tiết" ---
            if (action === 'detail') {
                console.log('Xem chi tiết đơn hàng ID:', orderId);
                alert('Đang mở chi tiết cho đơn hàng #' + orderId + ' (Không bị reload trang!)');
                // Tại đây bạn có thể viết thêm code để mở Modal Bootstrap hoặc gọi API lấy sản phẩm chi tiết...
                return; // Dừng xử lý tại đây, không gửi request POST đi nơi khác
            }

            // --- Xử lý cho nút "Xác nhận" và "Hủy" ngầm bằng AJAX ---
            const url = this.getAttribute('action') || window.location.href;

            fetch(url, {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        alert('Thành công! Đã xử lý ' + (action === 'confirm' ? 'XÁC NHẬN' : 'HỦY') + ' đơn hàng #' + orderId);

                        // Cập nhật giao diện lập tức (Ẩn form nút vừa bấm để tránh bấm lại)
                        this.style.display = 'none';

                        // Tìm dòng (row) hiện tại để đổi chữ trạng thái đơn hàng tương ứng
                        const row = this.closest('tr');
                        const statusCell = row.cells[3]; // Cột Trạng thái đơn hàng

                        if (action === 'confirm') {
                            statusCell.innerHTML = '<i class="fa-solid fa-truck-arrow-right"></i> Đang giao';
                            // Ẩn luôn nút Hủy đi kèm bên cạnh nếu có
                            const cancelForm = row.querySelector('input[value="cancel"]')?.closest('form');
                            if (cancelForm) cancelForm.style.display = 'none';
                        } else if (action === 'cancel') {
                            statusCell.innerHTML = '<i class="fa-regular fa-circle-xmark"></i> Đã hủy';
                            // Ẩn luôn nút Xác nhận đi kèm bên cạnh nếu có
                            const confirmForm = row.querySelector('input[value="confirm"]')?.closest('form');
                            if (confirmForm) confirmForm.style.display = 'none';
                        }
                    } else {
                        alert('Lỗi máy chủ! Không thể cập nhật trạng thái đơn hàng.');
                    }
                })
                .catch(error => {
                    console.error('Lỗi kết nối fetch:', error);
                    alert('Không thể kết nối tới máy chủ, vui lòng kiểm tra mạng.');
                });
        });
    });
});