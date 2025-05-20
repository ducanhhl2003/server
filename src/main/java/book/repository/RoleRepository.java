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
import book.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer>, JpaSpecificationExecutor<RoleEntity> {
//	@EntityGraph(attributePaths = "permissions") 
	RoleEntity findOneByName(String name);

	@Query("SELECT r FROM RoleEntity r LEFT JOIN FETCH r.permissions p WHERE "
			+ "(:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) ")
	Page<RoleEntity> searchRoles(@Param("name") String name, Pageable pageable);

	@Query("SELECT r FROM RoleEntity r WHERE r.isDeleted = false")
	List<RoleEntity> findAllActive();

	@Query("SELECT p FROM RoleEntity r JOIN r.permissions p WHERE r.name = :name")
	List<PermissionEntity> findPermissionsByName(@Param("name") String name);

}
