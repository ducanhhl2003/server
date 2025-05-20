package book.service.impl;

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

import book.controller.output.RoleOutput;
import book.dto.RoleDTO;
import book.dto.search.RoleSearchDTO;
import book.entity.PermissionEntity;
import book.entity.RoleEntity;
import book.entity.UserEntity;
import book.exception.DataNotFoundException;
import book.repository.PermissionRepository;
import book.repository.RoleRepository;
import book.repository.UserRepository;
import book.service.IRoleService;
import book.utils.MessageKeys;

@Service
public class RoleService implements IRoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public RoleDTO save(RoleDTO roleDTO) {
		RoleEntity roleEntity;

		if (roleDTO.getId() != null) {
			roleEntity = roleRepository.findById(roleDTO.getId())
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.ROLE_NOT_FOUND, roleDTO.getId()));
			modelMapper.map(roleDTO, roleEntity);
		} else {
			roleEntity = modelMapper.map(roleDTO, RoleEntity.class);
		}

		if (roleDTO.getUserName() != null) {
			UserEntity user = userRepository.findByUserName(roleDTO.getUserName());
			if (user != null) {
				roleEntity.getUsers().clear();
				roleEntity.getUsers().add(user);
			} else {
				throw new DataNotFoundException(MessageKeys.ROLE_NOT_FOUND, roleDTO.getUserName());
			}
		}

		if (roleDTO.getPermissionName() != null) {
			PermissionEntity permission = permissionRepository.findOneByName(roleDTO.getPermissionName());
			if (permission != null) {
				roleEntity.getPermissions().clear();
				roleEntity.getPermissions().add(permission);
			} else {
				throw new DataNotFoundException(MessageKeys.PERMISSION_NOT_FOUND, roleDTO.getPermissionName());
			}
		}

		roleEntity = roleRepository.save(roleEntity);
		return modelMapper.map(roleEntity, RoleDTO.class);
	}

	@Override
	@Transactional
	public void delete(Integer[] ids) {
		for (Integer id : ids) {
			RoleEntity role = roleRepository.findById(id)
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.ROLE_NOT_FOUND, id));
			role.setIsDeleted(true);
			roleRepository.save(role);
		}
	}

	@Override
	public List<RoleDTO> findAll(Pageable pageable) {
		return roleRepository.findAll(pageable).getContent().stream().map(role -> modelMapper.map(role, RoleDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	public int totalItem() {
		return (int) roleRepository.count();
	}

	@Override
	public Page<RoleDTO> searchRoles(RoleSearchDTO request) {
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy()));

		Page<RoleEntity> roles = roleRepository.searchRoles(request.getName(), pageable);

		return roles.map(role -> {
			RoleDTO roleDTO = modelMapper.map(role, RoleDTO.class);

			if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
				String permissions = role.getPermissions().stream().map(PermissionEntity::getName)
						.collect(Collectors.joining(", "));
				roleDTO.setPermissionName(permissions);
			}

			if (role.getUsers() != null && !role.getUsers().isEmpty()) {
				String users = role.getUsers().stream().map(UserEntity::getUserName).collect(Collectors.joining(", "));
				roleDTO.setUserName(users);
			}
			return roleDTO;
		});
	}

	@Override
	public RoleOutput getRoleList(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		List<RoleDTO> roles = roleRepository.findAll(pageable).getContent().stream()
				.map(role -> modelMapper.map(role, RoleDTO.class)).collect(Collectors.toList());

		int totalItems = (int) roleRepository.count();
		int totalPages = (int) Math.ceil((double) totalItems / limit);

		RoleOutput result = new RoleOutput();
		result.setPage(page);
		result.setListResult(roles);
		result.setTotalPage(totalPages);
		return result;
	}

	@Override
	public Map<String, Object> searchRolesResponse(RoleSearchDTO request) {
		Page<RoleDTO> rolePage = searchRoles(request);

		Map<String, Object> response = new HashMap<>();
		response.put("code", "200");
		response.put("message", "Search successful");
		response.put("data", rolePage.getContent());
		response.put("currentPage", rolePage.getNumber());
		response.put("totalPages", rolePage.getTotalPages());
		response.put("totalItems", rolePage.getTotalElements());

		return response;
	}
}
