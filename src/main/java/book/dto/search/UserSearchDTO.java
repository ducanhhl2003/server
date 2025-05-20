package book.dto.search;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSearchDTO {
	private Integer id;
	private String userName;
	private String fullName;
	private String email;
	private String phone;
	private String roleName;
	private String address;
	private List<Integer> roleGroupIds;
	private int page = 0;
	private int size = 10;
	private String sortBy = "userName";
	private String sortDirection = "asc";
}
