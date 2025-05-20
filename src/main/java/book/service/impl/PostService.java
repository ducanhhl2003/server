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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import book.config.AuthenticationFacade;
import book.controller.output.PostOutput;
import book.dto.PostDTO;
import book.dto.search.PostSearchDTO;
import book.entity.PostEntity;
import book.entity.UserEntity;
import book.exception.DataNotFoundException;
import book.exception.InvalidOwnerException;
import book.repository.PostRepository;
import book.service.IPostService;
import book.utils.MessageKeys;

@Service
public class PostService implements IPostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private ModelMapper modelMapper;

	private AuthenticationFacade authenticationFacade;

	@Override
	public PostDTO save(PostDTO postDTO) {
		PostEntity postEntity;

		if (postDTO.getId() != null) {
			postEntity = postRepository.findById(postDTO.getId())
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.POST_NOT_FOUND, postDTO.getId()));

			UserEntity currentUser = authenticationFacade.getCurrentUser();

			if (!postEntity.getUser().getId().equals(currentUser.getId())) {
				throw new InvalidOwnerException(MessageKeys.POST_INVALID_OWNER);
			}

			modelMapper.map(postDTO, postEntity);
		} else {
			postEntity = modelMapper.map(postDTO, PostEntity.class);
			postEntity.setUser(authenticationFacade.getCurrentUser());
		}

		postEntity = postRepository.save(postEntity);
		return modelMapper.map(postEntity, PostDTO.class);
	}

	@Override
	@Transactional
	public void delete(Integer[] ids) {
		for (Integer id : ids) {
			PostEntity post = postRepository.findById(id)
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.POST_NOT_FOUND, id));
			UserEntity currentUser = authenticationFacade.getCurrentUser();
			if (!post.getUser().getId().equals(currentUser.getId())) {
				throw new AccessDeniedException("Bạn chỉ có thể xoá bài viết của chính mình");
			}
			post.setIsDeleted(true);
			postRepository.save(post);
		}
	}

	@Override
	public List<PostDTO> findAll(Pageable pageable) {
		return postRepository.findAll(pageable).getContent().stream().map(post -> modelMapper.map(post, PostDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	public int totalItem() {
		return (int) postRepository.count();
	}

	@Override
	public Page<PostDTO> searchPosts(PostSearchDTO request) {
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy()));

		Page<PostEntity> posts = postRepository.searchPosts(request.getTitle(), request.getContent(), pageable);

		return posts.map(post -> {
			PostDTO postDTO = modelMapper.map(post, PostDTO.class);
			return postDTO;
		});
	}

	@Override
	public PostOutput getPostList(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		List<PostDTO> posts = postRepository.findAll(pageable).getContent().stream()
				.map(post -> modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());

		int totalItems = (int) postRepository.count();
		int totalPages = (int) Math.ceil((double) totalItems / limit);

		PostOutput result = new PostOutput();
		result.setPage(page);
		result.setListResult(posts);
		result.setTotalPage(totalPages);
		return result;
	}

	@Override
	public Map<String, Object> searchPostsResponse(PostSearchDTO request) {
		Page<PostDTO> postPage = searchPosts(request);

		Map<String, Object> response = new HashMap<>();
		response.put("code", "200");
		response.put("message", "Search successful");
		response.put("data", postPage.getContent());
		response.put("currentPage", postPage.getNumber());
		response.put("totalPages", postPage.getTotalPages());
		response.put("totalItems", postPage.getTotalElements());

		return response;
	}
}
