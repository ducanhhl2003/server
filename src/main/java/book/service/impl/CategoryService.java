package book.service.impl;

import java.util.Collections;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import book.controller.output.CategoryOutput;
import book.dto.CategoryDTO;
import book.dto.search.CategorySearchDTO;
import book.entity.BookEntity;
import book.entity.CategoryEntity;
import book.exception.DataNotFoundException;
import book.repository.BookRepository;
import book.repository.CategoryRepository;
import book.service.ICategoryService;
import book.utils.MessageKeys;

@Service
public class CategoryService implements ICategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public CategoryDTO save(CategoryDTO categoryDTO) {
		CategoryEntity categoryEntity;

		if (categoryDTO.getId() != null) {
			categoryEntity = categoryRepository.findById(categoryDTO.getId())
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.CATEGORY_NOT_FOUND, categoryDTO.getId()));
			modelMapper.map(categoryDTO, categoryEntity);
		} else {
			categoryEntity = modelMapper.map(categoryDTO, CategoryEntity.class);
		}

		if (categoryDTO.getBook() != null && !categoryDTO.getBook().isEmpty()) {
			BookEntity book = bookRepository.findOneByTitle(categoryDTO.getBook());
			if (book != null) {
				categoryEntity.setBooks(Collections.singleton(book));
			} else {
				throw new DataNotFoundException(MessageKeys.BOOK_NOT_FOUND, categoryDTO.getBook());
			}
		}

		categoryEntity = categoryRepository.save(categoryEntity);
		return modelMapper.map(categoryEntity, CategoryDTO.class);
	}

	@Override
	@Transactional
	public void delete(Integer[] ids) {
		for (Integer id : ids) {
			CategoryEntity category = categoryRepository.findById(id)
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.CATEGORY_NOT_FOUND, id));
			category.setIsDeleted(true);
			categoryRepository.save(category);
		}
	}

	@Override
	public List<CategoryDTO> findAll(Pageable pageable) {
		return categoryRepository.findAll(pageable).getContent().stream()
				.map(category -> modelMapper.map(category, CategoryDTO.class)).collect(Collectors.toList());
	}

	@Override
	public int totalItem() {
		return (int) categoryRepository.count();
	}

	@Override
	public Page<CategoryDTO> searchCategories(CategorySearchDTO request) {
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy()));

		Page<CategoryEntity> categories = categoryRepository.searchCategories(request.getName(), pageable);

		return categories.map(category -> {
			CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);

			if (category.getBooks() != null && !category.getBooks().isEmpty()) {
				String books = category.getBooks().stream().map(BookEntity::getTitle).collect(Collectors.joining(", "));
				categoryDTO.setBook(books);
			}
			return categoryDTO;
		});
	}

	@Override
	public CategoryOutput getCategoryList(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		List<CategoryDTO> categories = categoryRepository.findAll(pageable).getContent().stream()
				.map(category -> modelMapper.map(category, CategoryDTO.class)).collect(Collectors.toList());

		int totalItems = (int) categoryRepository.count();
		int totalPages = (int) Math.ceil((double) totalItems / limit);

		CategoryOutput result = new CategoryOutput();
		result.setPage(page);
		result.setListResult(categories);
		result.setTotalPage(totalPages);
		return result;
	}

	@Override
	public Map<String, Object> searchCategoriesResponse(CategorySearchDTO request) {
		Page<CategoryDTO> cPage = searchCategories(request);

		Map<String, Object> response = new HashMap<>();
		response.put("code", "200");
		response.put("message", "Search successful");
		response.put("data", cPage.getContent());
		response.put("currentPage", cPage.getNumber());
		response.put("totalPages", cPage.getTotalPages());
		response.put("totalItems", cPage.getTotalElements());

		return response;
	}
}