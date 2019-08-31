package com.heima.page.web;

import com.heima.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @Classname PageController
 * @Description TODO
 * @Date 2019/8/24 21:01
 * @Created by YJF
 */
@Controller
public class PageController {

    @Autowired
    PageService pageService;

    @GetMapping("item/{id}.html")
    public String toItemPage(Model model, @PathVariable("id") Long id) {

//        查询页面数据
        Map<String,Object> itemData = pageService.loadItemData(id);
//        将页面数据封装到Model中
        model.addAllAttributes(itemData);
        return "item";
    }
}
