document.addEventListener("DOMContentLoaded", function() {
    // ------------------------------------------
    // 1. TÌM KIẾM VÀ LỌC
    // ------------------------------------------
    const searchInput = document.getElementById('searchInput');
    const statusFilter = document.getElementById('statusFilter');
    const sortFilter = document.getElementById('sortFilter');
    const tableBody = document.getElementById('orderTableBody');

    if (searchInput && statusFilter && sortFilter) {
        searchInput.addEventListener('input', applyFilters);
        statusFilter.addEventListener('change', applyFilters);
        sortFilter.addEventListener('change', applyFilters);
        applyFilters(); // Chạy lần đầu
    }

    function applyFilters() {
        const searchValue = searchInput.value.toLowerCase();
        const statusValue = statusFilter.value;
        const sortValue = sortFilter.value;
        let rows = Array.from(tableBody.querySelectorAll('tr'));

        rows.forEach(row => {
            const idText = row.cells[0].textContent.toLowerCase();
            const nameText = row.cells[1].textContent.toLowerCase();
            const statusText = row.cells[3].textContent.trim();

            const matchSearch = idText.includes(searchValue) || nameText.includes(searchValue);
            let matchStatus = false;
            if (statusValue === 'ALL') matchStatus = true;
            else if (statusValue === 'PENDING' && statusText.includes('Chờ xác nhận')) matchStatus = true;
            else if (statusValue === 'SHIPPING' && statusText.includes('Đang giao')) matchStatus = true;
            else if (statusValue === 'DELIVERED' && statusText.includes('Đã giao')) matchStatus = true;
            else if (statusValue === 'CANCELLED' && statusText.includes('Đã hủy')) matchStatus = true;

            row.style.display = (matchSearch && matchStatus) ? '' : 'none';
        });

        rows.sort((a, b) => {
            const idA = parseInt(a.cells[0].textContent.replace('#', '').trim());
            const idB = parseInt(b.cells[0].textContent.replace('#', '').trim());
            return sortValue === 'asc' ? (idA - idB) : (idB - idA);
        });

        rows.forEach(row => tableBody.appendChild(row));
    }

    // ------------------------------------------
    // 2. XỬ LÝ NÚT BẤM (AJAX) & MODAL CHI TIẾT
    // ------------------------------------------
    const actionForms = document.querySelectorAll('.admin-table form');
    const modal = document.getElementById('orderDetailModal');
    const modalBody = document.getElementById('modalOrderBody');
    const closeBtn = document.getElementById('closeModalBtn');

    actionForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(this);
            const action = formData.get('action');
            const orderId = formData.get('orderId');

            // NẾU LÀ NÚT CHI TIẾT (Mở Modal)
            if (action === 'detail') {
                document.getElementById('modalTitleOrderId').innerText = orderId;
                modal.classList.add('show');
                modalBody.innerHTML = '<div class="loader"></div><p style="text-align:center;">Đang tải dữ liệu...</p>';

                // ĐÃ SỬA TẠI ĐÂY: Tự động ghép thêm Context Path từ file JSP truyền sang
                const projectPath = window.contextPath || '';

                fetch(projectPath + '/admin/order-detail?id=' + orderId)
                    .then(response => {
                        if (!response.ok) throw new Error('Network fail');
                        return response.text();
                    })
                    .then(html => {
                        modalBody.innerHTML = html;
                    })
                    .catch(error => {
                        console.error('Lỗi tải chi tiết:', error);
                        modalBody.innerHTML = '<p style="color:red; text-align:center;">Lỗi tải dữ liệu. Vui lòng kiểm tra Server.</p>';
                    });
                return;
            }

            // NẾU LÀ NÚT XÁC NHẬN / HỦY (Cập nhật ngầm)
            const url = this.getAttribute('action') || window.location.href;
            fetch(url, {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        alert('Thành công! Đã xử lý ' + (action === 'confirm' ? 'XÁC NHẬN' : 'HỦY') + ' đơn hàng #' + orderId);
                        this.style.display = 'none';

                        const row = this.closest('tr');
                        const statusCell = row.cells[3];
                        if (action === 'confirm') {
                            statusCell.innerHTML = '<i class="fa-solid fa-truck-arrow-right"></i> Đang giao';
                            const cancelForm = row.querySelector('input[value="cancel"]')?.closest('form');
                            if (cancelForm) cancelForm.style.display = 'none';
                        } else if (action === 'cancel') {
                            statusCell.innerHTML = '<i class="fa-regular fa-circle-xmark"></i> Đã hủy';
                            const confirmForm = row.querySelector('input[value="confirm"]')?.closest('form');
                            if (confirmForm) confirmForm.style.display = 'none';
                        }
                    } else {
                        alert('Lỗi cập nhật trạng thái đơn hàng.');
                    }
                })
                .catch(error => alert('Lỗi kết nối tới máy chủ.'));
        });
    });

    // --- Đóng Modal ---
    if(closeBtn) {
        closeBtn.addEventListener('click', () => modal.classList.remove('show'));
    }
    window.addEventListener('click', (e) => {
        if (e.target === modal) modal.classList.remove('show');
    });

});