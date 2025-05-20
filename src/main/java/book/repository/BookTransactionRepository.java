package book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import book.dto.BorrowedBookDTO;
import book.entity.BookTransactionEntity;

@Repository
public interface BookTransactionRepository
		extends JpaRepository<BookTransactionEntity, Integer>, JpaSpecificationExecutor<BookTransactionEntity> {
	@Query("SELECT new book.dto.BorrowedBookDTO(bt.book.id, bt.book.title, bt.book.author) "
			+ "FROM BookTransactionEntity bt " + "WHERE bt.user.id = :userId AND bt.return_date IS NULL")
	List<BorrowedBookDTO> findCurrentlyBorrowedBooksByUser(Integer userId);
}
