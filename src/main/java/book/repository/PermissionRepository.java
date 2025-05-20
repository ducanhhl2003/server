package book.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import book.entity.PermissionEntity;

@Repository
public interface PermissionRepository
		extends JpaRepository<PermissionEntity, Integer>, JpaSpecificationExecutor<PermissionEntity> {
	PermissionEntity findOneByName(String name);

	@Query("SELECT p FROM PermissionEntity p WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
	Page<PermissionEntity> searchPermissions(@Param("name") String name, Pageable pageable);

	@Query("SELECT r FROM PermissionEntity r WHERE r.isDeleted = false")
	List<PermissionEntity> findAllActive();
}
