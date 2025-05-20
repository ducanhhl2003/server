package book.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import book.dto.BookCategoryCountDTO;
import book.dto.BorrowedBookDTO;
import book.dto.TopPostDTO;
import book.service.impl.BookCategoryCountService;
import book.service.impl.HomeService;

@CrossOrigin
@RestController
@RequestMapping("/api/home")
public class HomeController {
	@Autowired
	private BookCategoryCountService bookCategeryCountService;
	@Autowired
	private HomeService homeService;

	@GetMapping("/book-category-stats")
	public ResponseEntity<List<BookCategoryCountDTO>> getBookCategoryStats() {
		List<BookCategoryCountDTO> stats = bookCategeryCountService.getBookCountByCategory();
		return ResponseEntity.ok(stats);
	}

	@GetMapping("/top-articles")
	public ResponseEntity<List<TopPostDTO>> getTop5Articles() {
		List<TopPostDTO> topArticles = homeService.getTop5FavoriteArticles();
		return ResponseEntity.ok(topArticles);
	}

	@GetMapping("/borrowed-books/{userId}")
	public List<BorrowedBookDTO> getBorrowedBooks(@PathVariable Integer userId) {
		return homeService.getCurrentlyBorrowedBooksByUser(userId);
	}
}
