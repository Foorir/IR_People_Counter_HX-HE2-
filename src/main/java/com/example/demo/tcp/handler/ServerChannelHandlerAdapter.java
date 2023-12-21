package com.example.demo.tcp.handler;


import com.example.demo.tcp.constant.MsgVo;
import com.example.demo.tcp.constant.NettyChannelMap;
import com.example.demo.tcp.dispatcher.RequestDispatcher;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;


/**
 * 管道数据读取
 * 
 * @author allen 2018年8月7日
 */
@ChannelHandler.Sharable
@Component
public class ServerChannelHandlerAdapter extends ChannelInboundHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(ServerChannelHandlerAdapter.class);
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");



	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		 String uuid = ctx.channel().id().asLongText();
		 logger.info("Client：" + ctx.channel().remoteAddress() +",Connected! UUID:" +uuid);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		SocketChannel channel = (SocketChannel) ctx.channel();
		String deviceId = NettyChannelMap.get(channel);
		if (deviceId != null) {
			NettyChannelMap.removeClient(deviceId);
			logger.warn("{} 下线", deviceId);
		}
		super.channelInactive(ctx);
		try {
			ctx.channel().close();
		} catch (Exception e) {
			logger.error("channelInactive异常", e);
		}

	}

	/**
	 * 此方法是16进制报文解码完成后所执行的步骤
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


		Object datamsg = msg;
		String s = String.valueOf(datamsg);


		System.err.println("+++++++++++++++-----------");
		System.err.println(s.toString());
//		将解码后的信息转成msgVo对象

		MsgVo vo = (MsgVo) msg;
		logger.info(vo.getData()+"|||||||");
		try {
//			将消息传入中间处理器中
			RequestDispatcher.dispatcher(ctx, vo);
		} catch (Exception e) {

			e.printStackTrace();
			logger.error("channelReadException:" + e.getLocalizedMessage());
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		/*
		 * try{ Writer writer = new StringWriter(); PrintWriter printWriter = new
		 * PrintWriter(writer); while (cause != null) {
		 * cause.printStackTrace(printWriter); cause = cause.getCause(); }
		 * printWriter.close(); String result = writer.toString();
		 * logger.error("exceptionCaught:"+getIPString(ctx)+",cause:"+result); //
		 * logger.info("当前已使用的堆外内存:"+DirectMemoryUtill.getDirectMemorySize()+"Kb");
		 * }catch(Exception e){ } if(cause instanceof ReadTimeoutException){
		 * logger.info("读取超时Exceptione"); }
		 */
//		SocketChannel channel = (SocketChannel) ctx.channel();
//		String deviceId = NettyChannelMap.get(channel);
//		ctx.close();
//		logger.error("通道异常 [deviceId={},ip={}]", deviceId, getIPString(ctx), cause);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				// logger.info("TCP IdleStateEvent,超过5分钟未读取到终端数据，关闭链接");
				ctx.channel().close();
				logger.warn(NettyChannelMap.get((SocketChannel) ctx.channel()) + " 超过3分钟未心跳");
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	

	public static String getIPString(ChannelHandlerContext ctx) {
		String ipString = "";
		String socketString = ctx.channel().remoteAddress().toString();
		int colonAt = socketString.indexOf(":");
		ipString = socketString.substring(1, colonAt);
		return ipString;
	}

	public static String getRemoteAddress(ChannelHandlerContext ctx) {
		String socketString = "";
		socketString = ctx.channel().remoteAddress().toString();
		return socketString;
	}
}
