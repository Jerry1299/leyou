package com.heima.search.controller;

import com.heima.common.vo.PageResult;
import com.heima.search.dto.GoodsDTO;
import com.heima.search.dto.SearchRequest;
import com.heima.search.pojo.Goods;
import com.heima.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname SearchController
 * @Description TODO
 * @Date 2019/8/24 19:08
 * @Created by YJF
 */
@RestController
public class SearchController {


    @Autowired
    SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<PageResult<GoodsDTO>> queryGoodsPage(@RequestBody SearchRequest request) {

        return ResponseEntity.ok(searchService.queryGoodsPage(request));

    }

}
