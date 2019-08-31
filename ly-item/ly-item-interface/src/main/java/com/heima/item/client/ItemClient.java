package com.heima.item.client;

import com.heima.common.vo.PageResult;
import com.heima.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("item-service")
public interface ItemClient {

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("brand/{id}")
    BrandDTO queryById(@PathVariable("id") Long id);

    /**
     * 根据id集合查询Category集合
     * @param ids
     * @return
     */
    @GetMapping("category/list")
    List<CategoryDTO> queryListById(@RequestParam("ids") List<Long> ids);

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
    PageResult<SpuDTO> getSpuPage(@RequestParam("key") String key
            , @RequestParam("saleable") Boolean saleable
            , @RequestParam("page") Integer page
            , @RequestParam("rows") Integer rows) ;


    /**
     * 根据spuid查询spudetail
     * @param id
     * @return
     */
    @GetMapping("spu/spudetail")
    SpuDetailDTO querySpuDetailById(@RequestParam("id") Long id) ;


    /**
     * 通过gid查询SpecParam集合
     * 请求方式：get
     * 请求路径：spec/params?gid=1
     * 请求参数：gid
     * 返回值：List<SpecParamDTO>
     */
    @GetMapping("spec/params")
    List<SpecParamDTO> queryParamsByGroup(@RequestParam(value="gid",required = false) Long gid,
                                          @RequestParam(value = "cid",required = false)Long cid,
                                          @RequestParam(value = "searching",required = false)Boolean searching) ;


    /**
     * 根据spuId查询Sku
     * @param id
     * @return
     */
    @GetMapping("sku/of/spu")
    List<SkuDTO> querySkuDTOBySpuId(@RequestParam("id") Long id) ;


    /**
     * 根据spuId查询spu
     * @param id
     * @return
     */
    @GetMapping("/spu/{id}")
    SpuDTO querySpuById(@PathVariable("id") Long id);

    /**
     * 根据CategoryId查询所欲specs集合
     * @param id
     * @return
     */
    @GetMapping("/spec/of/category")
    List<SpecGroupDTO> querySpecsByCid(@RequestParam("id") Long id);

}
