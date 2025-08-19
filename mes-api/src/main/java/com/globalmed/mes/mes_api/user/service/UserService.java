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
    public static final int MAX_FAILED_ATTEMPTS = 3;   // 최대 실패 횟수
    private static final int LOCK_TIME_MINUTES = 1;   // 잠금 시간 (분)

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
    /** 로그인 성공 시 실패 횟수 초기화 및 마지막 로그인 시간 갱신 */
    public void resetFailedAttempts(UserEntity user) {
        user.setFailedLoginCount(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /** 로그인 실패 시 횟수 증가 및 필요시 계정 잠금 */
    public void increaseFailedAttempts(UserEntity user) {
        int newFailCount = user.getFailedLoginCount() + 1;
        user.setFailedLoginCount(newFailCount);

        if (newFailCount >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES));
        }

        userRepository.save(user);
    }

    /** 계정이 잠겨있는지 확인 */
    public boolean isAccountLocked(UserEntity user) {
        if (user.getLockedUntil() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (user.getLockedUntil().isAfter(now)) {
                // 아직 잠겨 있음
                return true;
            } else {
                // 잠금 해제 시점 지남 → 초기화
                user.setLockedUntil(null);
                user.setFailedLoginCount(0);
                userRepository.save(user);
                return false;
            }
        }
        return false;
    }
}
