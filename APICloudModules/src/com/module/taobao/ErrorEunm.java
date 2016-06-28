package com.module.taobao;

public enum ErrorEunm {
	UN_ASYNCINIT("尚未初始化，请先调用asyncInit方法！", 1000);
    // 成员变量
    private String msg;
    private int code;

    // 构造方法
    private ErrorEunm(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public int getCode() {
        return code;
    }
}
