package book.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSearchDTO {
	private String name;
	private String description;
	private Float price;
	private String categoryName;
	private int page = 0;
	private int size = 10;
	private String sortBy = "name";
	private String sortDirection = "asc";
}
