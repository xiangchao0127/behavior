package com.handge.bigdata.aop;

import com.handge.bigdata.context.CustomProperty;
import com.handge.bigdata.context.ExceptionHandle;
import com.handge.bigdata.dao.api.RepositoryAccount;
import com.handge.bigdata.dao.model.TableAuthAccount;
import com.handge.bigdata.dao.model.TableAuthRoleBasic;
import com.handge.bigdata.enumeration.RolePropertyEnum;
import com.handge.bigdata.resource.models.response.common.CommonRes;
import com.handge.bigdata.resource.models.response.monitor.AbnormalAlarmInfo;
import com.handge.bigdata.resource.models.response.monitor.IllegalInfo;
import com.handge.bigdata.resource.models.response.monitor.NonWorkingTimeLengthUserTop;
import com.handge.bigdata.resource.models.response.professional.ProfessionalAccomplishmentByDepartmentManager;
import com.handge.bigdata.resource.models.response.professional.Staff;
import com.handge.bigdata.resource.models.response.statistics.NonWorkingTimeByStaff;
import com.handge.bigdata.utils.PageResults;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Liujuhao
 * @date 2018/5/30.
 */

@Component
@Aspect
public class ResponseAOP {

    Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    ExceptionHandle exceptionHandle;

    @Autowired
    RepositoryAccount repositoryAccount;

    @Autowired
    CustomProperty customProperty;

    @Pointcut("execution(public * com.handge.bigdata.resource.*.*(..))")
    public void reponseDo() {
    }

    @Before("reponseDo()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        logger.info("URL : " + request.getRequestURL().toString());
        logger.info("HTTP_METHOD : " + request.getMethod());
        logger.info("IP : " + request.getRemoteAddr());
        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "ret", pointcut = "reponseDo()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        logger.info("METHOD_RETURN : " + ret);
    }

    //后置异常通知
/*    @AfterThrowing("reponseDo()")
    public void throwss(JoinPoint jp) {
        logger.info("方法异常时执行.....");
    }*/

    //后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
/*    @After("reponseDo()")
    public void after(JoinPoint jp) {
        logger.info("方法最后执行.....");
    }*/

    @Around("reponseDo()")
    public Object doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        boolean isHyperVisor = assertHyperVisor();
        Object result = null;
        logger.info("AROUND_TARGET_METHOD : " + proceedingJoinPoint.getSignature().getName());
        //obj之前可以写目标方法执行前的逻辑
        try {

        } catch (Exception e) {
            return exceptionHandle.exceptionGet(e);
        }
        result = proceedingJoinPoint.proceed();
        if (result instanceof ResponseEntity) {
            Object body = ((ResponseEntity) result).getBody();
            if (body instanceof Integer && (int) body == 1) {
                return ResponseEntity.ok().body(new CommonRes() {{
                    this.setDescription(customProperty.successDescription);
                }});
            }
            if (customProperty.isHidingName && !isHyperVisor) {
                if (body instanceof List || body instanceof PageResults) {
                    List list;
                    if (body instanceof PageResults) {
                        list = ((PageResults) body).getResults();
                    } else {
                        list = (List) body;
                    }
                    if (list.size() != 0) {
                        Object type = list.get(0);
                        if (type instanceof NonWorkingTimeLengthUserTop
                                || type instanceof NonWorkingTimeByStaff
                                || type instanceof ProfessionalAccomplishmentByDepartmentManager
                                || type instanceof Staff
//                                || type instanceof InternetFootprint
                                || type instanceof IllegalInfo
                                || type instanceof AbnormalAlarmInfo) {
                            for (Object o : list) {
                                Field name = o.getClass().getDeclaredField("name");
                                name.setAccessible(true);
                                if (name.get(o) != null) {
                                    if (name.get(o) instanceof List) {
                                        Object collect = ((List) name.get(o)).stream().map(s -> "***").collect(Collectors.toList());
                                        name.set(o, collect);
                                    } else {
                                        name.set(o, "***");
                                    }
                                }
                            }
                            return ResponseEntity.ok().body(body instanceof PageResults ? body : list);
                        }
                    }
                }
            }
        }
        return result;
    }

    private boolean assertHyperVisor() {
        boolean flag = false;
        Subject subject = SecurityUtils.getSubject();
        if (subject == null || subject.getPrincipal() == null) {
            return false;
        }
        TableAuthAccount user = (TableAuthAccount) subject.getPrincipals().getPrimaryPrincipal();
        TableAuthAccount authAccount = repositoryAccount.findByUsername(user.getUsername());
        Set<TableAuthRoleBasic> roleList = authAccount.getRoleList();
        for (TableAuthRoleBasic roleBasic : roleList) {
            if (roleBasic.getRoleProperty().equals(RolePropertyEnum.HyperVisor.getCode())) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
