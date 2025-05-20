package book.service;

import java.util.List;

import book.dto.BorrowedBookDTO;
import book.dto.TopPostDTO;

public interface IHomeService {
	List<TopPostDTO> getTop5FavoriteArticles();

	List<BorrowedBookDTO> getCurrentlyBorrowedBooksByUser(Integer userId);
}
