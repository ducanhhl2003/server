package book.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import book.controller.output.OrderOutput;
import book.dto.CartItemDTO;
import book.dto.OrderDTO;
import book.dto.OrderDetailDTO;
import book.dto.ProductDTO;
import book.entity.OrderDetailEntity;
import book.entity.OrderEntity;
import book.entity.ProductEntity;
import book.entity.UserEntity;
import book.enums.OrderStatus;
import book.exception.DataNotFoundException;
import book.exception.NotFoundException;
import book.repository.OrderDetailRepository;
import book.repository.OrderRepository;
import book.repository.ProductRepository;
import book.repository.UserRepository;
import book.service.IOrderService;
import book.utils.MessageKeys;
import jakarta.transaction.Transactional;

@Service
public class OrderService implements IOrderService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderDetailRepository orderDetailRepository;

	@Override
	@Transactional
	public OrderDTO createOrder(OrderDTO orderDTO) {
		UserEntity user = userRepository.findById(orderDTO.getUserId())
				.orElseThrow(() -> new DataNotFoundException(MessageKeys.ORDER_NOT_FOUND, orderDTO.getUserId()));

		modelMapper.typeMap(OrderDTO.class, OrderEntity.class).addMappings(mapper -> mapper.skip(OrderEntity::setId));

		OrderEntity order = modelMapper.map(orderDTO, OrderEntity.class);
		order.setUser(user);
		order.setOrderDate(new Date());
		order.setStatus(OrderStatus.PENDING);

		LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();

		if (shippingDate.isBefore(LocalDate.now())) {
			throw new NotFoundException("Date must be at least today!");
		}

		order.setTotalMoney(orderDTO.getTotalMoney());
		orderRepository.save(order);

		List<OrderDetailEntity> orderDetails = new ArrayList<>();
		for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
			OrderDetailEntity orderDetail = new OrderDetailEntity();
			orderDetail.setOrder(order);

			Integer productId = cartItemDTO.getProductId();
			int quantity = cartItemDTO.getQuantity();

			ProductEntity product = productRepository.findById(productId)
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.PRODUCT_NOT_FOUND));

			orderDetail.setProduct(product);
			orderDetail.setNumberOfProducts(quantity);
			orderDetail.setPrice(product.getPrice());

			orderDetails.add(orderDetail);
		}
		orderDetailRepository.saveAll(orderDetails);

		// Chuyển đổi từ OrderEntity sang OrderDTO trước khi trả về
		return modelMapper.map(order, OrderDTO.class);
	}

	@Override
	public OrderDTO getOrder(Integer id) {
		OrderEntity orderEntity = orderRepository.findById(id).orElse(null);

		if (orderEntity == null) {
			throw new DataNotFoundException(MessageKeys.ORDER_NOT_FOUND, id);
		}

		return modelMapper.map(orderEntity, OrderDTO.class);
	}

	@Override
	public Page<OrderEntity> getOrdersByKeyword(String keyword, Pageable pageable) {
		return orderRepository.findByKeyword(keyword, pageable);
	}

	@Override
	public OrderOutput getOrderList(int page, int limit, String keyword) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		Page<OrderEntity> orderPage = orderRepository.findByKeyword(keyword, pageable);

		List<OrderDTO> orders = orderPage.getContent().stream().map(order -> {
			OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

			// Chuyển đổi danh sách category nếu cần
//	        orderDTO.setCategories(order.getCategories()
//	                .stream().map(category -> new CategoryDTO(category.getId(), category.getName()))
//	                .collect(Collectors.toSet()));

			return orderDTO;
		}).collect(Collectors.toList());

		// Tạo đối tượng kết quả
		OrderOutput result = new OrderOutput();
		result.setPage(page);
		result.setListResult(orders);
		result.setTotalPage(orderPage.getTotalPages());

		return result;
	}
	@Override
	public List<OrderDTO> getOrdersByUserId(Integer userId) {
	    List<OrderEntity> orders = orderRepository.findByUserId(userId);
	    if (orders.isEmpty()) {
	        throw new RuntimeException("Không tìm thấy đơn hàng nào cho userId = " + userId);
	    }

	    return orders.stream().map(order -> {
	        OrderDTO dto = OrderDTO.builder()
	            .id(order.getId())
	            .userId(order.getUser().getId())
	            .fullName(order.getUser().getFullName())
	            .email(order.getUser().getEmail())
	            .phoneNumber(order.getUser().getPhone())
	            .address(order.getUser().getAddress())
	            .price(order.getPrice())
	            .note(order.getNote())
	            .orderDate(order.getOrderDate())
	            .status(order.getStatus().name())
	            .totalMoney(order.getTotalMoney())
	            .shippingMethod(order.getShippingMethod())
	            .trackingNumber(order.getTrackingNumber())
	            .paymentMethod(order.getPaymentMethod())
	            .build();

	        List<OrderDetailDTO> detailDTOs = order.getOrderDetails().stream().map(detail -> {
	            ProductEntity product = detail.getProduct();

	            return OrderDetailDTO.builder()
	                .id(detail.getId())
	                .orderId(order.getId())
	                .productId(product.getId())
	                .price(detail.getPrice())
	                .numberOfProducts(detail.getNumberOfProducts())
	                .totalMoney(detail.getTotalMoney())
	                .product(ProductDTO.builder()
	                    .id(product.getId())
	                    .name(product.getName())
	                    .price(product.getPrice())
	                    .thumbnail(product.getThumbnail())
	                    .description(product.getDescription())
	                    .build())
	                .build();
	        }).collect(Collectors.toList());

	        dto.setOrderDetails(detailDTOs);
	        return dto;
	    }).collect(Collectors.toList());
	}



}
