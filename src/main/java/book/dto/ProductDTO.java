package book.dto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import book.entity.ProductEntity;
import jakarta.persistence.Column;
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
public class ProductDTO extends AbstractDTO<CategoryDTO> {
	private Integer id;
//	@NotBlank(message = "Mã sách không được để trống")
//	@Size(max = 350, message = "Tên sản phẩm không được vượt quá 350 ký tự")
	private String name;

	private Float price;
	private String thumbnail;
	private Boolean ishot;
//	@NotBlank(message = "Mô tả sách không được để trống")
	private String description;
	private Set<String> categoryNames;
	private Set<Integer> categoryIds;
	private List<MultipartFile> files;

	private List<ProductImageDTO> images;

	public ProductDTO(ProductEntity product) {
		this.id = product.getId();
		this.name = product.getName();
		this.thumbnail = product.getThumbnail();
		this.price = product.getPrice();
		this.description = product.getDescription();

		if (product.getCategories() != null) {
			this.categoryNames = product.getCategories().stream().map(category -> category.getName())
					.collect(Collectors.toSet());
			this.categoryIds = product.getCategories().stream()
			        .map(category -> category.getId())
			        .collect(Collectors.toSet());


		}
	}

	public String getCategoryName() {
		return (categoryNames != null && !categoryNames.isEmpty()) ? categoryNames.iterator().next() : null;
	}

	public void setCategoryName(String categoryName) {
		this.categoryNames = Set.of(categoryName);
	}
	public Integer getCategoryId() {
		return (categoryIds != null && !categoryIds.isEmpty()) ? categoryIds.iterator().next() : null;
	}

	public void setCategoryId(String categoryId) {
	    if (this.categoryIds == null) {
	        this.categoryIds = new HashSet<>();
	    }
	    Arrays.stream(categoryId.split(", ")) // Tách chuỗi thành danh sách
	          .map(Integer::parseInt) // Chuyển đổi từng phần tử thành Integer
	          .forEach(this.categoryIds::add); // Thêm vào tập hợp
	}
}
