package com.example.mybatis.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body to update a menu; only non-null fields are updated")
public class MenuUpdateRequest {

    @Size(min = 1, max = 200)
    @Schema(description = "Menu name")
    private String name;

    @Size(max = 500)
    @Schema(description = "Route path")
    private String path;

    @Size(max = 255)
    @Schema(description = "Redirect path")
    private String redirect;

    @Schema(description = "Always show in sidebar")
    private Boolean alwaysShow;

    @Schema(description = "Hidden from sidebar")
    private Boolean hidden;

    @Size(max = 200)
    @Schema(description = "Display title")
    private String title;

    @Size(max = 100)
    @Schema(description = "Icon name or class")
    private String icon;

    @Schema(description = "Disable cache")
    private Boolean noCache;

    @Size(max = 100)
    @Schema(description = "i18n title key")
    private String titleKey;

    @Size(max = 500)
    @Schema(description = "External link")
    private String link;

    @Size(max = 255)
    @Schema(description = "Vue component name")
    private String component;

    @Schema(description = "Sort order")
    private Integer sortOrder;

    @Schema(description = "Parent menu ID")
    private Long parentId;

    @Size(max = 20)
    @Schema(description = "Text color")
    private String textColor;
}
