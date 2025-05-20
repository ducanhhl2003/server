package book.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import book.controller.output.BookOutput;
import book.dto.BookDTO;
import book.dto.search.BookSearchDTO;

public interface IBookService {
	BookDTO save(BookDTO bookDTO);

	void delete(Integer[] ids);

	List<BookDTO> findAll(Pageable pageable);

	int totalItem();

	Page<BookDTO> searchBooks(BookSearchDTO request);

	BookOutput getBookList(int page, int limit);

	String searchBooksResponse(BookSearchDTO request);

	ByteArrayInputStream exportBooksToExcel();

	ByteArrayInputStream importBooksFromExcel(MultipartFile file);

}
