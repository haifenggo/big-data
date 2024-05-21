package com.bigdata.bigdata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2024/4/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Boolean success;
    private String msg;
    private Object data;

    public static Result success(){
        return new Result(true, null, null);
    }
    public static Result success(Object data){
        return new Result(true, null, data);
    }
    public static Result fail(String msg){
        return new Result(false, msg, null);
    }
}

