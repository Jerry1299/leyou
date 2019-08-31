package com.heima.item.controller;

import com.heima.item.dto.CategoryDTO;
import com.heima.item.service.CategoryService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Classname CategoryController
 * @Description TODO
 * @Date 2019/8/14 16:21
 * @Created by YJF
 */
@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /**
     * 根据parentId查询CategoryList
     *
     * @param id
     * @return
     */
    @GetMapping("of/parent")
    public ResponseEntity<List<CategoryDTO>> queryListByParent(@RequestParam("pid") Long id) {
        List<CategoryDTO> categoryDTOS = categoryService.queryListByParent(id);

        return ResponseEntity.ok(categoryDTOS);
    }

    /**
     * 根据id集合查询Category集合
     * @param ids
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<CategoryDTO>> queryListById(@RequestParam("ids") List<Long> ids) {
        List<CategoryDTO> categoryDTOS = categoryService.queryCategoryByIds(ids);
        return ResponseEntity.ok(categoryDTOS);
    }

}
