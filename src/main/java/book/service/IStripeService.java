package book.service;

import book.dto.OrderDTO;

public interface IStripeService {
	String createCheckoutSession(OrderDTO order);
}
