package book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonSyntaxException;
import com.stripe.model.Event;
import book.entity.TransactionEntity;
import book.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {

	@Autowired
	private TransactionRepository transactionRepository;

	@PostMapping("/stripe")
	public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload) {
		Event event;

		try {
			event = Event.GSON.fromJson(payload, Event.class);
			log.info("📥 Nhận Webhook từ Stripe: Event ID = {}", event.getId());
		} catch (JsonSyntaxException e) {
			log.error("❌ Payload không hợp lệ!", e);
			return ResponseEntity.status(400).body("Invalid payload!");
		} catch (Exception e) {
			log.error("❌ Lỗi không xác định khi xử lý webhook!", e);
			return ResponseEntity.status(500).body("Internal Server Error");
		}

		// Xử lý sự kiện thanh toán hoàn tất
		if ("checkout.session.completed".equals(event.getType())) {
			try {
				// Lấy dữ liệu từ event
				if (event.getDataObjectDeserializer().getObject().isEmpty()) {
					log.warn("⚠️ Không thể parse dữ liệu từ webhook.");
					return ResponseEntity.status(400).body("Invalid event data!");
				}

				Map<String, Object> data = (Map<String, Object>) event.getData().getObject();

				// Kiểm tra xem key "id" có tồn tại không
				Object sessionIdObj = data.get("id");
				Object amountTotalObj = data.get("amount_total");

				if (sessionIdObj == null || amountTotalObj == null) {
					log.warn("⚠️ Thiếu dữ liệu từ webhook! sessionId={}, amountTotal={}", sessionIdObj, amountTotalObj);
					return ResponseEntity.status(400).body("Missing required fields");
				}

				String sessionId = sessionIdObj.toString();
				long amountTotal = ((Number) amountTotalObj).longValue();

				// Kiểm tra giao dịch trong database
				Optional<TransactionEntity> transactionOpt = transactionRepository.findByStripeSessionId(sessionId);
				if (transactionOpt.isPresent()) {
					TransactionEntity transaction = transactionOpt.get();

					if (transaction.getAmount() != amountTotal) {
						log.warn("⚠️ Số tiền không khớp! DB: {}, Stripe: {}", transaction.getAmount(), amountTotal);
						return ResponseEntity.status(400).body("Amount mismatch!");
					}

					// Cập nhật trạng thái thanh toán
					transaction.setPaymentStatus("PAID");
					transactionRepository.save(transaction);
					log.info("✅ Đã cập nhật giao dịch {} thành PAID!", transaction.getId());
					return ResponseEntity.ok("Success");
				} else {
					log.warn("⚠️ Không tìm thấy giao dịch với Session ID: {}", sessionId);
					return ResponseEntity.status(404).body("Transaction not found!");
				}
			} catch (Exception e) {
				log.error("❌ Lỗi khi xử lý dữ liệu webhook!", e);
				return ResponseEntity.status(400).body("Invalid event data!");
			}
		}

		return ResponseEntity.ok("Event ignored");
	}

}
