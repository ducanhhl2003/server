package book.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import book.controller.output.RoleOutput;
import book.dto.RoleDTO;
import book.dto.response.GenericResponse;
import book.dto.search.RoleSearchDTO;
import book.service.IRoleService;
import book.utils.LocalizationUtils;
import book.utils.MessageKeys;
import book.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor

public class RoleController {
	private final LocalizationUtils localizationUtils;
	@Autowired
	private IRoleService roleService;

	@GetMapping
	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	public ResponseEntity<GenericResponse<RoleOutput>> showRole(@RequestParam("page") int page,
			@RequestParam("limit") int limit) {
		RoleOutput result = roleService.getRoleList(page, limit);
		return ResponseUtil.success(MessageKeys.GET_ROLE_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.GET_ROLE_SUCCESSFULLY), result);
	}

	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	@PostMapping("/search")
	public ResponseEntity<Map<String, Object>> searchRoles(@RequestBody RoleSearchDTO searchRequest) {
		return ResponseEntity.ok(roleService.searchRolesResponse(searchRequest));
	}

	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	@PostMapping
	public ResponseEntity<GenericResponse<RoleDTO>> createNew(@Valid @RequestBody RoleDTO model) {
		return ResponseUtil.success(MessageKeys.INSERT_ROLE_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.INSERT_ROLE_SUCCESSFULLY), roleService.save(model));
	}

	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	@PutMapping("/{id}")
	public ResponseEntity<GenericResponse<RoleDTO>> updateNew(@Valid @RequestBody RoleDTO model,
			@PathVariable("id") Integer id) {
		model.setId(id);
		return ResponseUtil.success(MessageKeys.UPDATE_ROLE_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_ROLE_SUCCESSFULLY), roleService.save(model));
	}

	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	@DeleteMapping
	public ResponseEntity<Void> deleteNew(@RequestBody Integer[] ids) {
		roleService.delete(ids);
		return ResponseEntity.noContent().build();

	}
}
