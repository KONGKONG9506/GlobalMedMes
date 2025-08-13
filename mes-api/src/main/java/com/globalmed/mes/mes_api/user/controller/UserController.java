package com.globalmed.mes.mes_api.user.controller;

import com.globalmed.mes.mes_api.user.repository.UserRepository;
import com.globalmed.mes.mes_api.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
}
