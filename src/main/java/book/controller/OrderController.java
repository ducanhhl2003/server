package book.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import book.controller.output.OrderOutput;
import book.dto.OrderDTO;
import book.dto.OrderDetailDTO;
import book.dto.ProductDTO;
import book.entity.OrderEntity;
import book.entity.ProductEntity;
import book.enums.OrderStatus;
import book.repository.OrderRepository;
import book.service.IOrderService;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private IOrderService orderService;
	@Autowired
	private OrderRepository orderRepository;

	@PostMapping
	public ResponseEntity<OrderDTO> createNew(@Valid @RequestBody OrderDTO model) {
		return ResponseEntity.ok(orderService.createOrder(model));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getOrder(@Valid @PathVariable("id") Integer orderId) {
		try {
			OrderDTO existingOrder = orderService.getOrder(orderId);
			return ResponseEntity.ok(existingOrder);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
	
	@GetMapping("/get-orders-by-keyword")
	public ResponseEntity<OrderOutput> showProduct(
	        @RequestParam("page") int page,
	        @RequestParam("limit") int limit,
//	        @RequestParam(required = false) Integer categoryId,
	        @RequestParam(value = "keyword", required = false) String keyword) { // Thêm tham số keyword

	    OrderOutput result = orderService.getOrderList(page, limit, keyword);
	    
	    return ResponseEntity.ok(result);
	}
	@PutMapping("/update-order-status/{orderId}")
	public ResponseEntity<?> updateOrderStatus(@PathVariable Integer orderId, @RequestParam String status) {
	    OrderEntity order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
	    
	    try {
	        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase()); // Chuyển String thành Enum
	        order.setStatus(orderStatus);
	        orderRepository.save(order);
	        return ResponseEntity.ok("Cập nhật trạng thái thành công!");
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body("Trạng thái không hợp lệ: " + status);
	    }
	}
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getOrdersByUserId(@PathVariable Integer userId) {
	    List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
	    return ResponseEntity.ok(orders);
	}

	






}
