package com.handge.bigdata;

import com.handge.bigdata.enumeration.ExceptionWrapperEnum;

import java.io.Serializable;

/**
 * @auther Liujuhao
 * @date 2018/5/28.
 */
public class UnifiedException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -8082731045456711837L;
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

    /**
     * 封装的异常
     */
    private Throwable throwable;

    public UnifiedException(String parameterDesc, ExceptionWrapperEnum exceptionWrapperEnum) {
        this.code = exceptionWrapperEnum.getCode();
        this.description = parameterDesc + exceptionWrapperEnum.getExplain2();
        this.row = this.getStackTrace()[0].getLineNumber() + "";
        this.clazz = this.getStackTrace()[0].getClassName() + "";
    }

    public UnifiedException(Throwable throwable) {
        this.throwable = throwable;
    }

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

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public String toString() {
        return "UnifiedException{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", row='" + row + '\'' +
                ", clazz='" + clazz + '\'' +
                ", throwable=" + throwable +
                '}';
    }
}
