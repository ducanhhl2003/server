package book.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import book.controller.output.PostOutput;
import book.dto.PostDTO;
import book.dto.search.PostSearchDTO;

public interface IPostService {
	PostDTO save(PostDTO postDTO);

	void delete(Integer[] ids);

	List<PostDTO> findAll(Pageable pageable);

	int totalItem();

	PostOutput getPostList(int page, int limit);

	Map<String, Object> searchPostsResponse(PostSearchDTO request);

	Page<PostDTO> searchPosts(PostSearchDTO request);
}
