package book.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import book.controller.output.CategoryOutput;
import book.dto.CategoryDTO;
import book.dto.search.CategorySearchDTO;

public interface ICategoryService {
	CategoryDTO save(CategoryDTO categoryDTO);

	void delete(Integer[] ids);

	List<CategoryDTO> findAll(Pageable pageable);

	int totalItem();

	Page<CategoryDTO> searchCategories(CategorySearchDTO request);

	CategoryOutput getCategoryList(int page, int limit);

	Map<String, Object> searchCategoriesResponse(CategorySearchDTO request);
}
