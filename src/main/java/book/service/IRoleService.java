package book.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import book.controller.output.RoleOutput;
import book.dto.RoleDTO;
import book.dto.search.RoleSearchDTO;

public interface IRoleService {
	RoleDTO save(RoleDTO roleDTO);

	void delete(Integer[] ids);

	List<RoleDTO> findAll(Pageable pageable);

	int totalItem();

	Page<RoleDTO> searchRoles(RoleSearchDTO request);

	RoleOutput getRoleList(int page, int limit);

	Map<String, Object> searchRolesResponse(RoleSearchDTO request);
}
