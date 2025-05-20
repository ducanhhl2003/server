package book.dto;

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
public class OrderDetailDTO extends AbstractDTO<CategoryDTO> {
	private Integer id;
	private Float price; // Giá của sản phẩm
	private Integer numberOfProducts; // Số lượng sản phẩm
	private Float totalMoney; // Tổng tiền của mục này
	private Integer orderId;
	private Integer productId;
//    private String productName;
	private ProductDTO product;

	public String getProductName() {
		return product != null ? product.getName() : "Sản phẩm không tồn tại";
	}
}
