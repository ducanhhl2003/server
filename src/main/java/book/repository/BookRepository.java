package book.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import book.dto.search.BookSearchDTO;
import book.entity.BookEntity;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer>, JpaSpecificationExecutor<BookEntity> {
	BookEntity findOneByTitle(String title);

	@Query("SELECT b FROM BookEntity b WHERE b.isDeleted = false")
	List<BookEntity> findAllActive();

	@Query("SELECT b FROM BookEntity b LEFT JOIN FETCH b.categories c WHERE "
			+ "(:#{#bookSearchDTO?.title} IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :#{#bookSearchDTO.title}, '%'))) AND "
			+ "(:#{#bookSearchDTO?.code} IS NULL OR LOWER(b.code) LIKE LOWER(CONCAT('%', :#{#bookSearchDTO.code}, '%'))) AND "
			+ "(:#{#bookSearchDTO?.author} IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :#{#bookSearchDTO.author}, '%'))) AND "
			+ "(:#{#bookSearchDTO?.categoryName} IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :#{#bookSearchDTO.categoryName}, '%')))")
	Page<BookEntity> searchBooks(@Param("bookSearchDTO") BookSearchDTO bookSearchDTO, Pageable pageable);

	boolean existsByCode(String code);
}
