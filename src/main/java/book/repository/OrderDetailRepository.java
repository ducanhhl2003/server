package book.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import book.dto.TopSellingProductDTO;
import book.entity.OrderDetailEntity;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Integer> {
//	@Query("SELECT od.product.id AS productId, " +
//	           "od.product.name AS productName, " +
//	           "SUM(od.numberOfProducts) AS totalSold " +
//	           "FROM OrderDetailEntity od " +
//	           "GROUP BY od.product.id, od.product.name " +
//	           "ORDER BY totalSold DESC")
//	    List<TopSellingProductDTO> findTopSellingProducts(Pageable pageable);
	@Query("SELECT od.product.id AS productId, " +
	           "od.product.name AS productName, " +
	           "SUM(od.numberOfProducts) AS totalSold " +
	           "FROM OrderDetailEntity od " +
	           "WHERE FUNCTION('MONTH', od.order.createdDate) = :month " +
	           "AND FUNCTION('YEAR', od.order.createdDate) = :year " +
	           "GROUP BY od.product.id, od.product.name " +
	           "ORDER BY totalSold DESC")
	    List<TopSellingProductDTO> findTopSellingProductsByMonth(
	            @Param("month") int month,
	            @Param("year") int year,
	            Pageable pageable);
}
