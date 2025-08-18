package com.globalmed.mes.mes_api.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MenuDto {
    private Long menuId;
    private String menuCode;
    private String menuName;
    private String path;
    private String component;
    private String icon;
    private Integer sortOrder;
    private Byte isPublic;
    private Byte isActive;
}