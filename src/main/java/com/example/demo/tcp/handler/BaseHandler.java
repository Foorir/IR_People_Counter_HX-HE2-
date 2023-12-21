package com.example.demo.tcp.handler;


import com.example.demo.tcp.constant.MsgVo;
import io.netty.channel.ChannelHandlerContext;

/**
 * 协议处理基类
 * @author allen
 * 2018年8月13日
 */
public interface BaseHandler {
	
	
	 MsgVo handle(MsgVo msgVo, ChannelHandlerContext ctx);
	
	
}
