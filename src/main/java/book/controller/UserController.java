package book.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import book.config.RoleConfig;
import book.controller.output.UserOutput;
import book.dto.UserDTO;
import book.dto.response.GenericResponse;
import book.dto.search.UserSearchDTO;
import book.service.IUserService;
import book.service.impl.AuthenticationService;
import book.utils.LocalizationUtils;
import book.utils.MessageKeys;
import book.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final IUserService userService;
	AuthenticationService authenticationService;
	private final LocalizationUtils localizationUtils;
	private final RoleConfig roleConfig;

//	@PostMapping("/login")
//    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
//        var result = authenticationService.authenticate(request);
//        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
//    }

	@GetMapping
//	@PreAuthorize("hasRole('ADMIN') and @permissionChecker.hasPermission(authentication, 'VIEW_USER')")	
	public ResponseEntity<GenericResponse<UserOutput>> showUser(@RequestParam("page") int page,
			@RequestParam("limit") int limit) {
		UserOutput result = userService.getUserList(page, limit);
		return ResponseUtil.success(MessageKeys.GET_USER_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.GET_USER_SUCCESSFULLY), result);
	}

	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	@PostMapping("/search")
//	@PreAuthorize("hasRole('ADMIN') and @permissionChecker.hasPermission(authentication, 'VIEW_USER')")
	public ResponseEntity<String> searchUsers(@RequestBody UserSearchDTO searchRequest,
			HttpServletRequest httpServletRequest) {
		return ResponseEntity.ok(userService.searchUsersResponse(searchRequest));
	}

	@PostMapping
	public ResponseEntity<GenericResponse<UserDTO>> createNew(@Valid @RequestBody UserDTO model) {
		return ResponseUtil.success(MessageKeys.REGISTER_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY), userService.save(model));
	}

	@PutMapping("/{id}")
	public ResponseEntity<GenericResponse<UserDTO>> updateNew(@Valid @RequestBody UserDTO model,
			@PathVariable("id") Integer id) {
		model.setId(id);
		return ResponseUtil.success(MessageKeys.UPDATE_USER_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_USER_SUCCESSFULLY), userService.save(model));
	}

	@DeleteMapping
	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	public ResponseEntity<Void> deleteNew(@RequestBody Integer[] ids) {
		userService.delete(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/export")
	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	public ResponseEntity<InputStreamResource> exportUsers() {
		String filename = "users.xlsx";
		InputStreamResource file = new InputStreamResource(userService.exportUsersToExcel());

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(file);
	}

}
