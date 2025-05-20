package book.dto;

import java.util.Set;
import java.util.stream.Collectors;

import book.entity.PostEntity;
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
public class PostDTO extends AbstractDTO<PostDTO> {
	private Integer id;
	@NotBlank(message = "Tiêu đề bài viết không được để trống")
	@Size(min = 5, max = 100, message = "Tiêu đề phải có từ 5 đến 100 ký tự")
	private String title;
	@NotBlank(message = "Nội dung bài viết không được để trống")
	@Size(min = 10, message = "Nội dung phải có ít nhất 10 ký tự")
	private String content;
	private Integer likes;
	@NotBlank(message = "Tên người dùng không được để trống")
	private String userName;
	private Set<String> comments;

	public PostDTO(PostEntity post) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.content = post.getContent();
		this.likes = post.getLikes();
		this.userName = post.getUser() != null ? post.getUser().getUserName() : null;

		if (post.getComments() != null) {
			this.comments = post.getComments().stream().map(comment -> comment.getContent())
					.collect(Collectors.toSet());
		}
	}
}
