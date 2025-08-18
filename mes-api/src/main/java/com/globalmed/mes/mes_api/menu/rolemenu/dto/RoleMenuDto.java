package com.globalmed.mes.mes_api.menu.rolemenu.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleMenuDto {
    private Long id;
    private Long roleId;
    private Long menuId;
    private boolean allowRead;
    private boolean allowWrite;
    private boolean allowExec;
}