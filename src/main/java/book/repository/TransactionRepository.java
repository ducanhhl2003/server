package book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import book.entity.TransactionEntity;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
	Optional<TransactionEntity> findByStripeSessionId(String sessionId);

	List<TransactionEntity> findByPaymentStatus(String status); // Lọc theo trạng thái
}
