package book.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import book.dto.TopPostDTO;
import book.entity.PostEntity;

@Repository
public interface TopPostRepository extends JpaRepository<PostEntity, Integer> {
	@Query("SELECT new book.dto.TopPostDTO(p.title, p.likes) FROM PostEntity p ORDER BY p.likes DESC")
	List<TopPostDTO> findTopPostsByLikes(Pageable pageable);
}
