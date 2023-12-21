package com.example.demo.tcp.coder;


import com.example.demo.tcp.constant.MsgVo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 消息编码器
 * @author allen
 * 2018年8月7日
 */
public class MsgEncoder extends MessageToByteEncoder<MsgVo> {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void encode(ChannelHandlerContext ctx, MsgVo msg, ByteBuf out)
			throws Exception {
		//写入头字节
		out.writeBytes(new byte[]{(byte) 0xFA,(byte) 0xF5,(byte) 0xF6});
		//写入流水号
		out.writeShort(msg.getSeq());
		//写入消息类型
		out.writeByte(msg.getType());
		//写入包体长度
		out.writeShort(msg.getLen());
		//写入包数据
		out.writeBytes(msg.getData().getBytes());
		//写入结束字节
		out.writeBytes(new byte[]{(byte) 0xFA,(byte) 0xF6,(byte) 0xF5});
		//byte[] bs = ByteObjConverter.ObjectToByte(obj);
		//out.writeBytes(bs);
		ctx.flush();
	}
	


}
