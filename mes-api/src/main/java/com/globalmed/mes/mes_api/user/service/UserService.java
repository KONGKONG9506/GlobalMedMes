package com.globalmed.mes.mes_api.user.service;

import com.globalmed.mes.mes_api.user.domain.UserEntity;
import com.globalmed.mes.mes_api.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

//    public boolean register(UserEntity user) {
//        if (userRepository.existsByUsername(user.getUsername())) {
//            return false;
//        }
//        user.setUserId(UUID.randomUUID().toString());
//        user.setPasswordHash(encoder.encode(user.getPasswordHash())); // 비밀번호 해시화
//        user.setCreatedAt(LocalDateTime.now());
//        user.setFailedLoginCount(0);
//        userRepository.save(user);
//        return true;
//    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public UserEntity save(UserEntity user){
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }
}
