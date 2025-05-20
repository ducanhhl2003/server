package book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import book.dto.BookTransactionDTO;
import book.service.IBookTransactionService;

@CrossOrigin
@RestController
@RequestMapping("/api/book-transaction")
public class BookTransactionController {
	@Autowired
	private IBookTransactionService transactionService;

	@PostMapping
	public BookTransactionDTO createTransaction(@RequestBody BookTransactionDTO dto) {
		return transactionService.save(dto);
	}

	@PutMapping("/{id}")
	public BookTransactionDTO updateTransaction(@RequestBody BookTransactionDTO dto, @PathVariable Integer id) {
		dto.setId(id);
		return transactionService.save(dto);
	}

	@DeleteMapping
	public void deleteTransactions(@RequestBody Integer[] ids) {
		transactionService.delete(ids);
	}
}