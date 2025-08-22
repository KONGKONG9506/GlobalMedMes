package com.globalmed.mes.mes_api.OldCode.auth.role.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String description;
}