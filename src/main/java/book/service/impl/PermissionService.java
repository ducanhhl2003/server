package book.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import book.controller.output.PermissionOutput;
import book.dto.PermissionDTO;
import book.dto.search.PermissionSearchDTO;
import book.entity.PermissionEntity;
import book.entity.RoleEntity;
import book.exception.DataNotFoundException;
import book.repository.PermissionRepository;
import book.repository.RoleRepository;
import book.service.IPermissionService;
import book.utils.MessageKeys;

@Service
public class PermissionService implements IPermissionService {

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public PermissionDTO save(PermissionDTO permissionDTO) {
		PermissionEntity permissionEntity;

		if (permissionDTO.getId() != null) {
			permissionEntity = permissionRepository.findById(permissionDTO.getId()).orElseThrow(
					() -> new DataNotFoundException(MessageKeys.PERMISSION_NOT_FOUND, permissionDTO.getId()));
			modelMapper.map(permissionDTO, permissionEntity);
		} else {
			permissionEntity = modelMapper.map(permissionDTO, PermissionEntity.class);
		}

		if (permissionDTO.getRoleName() != null && !permissionDTO.getRoleName().isEmpty()) {
			RoleEntity role = roleRepository.findOneByName(permissionDTO.getRoleName());
			if (role != null) {
				permissionEntity.setRoles(Collections.singleton(role));
			} else {
				throw new DataNotFoundException(MessageKeys.ROLE_NOT_FOUND, permissionDTO.getRoleName());
			}
		}

		permissionEntity = permissionRepository.save(permissionEntity);

		PermissionDTO resultDTO = modelMapper.map(permissionEntity, PermissionDTO.class);
		if (!permissionEntity.getRoles().isEmpty()) {
			resultDTO.setRoleName(permissionEntity.getRoles().iterator().next().getName());
		}
		return resultDTO;
	}

	@Override
	@Transactional
	public void delete(Integer[] ids) {
		for (Integer id : ids) {
			PermissionEntity per = permissionRepository.findById(id)
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.PERMISSION_NOT_FOUND, id));
			per.setIsDeleted(true);
			permissionRepository.save(per);
		}
	}

	@Override
	public List<PermissionDTO> findAll(Pageable pageable) {
		return permissionRepository.findAll(pageable).getContent().stream()
				.map(permission -> modelMapper.map(permission, PermissionDTO.class)).collect(Collectors.toList());
	}

	@Override
	public int totalItem() {
		return (int) permissionRepository.count();
	}

	@Override
	public Page<PermissionDTO> searchPermissions(PermissionSearchDTO request) {
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy()));

		Page<PermissionEntity> permissions = permissionRepository.searchPermissions(request.getName(), pageable);

		return permissions.map(permission -> {
			PermissionDTO permissionDTO = modelMapper.map(permission, PermissionDTO.class);

			if (permission.getRoles() != null && !permission.getRoles().isEmpty()) {
				String roles = permission.getRoles().stream().map(RoleEntity::getName)
						.collect(Collectors.joining(", "));
				permissionDTO.setRoleName(roles);
			}

			return permissionDTO;
		});
	}

	@Override
	public PermissionOutput getPermissionList(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		List<PermissionDTO> permissions = permissionRepository.findAll(pageable).getContent().stream()
				.map(permission -> modelMapper.map(permission, PermissionDTO.class)).collect(Collectors.toList());

		int totalItems = (int) permissionRepository.count();
		int totalPages = (int) Math.ceil((double) totalItems / limit);

		PermissionOutput result = new PermissionOutput();
		result.setPage(page);
		result.setListResult(permissions);
		result.setTotalPage(totalPages);
		return result;
	}

	@Override
	public Map<String, Object> searchPermissionsResponse(PermissionSearchDTO request) {
		Page<PermissionDTO> permissionPage = searchPermissions(request);

		Map<String, Object> response = new HashMap<>();
		response.put("code", "200");
		response.put("message", "Search successful");
		response.put("data", permissionPage.getContent());
		response.put("currentPage", permissionPage.getNumber());
		response.put("totalPages", permissionPage.getTotalPages());
		response.put("totalItems", permissionPage.getTotalElements());

		return response;
	}
}
