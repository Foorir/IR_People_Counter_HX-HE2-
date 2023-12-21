package com.example.demo.tcp5g.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.tcp5g.coder.NettyDecoder5g;
import com.example.demo.tcp5g.coder.NettyEncoder5g;
import com.example.demo.tcp5g.handler.ServerChannelHandlerAdapter5g;
import com.example.demo.tcp5g.handler.TcpServerHandler5g;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author TRH
 * @description:
 * @Package com.example.demo.tcp5g.server
 * @date 2023/3/27 16:51
 */
@Slf4j
@Component
public class NettyTcpServer5g implements Runnable {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private ServerBootstrap server;

    private ChannelFuture channelFuture;

    private Integer port = 8086 ;


    /**
     * 通道适配器
     */
    @Resource
    private ServerChannelHandlerAdapter5g channelHandlerAdapter;

    public synchronized void startListen() {
        try {
            // 绑定到指定端口
            channelFuture = server.bind(port).sync();
            log.info("红外客流5G服务器在[{}]端口启动监听", port);
        } catch (Exception e) {
            log.error("红外客流5G服务器在[{}]端口启动监听失败", port);
            e.printStackTrace();
        }
    }

    public void sendMessageToClient(String clientIp, Object msg) {
        Map<String, Channel> channelMap = TcpServerHandler5g.channelSkipMap.get(port);
        Channel channel = channelMap.get(clientIp);
        String sendStr;
        try {
            sendStr = OBJECT_MAPPER.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            log.info("向客户端 {} 发送消息内容：{}", clientIp, sendStr);
            channel.writeAndFlush(sendStr);
        } catch (Exception var4) {
            log.error("向客户端 {} 发送消息失败，消息内容：{}", clientIp, sendStr);
            throw new RuntimeException(var4);
        }
    }


    @Override
    public void run() {
        // nio连接处理池
        this.bossGroup = new NioEventLoopGroup();
        // 处理事件池
        this.workerGroup = new NioEventLoopGroup();
        server = new ServerBootstrap();
        server.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 自定义处理类
                        ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(905969674,7,2,3,0));
                        ch.pipeline().addLast(new NettyDecoder5g());
//                        ch.pipeline().addLast(new TcpServerHandler1());
                        ch.pipeline().addLast(new IdleStateHandler(1, 0, 0, TimeUnit.MINUTES));
                        ch.pipeline().addLast(new NettyEncoder5g());
                        ch.pipeline().addLast(channelHandlerAdapter);
                    }
                });
        server.option(ChannelOption.SO_BACKLOG, 128);
        server.childOption(ChannelOption.SO_KEEPALIVE, true);
        startListen();
    }
}
