package com.heima.item.controller;

import com.heima.item.dto.SpecGroupDTO;
import com.heima.item.dto.SpecParamDTO;
import com.heima.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Classname SpecGroupController
 * @Description TODO
 * @Date 2019/8/19 16:00
 * @Created by YJF
 */
@RestController
public class SpecController {

    @Autowired
    SpecService specService;

    /**
     * 通过CategoryId查询GroupList
     * 请求方式：Get
     * 请求路径：spec/groups/of/category
     * 请求参数：id=3
     * 返回值：List<SpecGroupDTO>
     */

    @GetMapping("spec/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> queryGroupByCategory(@RequestParam("id") Long id) {
        return ResponseEntity.ok(specService.queryGroupByCategory(id));
    }


    /**
     * 通过gid查询SpecParam集合
     * 请求方式：get
     * 请求路径：spec/params?gid=1
     * 请求参数：gid
     * 返回值：List<SpecParamDTO>
     */
    @GetMapping("spec/params")
    public ResponseEntity<List<SpecParamDTO>> queryParamsByGroup(@RequestParam(value="gid",required = false) Long gid,
                                                                 @RequestParam(value = "cid",required = false)Long cid,
                                                                 @RequestParam(value = "searching",required = false)Boolean searching) {

        return ResponseEntity.ok(specService.queryParamsByGroup(gid,cid,searching));

    }


    /**
     * 根据CategoryId查询所有specs集合
     * @param id
     * @return
     */
    @GetMapping("spec/of/category")
    public ResponseEntity<List<SpecGroupDTO>> querySpecsByCid(@RequestParam("id") Long id) {
        return ResponseEntity.ok(specService.querySpecsByCid(id));
    }





}
