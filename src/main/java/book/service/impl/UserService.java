package book.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.controller.output.UserOutput;
import book.dto.UserDTO;
import book.dto.search.UserSearchDTO;
import book.entity.RoleEntity;
import book.entity.UserEntity;
import book.exception.BusinessException;
import book.exception.DataNotFoundException;
import book.repository.RoleRepository;
import book.repository.UserRepository;
import book.service.IUserService;
import book.utils.MessageKeys;

@Service
public class UserService implements IUserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public UserDTO save(UserDTO userDTO) {

		if (userDTO.getId() == null && userRepository.existsByUserName(userDTO.getUserName())) {
			throw new BusinessException(MessageKeys.USER_USERNAME_EXISTED);
		}
		UserEntity userEntity;

		if (userDTO.getId() != null) {
			userEntity = userRepository.findById(userDTO.getId())
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_FOUND, userDTO.getId()));

			modelMapper.map(userDTO, userEntity);
		} else {
			userEntity = modelMapper.map(userDTO, UserEntity.class);
			if (userDTO.getId() != null) {
	            userEntity.setId(userDTO.getId()); // Gán ID nếu có
	        } else {
	            Integer maxId = userRepository.findMaxId(); // Lấy ID lớn nhất
	            userEntity.setId((maxId != null) ? maxId + 1 : 1); // Gán ID mới
	        }
		}

		if (userDTO.getPassWord() != null && !userDTO.getPassWord().isEmpty()) {
			if (userDTO.getPassWord().length() < 6) {
				throw new BusinessException(MessageKeys.USER_PASSWORD_WEAK);
			}
			userEntity.setPassWord(passwordEncoder.encode(userDTO.getPassWord()));
		}

		if (userDTO.getRoleName() != null && !userDTO.getRoleName().isEmpty()) {
			RoleEntity role = roleRepository.findOneByName(userDTO.getRoleName());
			if (role != null) {
				userEntity.setRoles(Collections.singleton(role));
			} else {
				throw new DataNotFoundException(MessageKeys.ROLE_NOT_FOUND);
			}
		}

		userEntity = userRepository.save(userEntity);

		UserDTO resultDTO = modelMapper.map(userEntity, UserDTO.class);
		if (!userEntity.getRoles().isEmpty()) {
			resultDTO.setRoleName(userEntity.getRoles().iterator().next().getName());
		}

		return resultDTO;
	}

	@Override
	@Transactional
	public void delete(Integer[] ids) {
		for (Integer id : ids) {
			UserEntity user = userRepository.findById(id)
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_FOUND, id));
			user.setIsDeleted(true);
			userRepository.save(user);
		}
	}

//	@Override
//	public List<UserDTO> findAll(Pageable pageable) {
//		return userRepository.findAll(pageable).getContent().stream().map(user -> modelMapper.map(user, UserDTO.class))
//				.collect(Collectors.toList());
//	}
//
//	@Override
//	public int totalItem() {
//		return (int) userRepository.count();
//	}

//	@Override
//	public Page<UserDTO> searchUsers(UserSearchDTO request) {
//		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
//				Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy()));
//
//		Page<UserEntity> users = userRepository.searchUsers(request.getUserName(), request.getFullName(),
//				request.getEmail(), request.getPhone(), request.getRoleName(), pageable);
//
//		return users.map(user -> {
//			UserDTO userDTO = modelMapper.map(user, UserDTO.class);
//			if (user.getRoles() != null && !user.getRoles().isEmpty()) {
//				String roles = user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.joining(", "));
//				userDTO.setRoleName(roles);
//			}
//			return userDTO;
//		});
//	}
	@Override
	public Page<UserDTO> searchUsers(UserSearchDTO request) {
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy()));

		Page<UserEntity> users = userRepository.searchUsers(request, pageable);

		// Chuyển đổi Page<UserEntity> → Page<UserDTO>
		return users.map(user -> {
			UserDTO userDTO = modelMapper.map(user, UserDTO.class);
			if (user.getRoles() != null && !user.getRoles().isEmpty()) {
				String roles = user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.joining(", "));
				userDTO.setRoleName(roles);
			}
			return userDTO;
		});
	}

	@Override
	public UserOutput getUserList(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		List<UserDTO> users = userRepository.findAll(pageable).getContent().stream()
				.map(user -> modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());

		int totalItems = (int) userRepository.count();
		int totalPages = (int) Math.ceil((double) totalItems / limit);

		UserOutput result = new UserOutput();
		result.setPage(page);
		result.setListResult(users);
		result.setTotalPage(totalPages);
		return result;
	}

	@Override
	public String searchUsersResponse(UserSearchDTO request) {
		Page<UserDTO> userPage = searchUsers(request);

		Map<String, Object> response = new HashMap<>();
		response.put("data", userPage.getContent());
		response.put("currentPage", userPage.getNumber());
		response.put("totalPages", userPage.getTotalPages());
		response.put("totalItems", userPage.getTotalElements());

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(response); // Chuyển Map thành JSON String
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting response to JSON", e);
		}
	}

	@Override
	public ByteArrayInputStream exportUsersToExcel() {
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Users");

			String[] HEADERS = { "Username", "Full Name", "Email", "Phone", "Address" };
			Row headerRow = sheet.createRow(0);
			for (int col = 0; col < HEADERS.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERS[col]);
			}

			List<UserEntity> users = userRepository.findAll();

			int rowIdx = 1;
			for (UserEntity user : users) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(user.getUserName());
				row.createCell(1).setCellValue(user.getFullName());
				row.createCell(2).setCellValue(user.getEmail());
				row.createCell(3).setCellValue(user.getPhone());
				row.createCell(4).setCellValue(user.getAddress());
			}

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Lỗi khi xuất dữ liệu người dùng ra Excel: " + e.getMessage(), e);
		}
	}

}