package book.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import book.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer>, JpaSpecificationExecutor<PostEntity> {
	PostEntity findOneByTitle(String title);

	@Query("SELECT p FROM PostEntity p WHERE "
			+ "(:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND "
			+ "(:content IS NULL OR LOWER(p.content) LIKE LOWER(CONCAT('%', :content, '%')))")

	Page<PostEntity> searchPosts(@Param("title") String title, @Param("content") String content, Pageable pageable);

	@Query("SELECT p FROM PostEntity p WHERE p.isDeleted = false")
	List<PostEntity> findAllActive();
}
