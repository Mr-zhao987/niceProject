package com.ruoyi.web.controller.xf;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.DemoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/xf")
public class DemoController extends BaseController {

    @Resource
    private DemoService demoService;

    @RequestMapping("/demo")
    public AjaxResult demo() {
        AjaxResult ajaxResult = demoService.queryDemo();
        return ajaxResult;
    }
}
