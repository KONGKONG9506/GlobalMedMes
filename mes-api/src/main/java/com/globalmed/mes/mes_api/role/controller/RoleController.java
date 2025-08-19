package com.globalmed.mes.mes_api.role.controller;

import com.globalmed.mes.mes_api.user.userrole.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-roles")
@RequiredArgsConstructor
public class RoleController {

    private final UserRoleService userRoleService;

    /** 로그인한 사용자의 역할 조회 */
    @GetMapping("/my")
    public ResponseEntity<List<String>> getMyRoles(@AuthenticationPrincipal String username) {
        List<String> roles = userRoleService.getRolesByUsername(username);
        return ResponseEntity.ok(roles);
    }
}