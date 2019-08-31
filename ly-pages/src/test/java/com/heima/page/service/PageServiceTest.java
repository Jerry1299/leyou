package com.heima.page.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Classname PageServiceTest
 * @Description TODO
 * @Date 2019/8/25 15:16
 * @Created by YJF
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PageServiceTest {

    @Autowired
    PageService pageService;

    @Test
    public void createHtmlTest() {


        pageService.createItemHtml(141L);

    }



}
