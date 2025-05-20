package book.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import book.dto.BorrowedBookDTO;
import book.dto.TopPostDTO;
import book.repository.BookTransactionRepository;
import book.repository.TopPostRepository;
import book.service.IHomeService;

@Service
public class HomeService implements IHomeService {

	@Autowired
	private TopPostRepository topPostRepository;
	@Autowired
	private BookTransactionRepository bookTransactionRepository;

	@Override
	public List<TopPostDTO> getTop5FavoriteArticles() {
		return topPostRepository.findTopPostsByLikes(PageRequest.of(0, 5));
	}

	@Override
	public List<BorrowedBookDTO> getCurrentlyBorrowedBooksByUser(Integer userId) {
		return bookTransactionRepository.findCurrentlyBorrowedBooksByUser(userId);
	}

}
