package com.example.backend.user.repository;

import com.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

//    Optional<User> findByNick(String nick);
//
//    Optional<User> findByKakaoId(Long kakaoId);

    User findByUserId(String userId);
}
