package com.handge.bigdata.resource.models.response.base;

/**
 * @author Liujuhao
 * @date 2018/5/31.
 */
public class ExceptionInfo {

    /**
     * 异常编码
     */
    private String code;

    /**
     * 异常描述
     */
    private String description;

    /**
     * 错误发生的代码行址
     */
    private String row;

    /**
     * 错误发生的类名
     */
    private String clazz;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "ExceptionInfo{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", row='" + row + '\'' +
                ", clazz='" + clazz + '\'' +
                '}';
    }
}
