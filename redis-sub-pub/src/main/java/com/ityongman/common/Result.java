package com.ityongman.common;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Result<T> {
    @JSONField(name = "errCode")
    private Integer errCode;

    @JSONField(name = "reason")
    private String reason;

    @JSONField(name = "result")
    private Integer result;

    private T data;

    public static <T> Result<T> failResult(int code, String failMsg) {
        Result<T> result = new Result<>();
        result.setErrCode(code);
        result.setResult(0);
        result.setReason(failMsg);
        return result;
    }

    public static <T> Result<T> failResult(String failMsg) {
        return failResult(0, failMsg);
    }

    public static <T> Result<T> failResult() {
        return failResult("server fails");
    }

    public static <T> Result<T> failFallback() {
        return failResult(-1, "execute fail back method");
    }

    public static <T> Result<T> succeedResult(T data, Integer code) {
        Result<T> result = new Result<>();
        result.setErrCode(code);
        result.setResult(1);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> succeedResult(T data) {
        return succeedResult(data, null);
    }

    public static <T> Result<T> succeedResult() {
        return succeedResult(null, null);
    }

    public boolean resultSuccess() {
        return result != null && result == 1;
    }
}
