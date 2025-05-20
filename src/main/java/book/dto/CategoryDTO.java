package book.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO extends AbstractDTO<CategoryDTO> {
	private Integer id;
	@NotBlank(message = "Mã danh mục không được để trống")
	@Size(max = 50, message = "Mã danh mục không được vượt quá 50 ký tự")
	private String code;
	@NotBlank(message = "Tên danh mục không được để trống")
	@Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự")
	private String categoryName;
	private Boolean categoryStatus;
	private Set<String> books;

	public String getBook() {
		return (books != null && !books.isEmpty()) ? books.iterator().next() : null;
	}

	public void setBook(String book) {
		this.books = Set.of(book);
	}
}
