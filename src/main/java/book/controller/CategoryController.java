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

import book.controller.output.CategoryOutput;
import book.dto.CategoryDTO;
import book.dto.search.CategorySearchDTO;
import book.service.ICategoryService;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/category")
public class CategoryController {
	@Autowired
	private ICategoryService categoryService;

	@GetMapping
//	@PreAuthorize("hasRole('ADMIN') and @permissionChecker.hasPermission(authentication, 'VIEW_CATEGORY')")
	public ResponseEntity<CategoryOutput> showCategory(@RequestParam("page" )  int page ,
			@RequestParam("limit") int limit) {
		CategoryOutput result = categoryService.getCategoryList(page, limit);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/search")
//	@PreAuthorize("hasRole('ADMIN') and @permissionChecker.hasPermission(authentication, 'VIEW_CATEGORY')")
	public ResponseEntity<Map<String, Object>> searchCategories(@RequestBody CategorySearchDTO searchRequest) {
		return ResponseEntity.ok(categoryService.searchCategoriesResponse(searchRequest));
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN') and @permissionChecker.hasPermission(authentication, 'ADD_CATEGORY')")
	public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO model) {
		return ResponseEntity.ok(categoryService.save(model));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') and @permissionChecker.hasPermission(authentication, 'UPDATE_CATEGORY')")
	public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO model, @PathVariable("id") Integer id) {
		model.setId(id);
		return ResponseEntity.ok(categoryService.save(model));
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteCategories(@RequestBody Integer[] ids) {
		categoryService.delete(ids);
		return ResponseEntity.noContent().build();
	}
}
