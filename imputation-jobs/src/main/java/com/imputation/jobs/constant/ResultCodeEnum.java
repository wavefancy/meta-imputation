package com.imputation.jobs.constant;

public enum ResultCodeEnum {
    SUCCESS("成功",0),
    FAIL("失败",1),
    EMPTY("空参",2),
    EXCEPTION("异常",3),
    EXISTING("已存在相同账户",4),
    // 文件模块 3xxxx
    FILE_EMPTY("文件不能空",30001),
    FILE_NAME_EMPTY("文件名称不能为空",30002),
    FILE_MAX_SIZE("文件大小超出",30003),
    FILE_ID_EMPTY("文件id不能空",30004);


    private String name;
    private int code;

    private ResultCodeEnum(String name, int code){
        this.name=name;
        this.code=code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
