package com.example.demo.tcp.util;

import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 检测堆外内存
 * @author allen
 * 2019年1月2日
 */
public class DirectMemoryThead implements Runnable{
	
	Logger logger = LoggerFactory.getLogger(DirectMemoryThead.class);
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Field field = ReflectionUtils.findField(PlatformDependent.class, "DIRECT_MEMORY_COUNTER");
		field.setAccessible(true);
		while(true){
			try{
				AtomicLong directMemory = (AtomicLong)field.get(PlatformDependent.class);
				int memoryInkb = (int)(directMemory.get()/1024);
				logger.info("已使用堆外内存："+memoryInkb);
			}catch(Exception e){
				logger.info("堆外内存获取异常");
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
