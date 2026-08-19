const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);
stompClient.debug = null;
stompClient.connect({}, function () {
    console.log('Employee WebSocket connected');
    stompClient.subscribe(
        '/topic/payment/cash',
        function (message) {
            const request = JSON.parse(message.body);
            showCashPaymentToast(request);
        }
    );
}, function (error) {
    console.error('WebSocket connection error:', error);

});

function showCashPaymentToast(request) {
    const container =
        document.getElementById('payment-toast-container');
    if (!container) {
        return;
    }
    const toast = document.createElement('div');
    toast.className = 'payment-toast';
    toast.innerHTML = `
        <div class="payment-toast-title">
            🔔 Yêu cầu thanh toán
        </div>
        <div class="payment-toast-message">
            Bàn <strong>${request.tableNumber}</strong>
            yêu cầu thanh toán tiền mặt.
        </div>
        <div class="payment-toast-time">
            Nhấn để xem chi tiết đơn hàng
        </div>
    `;
    toast.addEventListener('click', function () {
        toast.remove();
        window.location.href =
            '/employee/orders/' + request.orderId;
    });
    container.appendChild(toast);
}