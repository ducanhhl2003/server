package book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import book.entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
//	@Query("SELECT o FROM OrderEntity o WHERE" + "(:keword IS NULL OR :keyword = '' OR o.fullName LIKE %:keyword% OR o.address LIKE %:keyword%"+
//           "OR o.note LIKE %:keyword%)"
//			)
//	Page<OrderEntity> findByKeyword(String keyword,Pageable pageable);
	@Query("""
			    SELECT DISTINCT o FROM OrderEntity o
			    WHERE (:keyword IS NULL OR :keyword = ''
			           OR LOWER(o.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			           OR LOWER(o.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
			           OR LOWER(o.note) LIKE LOWER(CONCAT('%', :keyword, '%')))
			""")
	Page<OrderEntity> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

	Optional<OrderEntity> findById(Integer id);
	List<OrderEntity> findByUserId(Integer userId);
}
