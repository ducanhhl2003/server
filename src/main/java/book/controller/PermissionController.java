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

import book.controller.output.PermissionOutput;
import book.dto.PermissionDTO;
import book.dto.search.PermissionSearchDTO;
import book.service.IPermissionService;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/permission")
public class PermissionController {
	@Autowired
	private IPermissionService permissionService;

	@PreAuthorize("hasRole('ADMIN') and @permissionChecker.hasPermission(authentication, 'VIEW_PERMISSION')")
	@GetMapping
	public ResponseEntity<PermissionOutput> showUser(@RequestParam("page") int page, @RequestParam("limit") int limit) {
		PermissionOutput result = permissionService.getPermissionList(page, limit);
		return ResponseEntity.ok(result);
	}

	@PreAuthorize("hasRole('ADMIN') and @permissionChecker.hasPermission(authentication, 'VIEW_PERMISSION')")
	@PostMapping("/search")
	public ResponseEntity<Map<String, Object>> searchPermissions(@RequestBody PermissionSearchDTO searchRequest) {
		return ResponseEntity.ok(permissionService.searchPermissionsResponse(searchRequest));

	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN') and @permissionChecker.hasPermission(authentication, 'ADD_PERMISSION')")
	public ResponseEntity<PermissionDTO> createNew(@Valid @RequestBody PermissionDTO model) {
		return ResponseEntity.ok(permissionService.save(model));
	}

	@PreAuthorize("hasRole('ADMIN') and @permissionChecker.hasPermission(authentication, 'UPDATE_PERMISSION')")
	@PutMapping("/{id}")
	public ResponseEntity<PermissionDTO> updateNew(@Valid @RequestBody PermissionDTO model, @PathVariable("id") Integer id) {
		model.setId(id);
		return ResponseEntity.ok(permissionService.save(model));
	}

	@PreAuthorize("hasRole('ADMIN') and @permissionChecker.hasPermission(authentication, 'DELETE_PERMISSION')")
	@DeleteMapping
	public ResponseEntity<Void> deleteNew(@RequestBody Integer[] ids) {
		permissionService.delete(ids);
		return ResponseEntity.noContent().build();

	}
}
