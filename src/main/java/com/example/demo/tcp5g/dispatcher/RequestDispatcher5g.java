package com.example.demo.tcp5g.dispatcher;

import com.alibaba.fastjson.JSON;
import com.example.demo.tcp.constant.CmdType;
import com.example.demo.tcp5g.handler.BaseHandler5g;
import com.example.demo.tcp5g.msg.MsgVo5g;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TRH
 * @description: 消息分发
 * @Package com.example.demo.tcp5g.dispatcher
 * @date 2023/3/27 16:19
 */
@Slf4j
public class RequestDispatcher5g {
    private static Map<Byte, BaseHandler5g> requestHandlerMap= new ConcurrentHashMap<>();


    public  static void dispatcher(ChannelHandlerContext channelHandlerContext, MsgVo5g msgVo5g){
//        获取消息处理器类型
        byte code = msgVo5g.getType();
//        找出对应注解
        String command = CmdType.getDescsByCode(code);
        log.info("{} - {} ===========>{}",String.format("%02x ", code), command, JSON.toJSONString(msgVo5g));

//		通过获取的code来进行后续业务处理的方法的选择
        BaseHandler5g baseHandler = (BaseHandler5g) requestHandlerMap.get(code);
        if (baseHandler == null){
            String sTemp = Integer.toHexString(0xFF & code);
            log.info("指令不存在: ======>{}",sTemp);
            return;
        }
        MsgVo5g responseMsg = baseHandler.handle(msgVo5g, channelHandlerContext);
        log.info("{} - {} ===========>{}", String.format("%02x ", code), "数据响应", JSON.toJSONString(responseMsg));
        if (responseMsg != null) {
//			如果有返回信息,将返回信息进行输出
            channelHandlerContext.writeAndFlush(responseMsg);
        }
    }

    /**
     * 启动项目时,将code和对应的处理类存入
     * @param courseMap
     */
    public static void setRequestHandlerMap(Map<Byte, BaseHandler5g> courseMap) {
        if (courseMap != null && courseMap.size() > 0) {
            for (Map.Entry<Byte, BaseHandler5g> entry : courseMap.entrySet()) {
                requestHandlerMap.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
