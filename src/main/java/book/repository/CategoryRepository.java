package book.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import book.entity.CategoryEntity;

@Repository
public interface CategoryRepository
		extends JpaRepository<CategoryEntity, Integer>, JpaSpecificationExecutor<CategoryEntity> {
	CategoryEntity findOneByName(String name);

	@Query("SELECT c FROM CategoryEntity c WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))")
	Page<CategoryEntity> searchCategories(@Param("name") String name, Pageable pageable);

	@Query("SELECT c FROM CategoryEntity c WHERE c.isDeleted = false")
	List<CategoryEntity> findAllActive();
}
