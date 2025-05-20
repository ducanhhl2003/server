package book.dto;

import java.util.Set;
import java.util.stream.Collectors;

import book.entity.BookEntity;
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
public class BookDTO extends AbstractDTO<BookDTO> {
	private Integer id;
	@NotBlank(message = "Mã sách không được để trống")
	@Size(max = 50, message = "Mã sách không được vượt quá 50 ký tự")
	private String code;
	@NotBlank(message = "Tiêu đề sách không được để trống")
	@Size(max = 255, message = "Tiêu đề sách không được vượt quá 255 ký tự")
	private String title;
	@NotBlank(message = "Tên tác giả không được để trống")
	@Size(max = 100, message = "Tên tác giả không được vượt quá 100 ký tự")
	private String author;
	private Set<String> categories;

	public BookDTO(BookEntity book) {
		this.id = book.getId();
		this.code = book.getCode();
		this.title = book.getTitle();
		this.author = book.getAuthor();

		if (book.getCategories() != null) {
			this.categories = book.getCategories().stream().map(category -> category.getName())
					.collect(Collectors.toSet());
		}
	}

	public String getCategoryName() {
		return (categories != null && !categories.isEmpty()) ? categories.iterator().next() : null;
	}

	public void setCategoryName(String categoryName) {
		this.categories = Set.of(categoryName);
	}
}
