package book.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserPageResponse {
	@JsonProperty(value = "user_list")
	private List<UserResponse> userResponses;

	@JsonProperty(value = "total_pages")
	private int totalPages;
}
