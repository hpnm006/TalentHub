package com.webapp.talenthub.repository;

import com.webapp.talenthub.entity.User;
import com.webapp.talenthub.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    @Query("""
            
                        SELECT u FROM User u
            WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%',:keyword,'%'))
            OR LOWER(u.username) LIKE LOWER(CONCAT('%',:keyword,'%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%',:keyword,'%'))
            """)
    List<User> search(@Param("keyword") String keyword);

    long countByRole(Role role);

    long countByEnabled(boolean enabled);

    long count();

    List<User> findAllByOrderByIdDesc();

    List<User> findByEnabled(boolean enabled);

    List<User> findByFullNameContainingIgnoreCase(String keyword);


}