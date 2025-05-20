package book.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import book.dto.CommentDTO;
import book.service.ICommentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;

	@PreAuthorize("hasRole('USER','ADMIN') and @permissionChecker.hasPermission(authentication, 'ADD_COMMENT')")
    @PostMapping("/{postId}")
    public ResponseEntity<?> addComment(@PathVariable Integer postId, @RequestBody CommentDTO dto) {
        return ResponseEntity.ok(commentService.addComment(postId, dto));
    }
	@PreAuthorize("hasRole('USER','ADMIN') and @permissionChecker.hasPermission(authentication, 'UPDATE_COMMENT')")
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Integer commentId, @RequestBody CommentDTO dto) {
        return ResponseEntity.ok(commentService.updateComment(commentId, dto));
    }

	@PreAuthorize("hasRole('USER','ADMIN') and @permissionChecker.hasPermission(authentication, 'DELET_COMMENT')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("Xóa comment thành công");
    }
	@PreAuthorize("hasRole('USER','ADMIN') and @permissionChecker.hasPermission(authentication, 'VIEW_COMMENT')")
    @GetMapping("/{postId}")
    public ResponseEntity<?> getCommentsByPostId(@PathVariable Integer postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }
}
