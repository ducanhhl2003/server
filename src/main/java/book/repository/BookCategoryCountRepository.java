package book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import book.dto.BookCategoryCountDTO;
import book.entity.BookEntity;

@Repository
public interface BookCategoryCountRepository extends JpaRepository<BookEntity, Integer> {
	@Query("SELECT new book.dto.BookCategoryCountDTO(c.name, CAST(COUNT(b) AS int)) "
			+ "FROM BookEntity b JOIN b.categories c " + "GROUP BY c.name")
	List<BookCategoryCountDTO> countBooksByCategory();

}
