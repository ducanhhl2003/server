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

import book.controller.output.PostOutput;
import book.dto.PostDTO;
import book.dto.response.GenericResponse;
import book.dto.search.PostSearchDTO;
import book.service.IPostService;
import book.utils.LocalizationUtils;
import book.utils.MessageKeys;
import book.utils.ResponseUtil;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/post")
public class PostController {
	@Autowired
	private IPostService postService;
	@Autowired
	private LocalizationUtils localizationUtils;


	@GetMapping
	@PreAuthorize("hasRole('USER','ADMIN') and @permissionChecker.hasPermission(authentication, 'VIEW_POST')")
	public ResponseEntity<GenericResponse<PostOutput>> showPost(@RequestParam("page") int page, @RequestParam("limit") int limit) {
		PostOutput result = postService.getPostList(page, limit);
		return ResponseUtil.success(MessageKeys.GET_POST_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.GET_POST_SUCCESSFULLY),result);
	}

	@PreAuthorize("hasRole('USER','ADMIN') and @permissionChecker.hasPermission(authentication, 'VIEW_POST')")
	@PostMapping("/search")
	public ResponseEntity<Map<String, Object>> searchPosts(@RequestBody PostSearchDTO searchRequest) {
		return ResponseEntity.ok(postService.searchPostsResponse(searchRequest));

	}

	@PostMapping
	@PreAuthorize("hasRole('USER','ADMIN') and @permissionChecker.hasPermission(authentication, 'ADD_POST')")
	public ResponseEntity<GenericResponse<PostDTO>> createNew(@Valid @RequestBody PostDTO model) {
		return ResponseUtil.success(MessageKeys.INSERT_POST_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_SUCCESSFULLY),model);
	}

	@PutMapping("{id}")
	@PreAuthorize("hasRole('USER','ADMIN') and @permissionChecker.hasPermission(authentication, 'UPDATE_POST')")
	public ResponseEntity<GenericResponse<PostDTO>> updateNew(@Valid @RequestBody PostDTO model, @PathVariable("id") Integer id) {
		model.setId(id);
		return ResponseUtil.success(MessageKeys.UPDATE_POST_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_POST_SUCCESSFULLY),
				postService.save(model));
	}

	@PreAuthorize("hasRole('USER','ADMIN') and @permissionChecker.hasPermission(authentication, 'DELETE_POST')")
	@DeleteMapping
	public ResponseEntity<Void> deleteNew(@RequestBody Integer[] ids) {
		postService.delete(ids);
		return ResponseEntity.noContent().build();
	}
}
