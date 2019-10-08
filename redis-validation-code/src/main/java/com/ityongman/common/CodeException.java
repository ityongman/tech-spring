package com.ityongman.common;

/**
 * ExchangeCode 工程抛出的异常信息
 */
public class CodeException extends RuntimeException{
    private Integer errCode ;


    public CodeException(Integer errCode, String msg) {
        super(msg);
        this.errCode = errCode;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

}
