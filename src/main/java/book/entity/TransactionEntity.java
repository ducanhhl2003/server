package book.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orderId; // ID đơn hàng
	private String stripeSessionId; // Mã session Stripe
	private String paymentStatus; // Trạng thái thanh toán (PAID, PENDING, FAILED)
	private String paymentMethod; // Phương thức thanh toán (Card, Stripe)
	private Double amount; // Số tiền đã thanh toán
	private String currency; // Loại tiền tệ (USD, VND)
	private String customerEmail; // Email khách hàng
	private LocalDateTime createdAt; // Thời gian tạo giao dịch

	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now();
	}
}
