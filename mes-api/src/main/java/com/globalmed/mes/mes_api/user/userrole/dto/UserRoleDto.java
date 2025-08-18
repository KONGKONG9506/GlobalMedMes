package com.globalmed.mes.mes_api.user.userrole.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleDto {
    private Long id;
    private String userId;
    private Long roleId;
}