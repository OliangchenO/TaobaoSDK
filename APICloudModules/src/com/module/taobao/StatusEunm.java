package com.module.taobao;

public enum StatusEunm {
	SUCCESS("请求成功！", 1000),
	UN_ASYNCINIT("尚未初始化，请先调用asyncInit方法！", 1001),
	ILLEGAL_ARGUMENT("参数异常请检查修改后再试！", 1002);
    // 成员变量
    private String msg;
    private int code;

    // 构造方法
    private StatusEunm(String msg, int code) {
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
