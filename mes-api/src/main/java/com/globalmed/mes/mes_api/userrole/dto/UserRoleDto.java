package com.globalmed.mes.mes_api.userrole.dto;


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