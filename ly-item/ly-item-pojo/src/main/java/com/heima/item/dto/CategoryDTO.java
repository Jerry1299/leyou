package com.heima.item.dto;

import lombok.Data;

/**
 * @Classname CategoryDTO
 * @Description TODO
 * @Date 2019/8/14 16:19
 * @Created by YJF
 */
@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Boolean isParent;
    private Integer sort;
}
