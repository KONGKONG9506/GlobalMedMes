package com.globalmed.mes.mes_api.user.controller;

import com.globalmed.mes.mes_api.user.domain.UserEntity;
import com.globalmed.mes.mes_api.user.dto.UserDto;
import com.globalmed.mes.mes_api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(u -> new UserDto(u.getUserId(), u.getUsername(), u.getEmail()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable String id) {
        UserEntity user = userService.getUserById(id);
        return new UserDto(user.getUserId(), user.getUsername(), user.getEmail());
    }
}