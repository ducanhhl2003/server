package book.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostSearchDTO {
	private String title;
	private String content;
	private int page = 0;
	private int size = 10;
	private String sortBy = "title";
	private String sortDirection = "asc";
}
