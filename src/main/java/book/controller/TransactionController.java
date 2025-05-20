package book.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import book.entity.TransactionEntity;
import book.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionRepository transactionRepository;

	@GetMapping
	public List<TransactionEntity> getAllTransactions() {
		return transactionRepository.findAll();
	}

	@GetMapping("/{status}")
	public List<TransactionEntity> getTransactionsByStatus(@PathVariable String status) {
		return transactionRepository.findByPaymentStatus(status.toUpperCase());
	}
}
