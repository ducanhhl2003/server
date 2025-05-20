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
public class ProductImageDTO extends AbstractDTO<CategoryDTO> {
	private Integer id;
	private String imageUrl;
	private Integer productId;
	
	public ProductImageDTO(Integer id,String imageUrl)
	{
		this.id = id;
		this.imageUrl = imageUrl;
	}

}
