package com.example.demo.tcp5g.handler;

import com.example.demo.tcp5g.annotation.RequestHandler5g;
import com.example.demo.tcp5g.msg.CmdType5g;
import com.example.demo.tcp5g.msg.MsgVo5g;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author TRH
 * @description: 历史数据
 * @Package com.example.demo.tcp5g.handler
 * @date 2023/4/6 15:12
 */
@Component
@RequestHandler5g(type = CmdType5g.DATA_UPLOAD_HISTORY)
@Slf4j
@RequiredArgsConstructor
public class HistoryDataHandler implements BaseHandler5g{

    @Override
    public MsgVo5g handle(MsgVo5g msgVo, ChannelHandlerContext ctx) {

        String data = msgVo.getData();
        log.info("接收到历史数据上报：{}", data);
        String[] split = data.split(",");
//sn
        String sn = split[0];
//        当前服务器时间
        LocalDateTime now = LocalDateTime.now();
        String time = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);

        int isLevelUp = 0;
        String updateUrl = "0";

        return response(msgVo, 0, time,isLevelUp, updateUrl);
    }


    public MsgVo5g response(MsgVo5g msgVo5g, int code, String time, int update, String updateUrl) {
        MsgVo5g msgvo = new MsgVo5g();
        msgvo.setType(msgVo5g.getType());
        msgvo.setParams(msgVo5g.getType());
        StringBuilder data = new StringBuilder();
//        状态码
        data.append(code);
        data.append(",");
//        时间
        data.append(time);
        data.append(",");
//        是否升级
        data.append(update);
        data.append(",");
//        更新的路径
        data.append(updateUrl);

        String s = data.toString();
        msgvo.setData(s);
        msgvo.setLen(s.length());
        msgvo.setCrcHigh(msgVo5g.getCrcHigh());
        msgvo.setCrcLow(msgVo5g.getCrcLow());
        return msgvo;
    }
}
