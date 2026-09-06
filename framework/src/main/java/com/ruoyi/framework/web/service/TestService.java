package com.ruoyi.framework.web.service;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;


@Service
public class TestService {
    @Autowired
    @Qualifier("myThreadPoolExecutor")
    private ThreadPoolTaskScheduler taskScheduler;
    private Map<String,ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();

   // 假如有个接口调用init,每次调用的时候 都应该把原来的所有任务取消掉

   public void init(){

       cancelAllTask();

       for(int i=0;i<10;i++){
           start( i + "test" , () ->{
               System.out.println("开始执行任务");
           }, LocalDateTime.now());
           start(i +"test1" , () ->{
               System.out.println("开始执行任务");
           }, LocalDateTime.now().plusMinutes(1));
           start(i +"test2", () ->{
               System.out.println("开始执行任务");
           }, LocalDateTime.now().plusMinutes(2));
           start(i +"test3", () ->{
               System.out.println("开始执行任务");
           }, LocalDateTime.now().plusMinutes(3));
       }


   }
   public void start(String name,Runnable task ,LocalDateTime time){
       // time 类型 变为date 类型

       Date executeTime = DateUtils.toDate(time);
       ScheduledFuture<?> schedule = taskScheduler.schedule(task, executeTime);
       taskMap.put(name, schedule);
       // 强制清空所有的任务
   }

   public void cancelAllTask(){
       for(Map.Entry<String,ScheduledFuture<?>> entry : taskMap.entrySet()){
           String name = entry.getKey();
           ScheduledFuture<?> scheduledFuture = entry.getValue();
           cancelTask(scheduledFuture,name);
       }
   }

   public void cancelTask(ScheduledFuture<?> scheduledFuture,String name){
       if (scheduledFuture != null &&!scheduledFuture.isDone() && !scheduledFuture.isCancelled()) {
           scheduledFuture.cancel(true);
       }
   }


}
