package com.acg.common.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel("统一返回结果集")
public class R {

    @ApiModelProperty(value = "是否成功")
    private boolean flag;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String msg;

    @ApiModelProperty(value = "返回数据")
    private Map<String ,Object> data;

    //构造方法私有
    private R(){}

    public R(boolean flag, Integer code, String msg, Map<String ,Object> data) {
        super();
        this.flag = flag;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public R(boolean flag, Integer code, String msg) {
        super();
        this.flag = flag;
        this.code = code;
        this.msg = msg;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String ,Object> getData() {
        return data;
    }

    public void setData(Map<String ,Object> data) {
        this.data = data;
    }
}