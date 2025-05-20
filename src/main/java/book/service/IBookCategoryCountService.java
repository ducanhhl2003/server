package book.service;

import java.util.List;

import book.dto.BookCategoryCountDTO;

public interface IBookCategoryCountService {
	List<BookCategoryCountDTO> getBookCountByCategory();
}
