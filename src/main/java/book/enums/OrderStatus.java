package book.enums;

public enum OrderStatus {

	PENDING, // Đơn hàng đang chờ xử lý
	PAID, CONFIRMED, // Đã xác nhận
	SHIPPED, // Đang giao hàng
	DELIVERED, // Đã giao hàng
	CANCELED // Đã hủy
}
