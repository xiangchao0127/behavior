package com.handge.bigdata.context;

import com.handge.bigdata.dao.api.ViewAuth;
import com.handge.bigdata.dao.model.TableAuthApi;
import com.handge.bigdata.pools.EnvironmentContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @auther Liujuhao
 * @date 2018/5/29.
 */
@Component
public class Runner implements CommandLineRunner {

    @Autowired
    WebApplicationContext applicationContext;

    @Autowired
    ViewAuth viewAuth;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("The Runner start to initialize ...");
        EnvironmentContainer.setENV();
        getAllUrl();
    }

    private void getAllUrl() {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        //获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        List<TableAuthApi> apiList = new ArrayList<>();
        for (RequestMappingInfo info : map.keySet()) {
            //获取url的Set集合，一个方法可能对应多个url
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            Method method = map.get(info).getMethod();
            PostMapping post = method.getAnnotation(PostMapping.class);
            PutMapping put = method.getAnnotation(PutMapping.class);
            GetMapping get = method.getAnnotation(GetMapping.class);
            DeleteMapping delete = method.getAnnotation(DeleteMapping.class);
            String type = null;
            if (post != null) {
                type = "POST";
            } else if (put != null) {
                type = "PUT";
            } else if (get != null) {
                type = "GET";
            } else if (delete != null) {
                type = "DELETE";
            }
            if (type != null) {
                for (String url : patterns) {
                    TableAuthApi api = new TableAuthApi();
                    api.setType(type);
                    api.setUrl(url);
                    api.setIsActived(true);
                    api.setUpdateAt(new Date());
                    apiList.add(api);
//                    System.out.println(type + "  " + url);
                }
            }
        }

//        viewAuth.updateAllApi(apiList);
//        viewAuth.updateByNewApi();
//        viewAuth.grantAllPermissionForAdmin();
    }
}
