package com.example.mybatis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    private Long id;
    private String name;
    private String path;
    private String redirect;
    private Boolean alwaysShow;
    private Boolean hidden;
    private String title;
    private String icon;
    private Boolean noCache;
    private String titleKey;
    private String link;
    private String component;
    private Integer sortOrder;
    private Long parentId;
    private String textColor;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private String username;
}
