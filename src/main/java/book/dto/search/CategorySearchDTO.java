package book.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategorySearchDTO {
	private String name;
	private int page = 0;
	private int size = 10;
	private String sortBy = "name";
	private String sortDirection = "asc";
}
