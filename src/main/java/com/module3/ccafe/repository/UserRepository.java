package com.module3.ccafe.repository;

import com.module3.ccafe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByPhone(String phone);
  
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
}
