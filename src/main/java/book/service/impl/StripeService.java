package book.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import book.dto.OrderDTO;
import book.entity.TransactionEntity;
import book.repository.TransactionRepository;
import book.service.IStripeService;

@Service
public class StripeService implements IStripeService {

	@Value("${stripe.secret-key}")
	private String stripeApiKey;

	@Value("${stripe.success.url}")
	private String successUrl;

	@Value("${stripe.cancel.url}")
	private String cancelUrl;

	@Value("${stripe.exchange-rate}")
	private double exchangeRate;

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public String createCheckoutSession(OrderDTO order) {
		if (order.getTotalMoney() == null || order.getTotalMoney() <= 0) {
			throw new IllegalArgumentException("Tổng tiền đơn hàng không hợp lệ!");
		}

		Stripe.apiKey = stripeApiKey;

		try {
			long amountInUsdCents = (long) ((order.getTotalMoney() / exchangeRate) * 100);
			String productNames = String.join(", ", order.getProductNames());

			SessionCreateParams params = SessionCreateParams.builder().setMode(SessionCreateParams.Mode.PAYMENT)
					.setSuccessUrl(successUrl).setCancelUrl(cancelUrl)
					.addLineItem(
							SessionCreateParams.LineItem.builder().setQuantity(1L)
									.setPriceData(SessionCreateParams.LineItem.PriceData.builder().setCurrency("usd")
											.setUnitAmount(amountInUsdCents)
											.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
													.setName("Đơn hàng #" + order.getId() + " - "
															+ order.getTotalMoney() + " VND" + " - " + productNames)
													.build())
											.build())
									.build())
					.build();

			Session session = Session.create(params);
			TransactionEntity transaction = TransactionEntity.builder()
//                    .orderId(order.getId().toString())
					.stripeSessionId(session.getId()).paymentStatus("PENDING").paymentMethod("Stripe")
					.amount(order.getTotalMoney().doubleValue()).currency("VND").customerEmail(order.getEmail())
					.build();

			transactionRepository.save(transaction);
			return session.getUrl();

		} catch (StripeException e) {
			throw new RuntimeException("Lỗi khi tạo session thanh toán: " + e.getMessage(), e);
		}
	}

}
