package com.heima.item.controller;

import com.heima.common.vo.PageResult;
import com.heima.item.dto.BrandDTO;
import com.heima.item.entity.Brand;
import com.heima.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Classname BrandController
 * @Description TODO
 * @Date 2019/8/14 22:28
 * @Created by YJF
 */
@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    BrandService brandService;

    /*
    * 请求方式:get
    * 请求路径:brand/page
    * 请求参数:
    *   key String
    *   page Long
    *   rows Long
    *   sortBy String
    *   desc Boolean
    * 返回值:
    *   PageResult<BrandDTO>
    * */

    @GetMapping("page")
    public ResponseEntity<PageResult<BrandDTO>> queryBrandPage(
            @RequestParam("key") String key,
            @RequestParam("page") Integer page,
            @RequestParam("rows") Integer rows,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("desc") Boolean desc) {

        return ResponseEntity.ok(brandService.queryBrandPage(key, page, rows,sortBy, desc));

    }

    /*
    * 请求方式:post
    * 请求路径:brand
    * 请求参数:
    *   brandDTO B
    *   ids List<Integer>
    * 返回值:
    *   Void
    * */
    @PostMapping
    public ResponseEntity<Void> addBrand(BrandDTO brandDTO, @RequestParam("cids") List<Integer> ids ) {

        brandService.addBrand(brandDTO,ids);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据CategoryId查询BrandDTO
     * @param id
     * @return
     */
    @GetMapping("of/category")
    public ResponseEntity<List<BrandDTO>> queryBrandByCategoryId(@RequestParam("id") Long id) {
        return ResponseEntity.ok(brandService.queryBrandsByCategoryId(id));
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<BrandDTO> queryById(@PathVariable("id") Long id) {
        BrandDTO brandDTO = brandService.queryBrandById(id);
        return ResponseEntity.ok(brandDTO);
    }





}
