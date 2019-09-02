package com.heima.item.controller;

import com.heima.common.vo.PageResult;
import com.heima.item.dto.SkuDTO;
import com.heima.item.dto.SpecGroupDTO;
import com.heima.item.dto.SpuDTO;
import com.heima.item.dto.SpuDetailDTO;
import com.heima.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Classname GoodsController
 * @Description TODO
 * @Date 2019/8/19 21:54
 * @Created by YJF
 */
@RestController
//@RequestMapping
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    /**
     * 分页查询所有商品
     * 请求类型:get
     * 请求路径:spu/page?key=&saleable=true&page=1&rows=5
     * 请求参数:
     * key
     * saleable
     * page
     * rows
     * 返回值:PageRult<SpuDTO>
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuDTO>> getSpuPage(@RequestParam(value = "key", required = false) String key
            , @RequestParam(value = "saleable", defaultValue = "5") Boolean saleable
            , @RequestParam(value = "page", defaultValue = "1") Integer page
            , @RequestParam(value = "rows", required = false) Integer rows) {

        return ResponseEntity.ok(goodsService.getSpuPage(key, saleable, page, rows));

    }

    /**
     * 新增商品
     * 请求方式:post
     * 请求路径:goods
     * 请求参数:SpuDTO
     * 返回值:Void
     */
    @PostMapping("goods")
    public ResponseEntity<Void> addGoods(@RequestBody SpuDTO spuDTO) {

        goodsService.addGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 上下架商品
     * 请求类型put
     * 请求路径spu/saleable
     * 请求参数id Long saleable Boolean
     * 返回值 无
     */
    @PutMapping("/spu/saleable")
    public ResponseEntity<Void> saleableSpu(@RequestParam("id") Long id,
                                            @RequestParam("saleable") Boolean saleable) {

        goodsService.saleableSpu(id, saleable);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 根据spuid查询spudetail
     *
     * @param id
     * @return
     */
    @GetMapping("spu/spudetail")
    public ResponseEntity<SpuDetailDTO> querySpuDetailById(@RequestParam("id") Long id) {
        return ResponseEntity.ok(goodsService.querySpuDetailById(id));
    }

    /**
     * 根据spuId查询Sku
     *
     * @param id
     * @return
     */
    @GetMapping("sku/of/spu")
    public ResponseEntity<List<SkuDTO>> querySkuDTOBySpuId(@RequestParam("id") Long id) {
        return ResponseEntity.ok(goodsService.querySkuDTOBySpuId(id));
    }


    /**
     * 根据SpuDTO对象修改Spu
     *
     * @param spuDTO
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO) {
        goodsService.updataGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * 根据spuId查询Spu
     *
     * @param id
     * @return
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> querySpuById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsService.querySpuById(id));
    }

    /**
     * 根据spuId查询SpuDetail
     *
     * @param id
     * @return
     */
    @GetMapping("spu/detail")
    public ResponseEntity<SpuDetailDTO> querySpuDetailBySpuId(@RequestParam("id") Long id) {
        return ResponseEntity.ok(goodsService.querySpuDetailById(id));
    }

    /**
     * 根据SkuIds查询List<Sku>
     * @param ids
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<SkuDTO>> querySkusBySkuIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(goodsService.querySkusBySkuIds(ids));
    }


}
