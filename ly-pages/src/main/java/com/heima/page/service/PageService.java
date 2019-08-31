package com.heima.page.service;

import com.heima.common.constants.ExceptionEnum;
import com.heima.common.exception.LyException;
import com.heima.item.client.ItemClient;
import com.heima.item.dto.BrandDTO;
import com.heima.item.dto.CategoryDTO;
import com.heima.item.dto.SpecGroupDTO;
import com.heima.item.dto.SpuDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname PageService
 * @Description TODO
 * @Date 2019/8/24 22:58
 * @Created by YJF
 */
@Service
@Slf4j
public class PageService {

    @Autowired
    ItemClient itemClient;

    /**
     * 根据商品id查询商品详情
     * @param id
     * @return
     */
    public Map<String, Object> loadItemData(Long id) {

//        查询spu
        SpuDTO spuDTO = itemClient.querySpuById(id);
//        查询分类集合
        List<CategoryDTO> categoryDTOS = itemClient.queryListById(spuDTO.getCategoryIds());
//        查询品牌
        BrandDTO brandDTO = itemClient.queryById(spuDTO.getBrandId());
//        查询规格参数
        List<SpecGroupDTO> specGroupDTOS = itemClient.querySpecsByCid(spuDTO.getCid3());

//        将查询到的数据保存到Map中,返回
        HashMap<String, Object> data = new HashMap<>();
        data.put("categories", categoryDTOS);
        data.put("brand", brandDTO);
        data.put("spuName", spuDTO.getName());
        data.put("subTitle", spuDTO.getSubTitle());
        data.put("skus", spuDTO.getSkus());
        data.put("detail", spuDTO.getSpuDetail());
        data.put("specs", specGroupDTOS);
        return data;
    }

    @Autowired
    SpringTemplateEngine templateEngine;

    @Value("${ly.static.itemDir}")
    private String itemDir;

    @Value("${ly.static.itemTemplate}")
    private String itemTemplate;


    /**
     * 根据商品id创建静态页面
     * @param id
     */
    public void createItemHtml(Long id) {
//        创建上下文对象
        Context context = new Context();
//        将页面数据保存到context中
        context.setVariables(loadItemData(id));
//        创建静态页面保存路径
        File fileDir = new File(itemDir);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                log.error("创建静态页面目录失败,静态页面目录为:{}",fileDir.getAbsolutePath());
                throw new LyException(ExceptionEnum.DIRECTORY_WRITER_ERROR);
            }
        }
//        针对要保存的静态页面,拼接绝对路径
        File filePath = new File(fileDir, id + ".html");

//        根据路径创建流
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(filePath, "utf-8");
//            调用TemplateEngine中的process方法,生成静态页面
            templateEngine.process(itemTemplate, context, printWriter);
        } catch (IOException e) {
            log.error("[静态页服务]静态页面生成失败,商品id:{}",id,e);
            throw new LyException(ExceptionEnum.FILE_WRITER_ERROR);
        }


    }

    /**
     * 根据spuId删除静态页面
     * @param spuId
     */
    public void deleteItemHtml(Long spuId) {
//        创建要删除的目标文件路径
        File file = new File(itemDir , spuId + ".html");
//        若文件存在,进行删除,否则报异常
        if (file.exists()) {
            boolean delete = file.delete();
            if (!delete) {
                log.error("删除静态页面失败");
                throw new RuntimeException("删除静态页面失败");
            }
        }
    }
}
