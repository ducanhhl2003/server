package book.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import book.config.AuthenticationFacade;
import book.dto.CommentDTO;
import book.entity.CommentEntity;
import book.entity.PostEntity;
import book.entity.UserEntity;
import book.exception.DataNotFoundException;
import book.exception.InvalidOwnerException;
import book.repository.CommentRepository;
import book.repository.PostRepository;
import book.service.ICommentService;
import book.utils.MessageKeys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final AuthenticationFacade authenticationFacade;
	private final ModelMapper modelMapper;

	@Override
	public CommentDTO addComment(Integer postId, CommentDTO dto) {
		UserEntity user = authenticationFacade.getCurrentUser();
		PostEntity post = postRepository.findById(postId)
				.orElseThrow(() -> new DataNotFoundException(MessageKeys.POST_NOT_FOUND));
		CommentEntity comment = modelMapper.map(dto, CommentEntity.class);
		comment.setUser(user);
		comment.setPost(post);
		return modelMapper.map(commentRepository.save(comment), CommentDTO.class);
	}

	@Override
	public CommentDTO updateComment(Integer commentId, CommentDTO dto) {
		CommentEntity comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new DataNotFoundException(MessageKeys.COMMENT_NOT_FOUND, commentId));
		if (!comment.getUser().getId().equals(authenticationFacade.getCurrentUser().getId())) {
			throw new InvalidOwnerException(MessageKeys.COMMENT_INVALID_OWNER);
		}
		comment.setContent(dto.getContent());
		return modelMapper.map(commentRepository.save(comment), CommentDTO.class);
	}

	@Override
	public void deleteComment(Integer commentId) {
		CommentEntity comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new DataNotFoundException(MessageKeys.COMMENT_NOT_FOUND, commentId));
		if (!comment.getUser().getId().equals(authenticationFacade.getCurrentUser().getId())) {
			throw new InvalidOwnerException(MessageKeys.COMMENT_INVALID_OWNER);
		}
		commentRepository.delete(comment);
	}

	@Override
	public List<CommentDTO> getCommentsByPostId(Integer postId) {
		List<CommentEntity> comments = commentRepository.findByPostId(postId);
		return comments.stream().map(comment -> modelMapper.map(comment, CommentDTO.class))
				.collect(Collectors.toList());
	}
}
