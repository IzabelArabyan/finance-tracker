package com.example.finance.tracker.user.repository;

import com.example.finance.tracker.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //login
    Optional<User> findByUsername(String username);

    //registr
    boolean existsByUsername(String username);
}
