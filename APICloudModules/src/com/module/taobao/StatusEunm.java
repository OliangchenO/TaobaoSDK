package com.module.taobao;

public enum StatusEunm {
	SUCCESS("请求成功！", 1),
	UN_ASYNCINIT("尚未初始化，请先调用asyncInit方法！", 1000),
	ILLEGAL_ARGUMENT("参数异常请检查修改后再试", 1001);
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
