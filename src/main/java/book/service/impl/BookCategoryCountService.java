package book.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import book.dto.BookCategoryCountDTO;
import book.repository.BookCategoryCountRepository;
import book.service.IBookCategoryCountService;

@Service
public class BookCategoryCountService implements IBookCategoryCountService {

	@Autowired
	private BookCategoryCountRepository bookRepository;

	@Override
	public List<BookCategoryCountDTO> getBookCountByCategory() {
		return bookRepository.countBooksByCategory();
	}

}
