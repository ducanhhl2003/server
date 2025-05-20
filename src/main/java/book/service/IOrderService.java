package book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import book.controller.output.OrderOutput;
import book.dto.OrderDTO;
import book.entity.OrderEntity;

public interface IOrderService {
	OrderDTO createOrder(OrderDTO orderDTO);

	OrderDTO getOrder(Integer id);

	Page<OrderEntity> getOrdersByKeyword(String keyword, Pageable pageable);

	OrderOutput getOrderList(int page, int limit, String keyword);
	
	List<OrderDTO> getOrdersByUserId(Integer userId);

}
