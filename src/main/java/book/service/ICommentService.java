package book.service;

import java.util.List;

import book.dto.CommentDTO;

public interface ICommentService {
	CommentDTO addComment(Integer postId, CommentDTO dto);

	CommentDTO updateComment(Integer commentId, CommentDTO dto);

	void deleteComment(Integer commentId);

	List<CommentDTO> getCommentsByPostId(Integer postId);
}
