package book.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import book.controller.output.PermissionOutput;
import book.dto.PermissionDTO;
import book.dto.search.PermissionSearchDTO;

public interface IPermissionService {
	PermissionDTO save(PermissionDTO permissionDTO);

	void delete(Integer[] ids);

	List<PermissionDTO> findAll(Pageable pageable);

	int totalItem();

	Page<PermissionDTO> searchPermissions(PermissionSearchDTO request);

	PermissionOutput getPermissionList(int page, int limit);

	Map<String, Object> searchPermissionsResponse(PermissionSearchDTO request);
}
