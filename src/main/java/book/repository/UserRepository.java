package book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import book.dto.search.UserSearchDTO;
import book.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {
	UserEntity findByUserName(String userName);

	boolean existsByUserName(String username);

	@Query("SELECT u FROM UserEntity u WHERE u.isDeleted = false")
	List<UserEntity> findAllActive();

	Optional<UserEntity> findByEmail(String email);

	@Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles r WHERE "
			+ "(:#{#userSearchDTO?.userName} IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :#{#userSearchDTO.userName}, '%'))) AND "
			+ "(:#{#userSearchDTO?.fullName} IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :#{#userSearchDTO.fullName}, '%'))) AND "
			+ "(:#{#userSearchDTO?.email} IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :#{#userSearchDTO.email}, '%'))) AND "
			+ "(:#{#userSearchDTO?.phone} IS NULL OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :#{#userSearchDTO.phone}, '%'))) AND "
			+ "(:#{#userSearchDTO?.roleName} IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :#{#userSearchDTO.roleName}, '%')))")
	Page<UserEntity> searchUsers(@Param("userSearchDTO") UserSearchDTO userSearchDTO, Pageable pageable);

	@Query("SELECT COALESCE(MAX(u.id), 0) FROM UserEntity u")
	Integer findMaxId();

//	@Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles r WHERE "
//			+ "(:userName IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :userName, '%'))) AND "
//			+ "(:fullName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND "
//			+ "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND "
//			+ "(:phone IS NULL OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) AND "
//			+ "(:roleName IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :roleName, '%')))")
//	Page<UserEntity> searchUsers(@Param("userName") String userName, @Param("fullName") String fullName,
//			@Param("email") String email, @Param("phone") String phone, @Param("roleName") String roleName,
//			Pageable pageable);

}
