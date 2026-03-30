package com.flower.repository;

import com.flower.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByPhone(String phone);

    Iterable<User> findByRole(String role);

    Iterable<User> findByIsMemberTrue();

    boolean existsByNickname(String nickname);

    boolean existsByNicknameAndIdNot(String nickname, Integer id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}