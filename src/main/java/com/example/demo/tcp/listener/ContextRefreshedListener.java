package com.example.demo.tcp.listener;


import com.example.demo.tcp.annotation.RequestHandler;
import com.example.demo.tcp.dispatcher.RequestDispatcher;
import com.example.demo.tcp.handler.BaseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@Component
public class ContextRefreshedListener implements ApplicationListener<ApplicationStartedEvent> {

	private Logger logger = LoggerFactory.getLogger(ContextRefreshedListener.class);

	
	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		Map<Byte, BaseHandler> map = new HashMap<>();
//		获取所有实例的业务场景
		Map<String, Object> handlerMap = event.getApplicationContext().getBeansWithAnnotation(RequestHandler.class);
		//logger.info("---------------------load request handler----------------------");
		for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
			//System.out.println(entry.getValue());
			BaseHandler object = (BaseHandler) entry.getValue();
			Class c = object.getClass();
			Annotation[] annotations = c.getDeclaredAnnotations();

			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(RequestHandler.class)) {
//					将相应的类型和业务处理放入map中
					RequestHandler requestHandler = (RequestHandler) annotation;
					map.put(requestHandler.type().getTypeCode(), object);
					//logger.info("{}：{}", requestHandler.type().getDescs(),object.getClass().getName());
				}
			}
		}
		//logger.info("---------------------------------------------------------------");
		RequestDispatcher.setRequestHandlerMap(map);
	}

}
