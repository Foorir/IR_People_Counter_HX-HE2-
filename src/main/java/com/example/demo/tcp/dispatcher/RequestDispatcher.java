package com.example.demo.tcp.dispatcher;


import com.alibaba.fastjson.JSON;
import com.example.demo.tcp.constant.CmdType;
import com.example.demo.tcp.constant.MsgVo;
import com.example.demo.tcp.handler.BaseHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestDispatcher {

	private static Map<Byte, BaseHandler> requestHandlerMap = new ConcurrentHashMap<>();

	static Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);
	static Logger DATA_LOGGER = LoggerFactory.getLogger("UPDATE_DATA");

	/**
	 * 消息流转处理,将消息进行分类处理
	 *
	 * @param channelHandlerContext
	 * @param
	 */
	public static void dispatcher(ChannelHandlerContext channelHandlerContext, MsgVo msg) {
//		获取消息的类型,16进制22为时间同步,21为数据上报
		byte code = msg.getType();
//		通过上面获得的code找出对应的类型
		String command = CmdType.getDescsByCode(code);
		logger.info("{} - {} ===========>{}",String.format("%02x ", code), command, JSON.toJSONString(msg));
//		通过获取的code来进行后续业务处理的方法的选择
		BaseHandler baseHandler = (BaseHandler) requestHandlerMap.get(code);
		MsgVo responseMsg = baseHandler.handle(msg, channelHandlerContext);
		logger.info("{} - {} ===========>{}", String.format("%02x ", code), "数据响应", JSON.toJSONString(responseMsg));
		if (responseMsg != null) {
//			如果有返回信息,将返回信息进行输出
			channelHandlerContext.writeAndFlush(responseMsg);
		}

	}

	/**
	 * 启动项目时,将code和对应的处理类存入
	 * @param courseMap
	 */
	public static void setRequestHandlerMap(Map<Byte, BaseHandler> courseMap) {
		if (courseMap != null && courseMap.size() > 0) {
			for (Map.Entry<Byte, BaseHandler> entry : courseMap.entrySet()) {
				requestHandlerMap.put(entry.getKey(), entry.getValue());
			}
		}
	}

}
