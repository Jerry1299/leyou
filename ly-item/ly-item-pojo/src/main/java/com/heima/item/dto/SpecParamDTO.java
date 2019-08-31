package com.heima.item.dto;

import lombok.Data;

/**
 * @Classname SpecParamDTO
 * @Description TODO
 * @Date 2019/8/19 19:44
 * @Created by YJF
 */
@Data
public class SpecParamDTO {
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String segments;
}
