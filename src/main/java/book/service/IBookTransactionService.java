package book.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import book.dto.BookTransactionDTO;

public interface IBookTransactionService {
	BookTransactionDTO save(BookTransactionDTO bookTransactionDTO);

	void delete(Integer[] ids);

	List<BookTransactionDTO> findAll(Pageable pageable);

	int totalItem();

}