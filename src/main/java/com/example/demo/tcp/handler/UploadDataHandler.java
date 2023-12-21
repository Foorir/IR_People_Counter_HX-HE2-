package com.example.demo.tcp.handler;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.tcp.annotation.RequestHandler;
import com.example.demo.tcp.constant.CmdType;
import com.example.demo.tcp.constant.MsgVo;
import com.example.demo.tcp.util.XmlTool;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequestHandler(type = CmdType.DATA_UPLOAD)
@RequiredArgsConstructor
public class UploadDataHandler<T> implements BaseHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 数据上报的处理过程
     * @param msgVo
     * @param ctx
     * @return
     */
    @Override
    public MsgVo handle(MsgVo msgVo, ChannelHandlerContext ctx) {
//        将数据中的xml格式数据解析为json格式
        JSONObject jsonObject = XmlTool.documentToJSONObject(msgVo.getData());

        logger.info("客流数据上报：" + jsonObject);

//        获取设备唯一值
        String uuid = jsonObject.getString("uuid");


//        LocalDateTime now = LocalDateTime.now();
//        time = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);
//        Calendar ca = Calendar.getInstance();
//        timeZone = ca.getTimeZone().getID();

        MsgVo msgVo1 = response(uuid, 0, msgVo);

        logger.info("客流数据回复：" + XmlTool.documentToJSONObject(msgVo1.getData()));

//        成功返回指定信息
        return msgVo1;
    }

    /**
     * 将返回数据进行格式化处理
     * @param deviceId
     * @param ret
     * @param msgVo
     * @return
     */
    public MsgVo response(String deviceId, int ret, MsgVo msgVo) {
        MsgVo responseMsg = new MsgVo();
        responseMsg.setSeq(msgVo.getSeq());
        responseMsg.setType(msgVo.getType());
        StringBuffer sb = new StringBuffer();
        sb.append("<UP_SENSOR_DATA_RES>");
        sb.append("<uuid>").append(deviceId).append("</uuid>");
        sb.append("<ret>").append(ret).append("</ret>");
//        sb.append("<timeZone>").append(timeZone).append("</timeZone>");
        sb.append("</UP_SENSOR_DATA_RES>");
//        sb.append("<time>").append(time).append("</time>");
        String pakBody = sb.toString();
        responseMsg.setLen(pakBody.length());
        responseMsg.setData(pakBody);
        return responseMsg;
    }


}
