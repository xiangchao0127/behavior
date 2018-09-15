package com.handge.bigdata.context;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import com.handge.bigdata.resource.models.response.base.ExceptionInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Liujuhao
 * @date 2018/5/31.
 */
@ControllerAdvice
public class ExceptionHandle {

    Log logger = LogFactory.getLog(this.getClass());

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object exceptionGet(Exception e) {
        logger.error("error-occurs", e);
        ExceptionInfo DTO;
        if (e instanceof UnifiedException) {
            DTO = transform2DTO((UnifiedException) e);
        } else {
            DTO = wrapJavaException(e);
        }
        return ResponseEntity.ok().body(DTO);
    }

    private ExceptionInfo transform2DTO(UnifiedException e) {
        if (e.getThrowable() != null) {
            return wrapJavaException((Exception) e.getThrowable());
        }
        ExceptionInfo exceptionInfo = new ExceptionInfo();
        exceptionInfo.setCode(e.getCode());
        exceptionInfo.setDescription(e.getDescription());
        exceptionInfo.setRow(e.getRow());
        exceptionInfo.setClazz(e.getClazz());
        return exceptionInfo;
    }

    private ExceptionInfo wrapJavaException(Exception e) {
        ExceptionInfo exceptionInfo = new ExceptionInfo();
        //空指针
        if (e instanceof NullPointerException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.NullPointerException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.NullPointerException.getExplain2());
        }

        //数学运算异常--分母为零
        else if (e instanceof ArithmeticException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.ArithmeticException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.ArithmeticException.getExplain2());
        }

        //数组下标越界
        else if (e instanceof ArrayIndexOutOfBoundsException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.ArrayIndexOutOfBoundsException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.ArrayIndexOutOfBoundsException.getExplain2());
        }

        //数据库异常
        else if (e instanceof SQLException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.SQLException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.SQLException.getExplain2());
        }

        //IO异常
        else if (e instanceof IOException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.IOException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.IOException.getExplain2());
        }

        //指定类不存在
        else if (e instanceof ClassNotFoundException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.ClassNotFoundException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.ClassNotFoundException.getExplain2());
        }

        //字符串转数字错误
        else if (e instanceof NumberFormatException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.NumberFormatException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.NumberFormatException.getExplain2());
        }

        //参数错误
        else if (e instanceof IllegalArgumentException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.IllegalArgumentException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.IllegalArgumentException.getExplain2());
        }

        //没有该类的访问权限
        else if (e instanceof IllegalAccessException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.IllegalAccessException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.IllegalAccessException.getExplain2());
        }

        //数据类型转换异常
        else if (e instanceof ClassCastException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.ClassCastException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.ClassCastException.getExplain2());
        }

        //数组存储异常
        else if (e instanceof ArrayStoreException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.ArrayStoreException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.ArrayStoreException.getExplain2());
        }

        //文件未找到
        else if (e instanceof FileNotFoundException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.FileNotFoundException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.FileNotFoundException.getExplain2());
        }

        //文件已结束
        else if (e instanceof EOFException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.EOFException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.EOFException.getExplain2());
        }

        //违背安全原则
        else if (e instanceof SecurityException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.SecurityException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.SecurityException.getExplain2());
        }

        //方法未找到
        else if (e instanceof NoSuchMethodException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.NoSuchMethodException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.NoSuchMethodException.getExplain2());
        }

        //线程被中断
        else if (e instanceof InterruptedException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.InterruptedException.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.InterruptedException.getExplain2());
        }

        //接口权限错误
        else if (e instanceof UnauthenticatedException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.Auth_NOT_Validate.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.Auth_NOT_Validate.getExplain2());
        }

        else if (e instanceof IncorrectCredentialsException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.Auth_Password_Error.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.Auth_Password_Error.getExplain2());
        }

        else if (e instanceof LockedAccountException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.Auth_Status_Error.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.Auth_Status_Error.getExplain2());
        }

        else if (e instanceof AuthenticationException) {
            exceptionInfo.setCode(ExceptionWrapperEnum.Auth_NOT_Validate.getCode());
            exceptionInfo.setDescription(ExceptionWrapperEnum.Auth_NOT_Validate.getExplain2());
        }
        exceptionInfo.setRow(e.getStackTrace()[0].getLineNumber() + "");
        exceptionInfo.setClazz(e.getStackTrace()[0].getClassName() + "");
        return exceptionInfo;
    }
}
