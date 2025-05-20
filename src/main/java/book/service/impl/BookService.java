package book.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.controller.output.BookOutput;
import book.dto.BookDTO;
import book.dto.search.BookSearchDTO;
import book.entity.BookEntity;
import book.entity.CategoryEntity;
import book.excel.ExcelHelperBook;
import book.exception.DataNotFoundException;
import book.repository.BookRepository;
import book.repository.CategoryRepository;
import book.service.IBookService;
import book.utils.MessageKeys;
import jakarta.transaction.Transactional;

@Service
public class BookService implements IBookService {

	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public BookDTO save(BookDTO bookDTO) {
		BookEntity bookEntity;

		if (bookDTO.getId() != null) {
			bookEntity = bookRepository.findById(bookDTO.getId())
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.BOOK_NOT_FOUND, bookDTO.getId()));

			modelMapper.map(bookDTO, bookEntity);
		} else {
			bookEntity = modelMapper.map(bookDTO, BookEntity.class);
		}

		if (bookDTO.getCategoryName() != null && !bookDTO.getCategoryName().isEmpty()) {
			CategoryEntity category = categoryRepository.findOneByName(bookDTO.getCategoryName());
			if (category != null) {
				bookEntity.setCategories(Collections.singleton(category));
			} else {
				throw new DataNotFoundException(MessageKeys.CATEGORY_NOT_FOUND, bookDTO.getCategoryName());
			}
		}

		bookEntity = bookRepository.save(bookEntity);

		BookDTO resultDTO = modelMapper.map(bookEntity, BookDTO.class);
		if (!bookEntity.getCategories().isEmpty()) {
			resultDTO.setCategoryName(bookEntity.getCategories().iterator().next().getName());
		}
		return resultDTO;
	}

	@Override
	@Transactional
	public void delete(Integer[] ids) {
		for (Integer id : ids) {
			BookEntity book = bookRepository.findById(id)
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.BOOK_NOT_FOUND, id));
			book.setIsDeleted(true);
			bookRepository.save(book);
		}
	}

	@Override
	public List<BookDTO> findAll(Pageable pageable) {
		return bookRepository.findAll(pageable).getContent().stream()
				.map(entity -> modelMapper.map(entity, BookDTO.class)).collect(Collectors.toList());
	}

	@Override
	public int totalItem() {
		return (int) bookRepository.count();
	}

	@Override
	public Page<BookDTO> searchBooks(BookSearchDTO request) {
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy()));

		Page<BookEntity> books = bookRepository.searchBooks(request, pageable);

		return books.map(book -> {
			BookDTO bookDTO = modelMapper.map(book, BookDTO.class);

			if (book.getCategories() != null && !book.getCategories().isEmpty()) {
				String categoryNames = book.getCategories().stream().map(CategoryEntity::getName)
						.collect(Collectors.joining(", "));
				bookDTO.setCategoryName(categoryNames);
			}

			return bookDTO;
		});
	}

	@Override
	public ByteArrayInputStream exportBooksToExcel() {
		try {
			List<BookEntity> books = bookRepository.findAll();
			return ExcelHelperBook.booksToExcel(books);
		} catch (IOException e) {
			throw new RuntimeException("Lỗi khi xuất dữ liệu người dùng ra Excel: " + e.getMessage(), e);
		}
	}

	@Override
	public ByteArrayInputStream importBooksFromExcel(MultipartFile file) {
		List<String[]> errorRows = new ArrayList<>();
		List<BookEntity> validBooks = new ArrayList<>();

		try {
			List<BookEntity> books = ExcelHelperBook.excelToBooks(file);

			for (int i = 0; i < books.size(); i++) {
				BookEntity book = books.get(i);
				List<String> errors = new ArrayList<>();
				if (book.getCode() == null || book.getTitle() == null || book.getAuthor() == null) {
					errors.add("Thiếu dữ liệu");
				}
				if (bookRepository.existsByCode(book.getCode())) {
					errors.add("Mã sách đã tồn tại");
				}

				if (!errors.isEmpty()) {
					errorRows.add(new String[] { String.valueOf(i + 2), String.join(", ", errors) });
				} else {
					validBooks.add(book);
				}
			}

			if (!validBooks.isEmpty()) {
				bookRepository.saveAll(validBooks);
			}

			return errorRows.isEmpty() ? null : ExcelHelperBook.createErrorFile(errorRows);

		} catch (Exception e) {
			throw new RuntimeException("Lỗi khi import sách: " + e.getMessage(), e);
		}
	}

	@Override
	public BookOutput getBookList(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		List<BookDTO> books = bookRepository.findAll(pageable).getContent().stream()
				.map(book -> modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());

		int totalItems = (int) bookRepository.count();
		int totalPages = (int) Math.ceil((double) totalItems / limit);

		BookOutput result = new BookOutput();
		result.setPage(page);
		result.setListResult(books);
		result.setTotalPage(totalPages);
		return result;
	}

	@Override
	public String searchBooksResponse(BookSearchDTO request) {
		Page<BookDTO> bookPage = searchBooks(request);

		Map<String, Object> response = new HashMap<>();
		response.put("code", "200");
		response.put("message", "Search successful");
		response.put("data", bookPage.getContent());
		response.put("currentPage", bookPage.getNumber());
		response.put("totalPages", bookPage.getTotalPages());
		response.put("totalItems", bookPage.getTotalElements());

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(response); // Chuyển Map thành JSON String
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting response to JSON", e);
		}
	}

}
