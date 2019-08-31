package com.heima.item.dto;

import lombok.Data;

import java.util.List;

/**
 * @Classname SpecGroupDTO
 * @Description TODO
 * @Date 2019/8/19 17:24
 * @Created by YJF
 */
@Data
public class SpecGroupDTO {
    private Long id;
    private Long cid;
    private String name;
    private List<SpecParamDTO> params;
}
