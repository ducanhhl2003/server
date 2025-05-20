package book.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import book.entity.ProductEntity;

@Repository
public interface ProductRepository
		extends JpaRepository<ProductEntity, Integer>, JpaSpecificationExecutor<ProductEntity> {
	ProductEntity findOneByName(String title);

	@Query("SELECT b FROM ProductEntity b WHERE b.isDeleted = false")
	List<ProductEntity> findAllActive();

	@Query("""
			    SELECT DISTINCT p FROM ProductEntity p
			    LEFT JOIN p.categories c
			    WHERE p.isDeleted = false
			    AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
			       AND (:price IS NULL OR p.price = :price)
			       AND (:description IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%')))
			    AND (:categoryName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%')))
			""")
	Page<ProductEntity> searchProducts(@Param("name") String name, @Param("price") Float price,
			@Param("description") String description, @Param("categoryName") String categoryName, Pageable pageable);

//	@Query("""
//		    SELECT pr FROM ProductEntity pr 
//		    WHERE (:categoryId IS NULL OR :categoryId = 0 OR 
//		          EXISTS (SELECT 1 FROM pr.categories c WHERE c.id = :categoryId)) 
//		    AND (:keyword IS NULL OR :keyword = '' OR LOWER(pr.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
//		    OR LOWER(pr.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
//		""")
//		Page<ProductEntity> findByCategoryAndKeyword(@Param("categoryId") Integer categoryId, 
//		                                             @Param("keyword") String keyword, 
//		                                             Pageable pageable);
//	@EntityGraph(attributePaths = {"categories"})
	@Query("""
		    SELECT DISTINCT p FROM ProductEntity p
		    JOIN FETCH p.categories c
		    WHERE (:categoryId IS NULL OR :categoryId = 0 OR c.id = :categoryId)
		      AND (:keyword IS NULL OR :keyword = '' 
		           OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
		           OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
		      AND (:minPrice IS NULL OR p.price >= :minPrice)
		      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
		      AND p.isDeleted = false
		""")
		Page<ProductEntity> findByCategoryAndKeywordAndPrice(
		    @Param("categoryId") Integer categoryId,
		    @Param("keyword") String keyword,
		    @Param("minPrice") Double minPrice,
		    @Param("maxPrice") Double maxPrice,
		    Pageable pageable
		);


	@Query("SELECT DISTINCT p FROM ProductEntity p JOIN p.categories c WHERE c.id = :categoryId")
	List<ProductEntity> findByCategory(@Param("categoryId") Integer categoryId);

	@Query("SELECT p FROM ProductEntity p WHERE p.id IN :productIds")
	List<ProductEntity> findProductsByIds(@Param("productIds") List<Integer> productIds);

	boolean existsByName(String name);
    List<ProductEntity> findByIshotTrue();

}
