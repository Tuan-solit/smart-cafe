package com.module3.ccafe.repository;

import com.module3.ccafe.entity.User;
import com.module3.ccafe.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByPhone(String phone);

  Optional<User> findByEmail(String email);
  
    @Query("""
            select u
            from User u
            join u.role r
            where r.name = :roleName
            """)
    List<User> findUserByRoleName(@Param("roleName") String roleName);

    @Query("""
            select u
            from User u
            join u.role r
            where u.userId = :userId
            and r.name = :roleName
            """)
    Optional<User> findUserByIdAndRoleName(@Param("userId") Integer userId,
                                           @Param("roleName") String roleName);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    @Query("""
            select count(u)
            from User u
            join u.role r
            where r.name = :roleName
            """)
    long countUsersByRoleName(@Param("roleName") String roleName);

  // ================================
  // ADMIN - STAFF SEARCH / FILTER
  // ================================

  @Query("""
            select u
            from User u
            join u.role r
            where r.name = 'EMPLOYEE'
              and (
                    :keyword = ''
                    or lower(u.fullName) like lower(concat('%', :keyword, '%'))
                    or lower(u.phone) like lower(concat('%', :keyword, '%'))
                    or lower(u.email) like lower(concat('%', :keyword, '%'))
                  )
              and (
                    :status is null
                    or u.status = :status
                  )
            """)
  Page<User> searchStaff(
          @Param("keyword") String keyword,
          @Param("status") com.module3.ccafe.entity.enums.UserStatus status,
          Pageable pageable
  );

  // ================================
  // ADMIN - STAFF PAGINATION
  // ================================

  @Query("""
            select u
            from User u
            join u.role r
            where r.name = :roleName
            """)
  Page<User> findUsersByRoleName(
          @Param("roleName") String roleName,
          Pageable pageable
  );

  @Query("""
        select u
        from User u
        join u.role r
        where r.name = :roleName
          and (
                :keyword = ''
                or lower(u.fullName) like lower(concat('%', :keyword, '%'))
                or lower(u.phone) like lower(concat('%', :keyword, '%'))
                or lower(u.email) like lower(concat('%', :keyword, '%'))
          )
          and (
                :status is null
                or u.status = :status
          )
        """)
  Page<User> searchUsersByRoleName(
          @Param("roleName") String roleName,
          @Param("keyword") String keyword,
          @Param("status") UserStatus status,
          Pageable pageable
  );
}
