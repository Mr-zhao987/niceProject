package com.ruoyi.system.domain;

import lombok.Data;

@Data
public class MyNewTest<T,U>{
    private T data;
    private U msg;
    private int code;
    public MyNewTest(T data, U msg, int code) {
        this.data = data;
        this.msg = msg;
        this.code = code;
    }
    public MyNewTest() {
    }


    public static void main(String[] args) {
        MyNewTest<Integer,String> myNewTest1 = new MyNewTest<>(1,"成功",200);

    }

}
