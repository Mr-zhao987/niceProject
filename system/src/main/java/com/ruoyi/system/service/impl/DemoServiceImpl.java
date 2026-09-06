package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

import com.ruoyi.system.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
public class DemoServiceImpl implements DemoService {



    public void test(){

    }
    @Override
    public AjaxResult queryDemo() {
        int a = 1;
        LoginUser loginUser = SecurityUtils.getLoginUser();
        try{
            if(a >0){
                throw new ServiceException("查询异常");
            }else {

            }
            return AjaxResult.success(loginUser.getUsername() + "查询成功");
        }catch (Exception e){
            return AjaxResult.error(loginUser.getUsername() + "查询失败");
        }
    }



}
