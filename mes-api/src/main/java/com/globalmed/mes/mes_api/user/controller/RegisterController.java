//package com.globalmed.mes.mes_api.user.controller;
//
//import com.globalmed.mes.mes_api.user.domain.UserEntity;
//import com.globalmed.mes.mes_api.user.dto.RegisterRequest;
//import com.globalmed.mes.mes_api.user.service.JwtProvider;
//import com.globalmed.mes.mes_api.user.service.UserService;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Slf4j
//@RequestMapping("/auth")
//@AllArgsConstructor
//@RestController
//public class RegisterController {
//    private final UserService userService;
//    private final JwtProvider jwtProvider;
//    private final BCryptPasswordEncoder encoder;
//
//    @PostMapping("/register")
//    public String register(@RequestBody RegisterRequest request) {
//        UserEntity user = new UserEntity();
//        user.setUserId(UUID.randomUUID().toString());
//        user.setUsername(request.getUsername()); // 필드명 맞춤
//        user.setEmail(request.getEmail());
//        user.setPasswordHash(request.getPassword()); // 평문 넣기 → service에서 해시 처리
//        user.setCreatedBy("system");
//        user.setCreatedAt(LocalDateTime.now());
//
//        boolean success = userService.register(user);
//        return success ? "register successful" : "register failure";
//    }
//}