package com.heima.common.vo;

import lombok.Data;

import java.util.List;

/**
 * @Classname PageResult
 * @Description TODO
 * @Date 2019/8/16 15:26
 * @Created by YJF
 */
@Data
public class PageResult<T> {
    private Integer total;
    private Integer totalPage;
    private List<T> items;

    public PageResult() {
    }

    public PageResult(Integer total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(Integer total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }
}
