package book.service;

import java.io.ByteArrayInputStream;

import org.springframework.data.domain.Page;

import book.controller.output.UserOutput;
import book.dto.UserDTO;
import book.dto.search.UserSearchDTO;

public interface IUserService {
	UserDTO save(UserDTO userDTO);

	void delete(Integer[] ids);

	Page<UserDTO> searchUsers(UserSearchDTO request);

	UserOutput getUserList(int page, int limit);

	String searchUsersResponse(UserSearchDTO request);

	ByteArrayInputStream exportUsersToExcel();

}