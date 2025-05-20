package book.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO extends AbstractDTO<CategoryDTO> {
	private Integer id;
	private Integer userId;
	private String fullName;
	private String email;
	private String phoneNumber;
	private String address;
	private Float price;
	private String note;
	private Date orderDate;
	private String status;
	private Float totalMoney;
	private String shippingMethod;
	private String shippingAddress;
	private LocalDate shippingDate;
	private String trackingNumber;
	private String paymentMethod;
	private List<OrderDetailDTO> orderDetails;
	private Set<CategoryDTO> categories;
	private List<CartItemDTO> cartItems;

	public List<String> getProductNames() {
		return orderDetails != null
				? orderDetails.stream().map(OrderDetailDTO::getProductName).collect(Collectors.toList())
				: List.of();
	}

}
