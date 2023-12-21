package com.example.demo.tcp5g.listener;

import com.example.demo.tcp5g.annotation.RequestHandler5g;
import com.example.demo.tcp5g.dispatcher.RequestDispatcher5g;
import com.example.demo.tcp5g.handler.BaseHandler5g;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TRH
 * @description:
 * @Package com.example.demo.tcp5g.listener
 * @date 2023/3/27 16:24
 */
@Slf4j
@Component
public class ContextRefreshedListener5g implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        HashMap<Byte, BaseHandler5g> map = new HashMap<>();
//        获取所有实例业务场景
        Map<String, Object> handlerMap = event.getApplicationContext().getBeansWithAnnotation(RequestHandler5g.class);
        //log.info("---------------------load request handler----------------------");

        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            BaseHandler5g object = (BaseHandler5g) entry.getValue();
            Class c = object.getClass();
            Annotation[] annotations = c.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(RequestHandler5g.class)){
//                    将相应的类型和业务放入到map中
                    RequestHandler5g requestHandler = (RequestHandler5g) annotation;
                    map.put(requestHandler.type().getTypeCode(), object);
                    //log.info("{}：{}", requestHandler.type().getDescs(),object.getClass().getName());
                }
            }
        }
        //log.info("---------------------------------------------------------------");
        RequestDispatcher5g.setRequestHandlerMap(map);
    }
}
