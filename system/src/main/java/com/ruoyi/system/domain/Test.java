package com.ruoyi.system.domain;

import lombok.Data;

import java.util.*;
@Data
public class Test {
    public static void main(String[] args) {
        Map<String, List<Map<String, Object>>> resMap = new HashMap<>();
        List<String> list1 = Arrays.asList("LM-215", "LM-216", "LM-217", "LM-218", "LM-219", "LM-220", "LM-221", "LM-222", "LM-223", "LM-224");

        for (int i = 0; i <= 9; i++) {

            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> map1 = new HashMap<>();
//            map1.put("label","全部任务");
            map1.put("全部任务", 10);
            list.add(map1);
            Map<String, Object> map2 = new HashMap<>();
//            map2.put("label","执行中");
            map2.put("执行中", 5);
            list.add(map2);

            Map<String, Object> map3 = new HashMap<>();
//            map3.put("label","已完成");
            map3.put("已完成", 3);
            list.add(map3);
            String name = list1.get(i);

            resMap.put(name, list);
        }


        System.out.println();


        resMap.forEach((k, v) -> {
            int all = 0;
            Integer executing = 0;
            Integer completed = 0;
            List<Map<String, Object>> maps = resMap.get(k);
            for (Map<String, Object> map : maps) {
                if(map.containsKey("全部任务")){
                    all = (Integer) map.getOrDefault("全部任务",0);

                }
                if(map.containsKey("执行中")){
                    executing  =(Integer) map.getOrDefault("执行中", 0);
                }
                if(map.containsKey("已完成")){
                    completed = (Integer) map.getOrDefault("已完成", 0);
                }

            }
            System.out.println();

        });


    }
}
