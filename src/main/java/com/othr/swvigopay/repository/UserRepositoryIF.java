package com.othr.swvigopay.repository;

import com.othr.swvigopay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepositoryIF extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.isActive = true")
    User findByEmailAndIsActive(String email);
}
