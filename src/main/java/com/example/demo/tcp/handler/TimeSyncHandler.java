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

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequestHandler(type = CmdType.TIME_SYNC)
@RequiredArgsConstructor
public class TimeSyncHandler<T> implements BaseHandler {

    private Logger logger = LoggerFactory.getLogger(TimeSyncHandler.class);


    /**
     * 时间同步的处理方法
     * @param msgVo
     * @param ctx
     * @return
     */
    @Override
    public MsgVo handle(MsgVo msgVo, ChannelHandlerContext ctx) {
//        将相应的xml形式的数据文件转为标准的json数据
        JSONObject jsonObject = XmlTool.documentToJSONObject(msgVo.getData());
//        获取唯一值
        String uuid = jsonObject.getString("uuid");

        logger.info("时间同步上报：" + jsonObject);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        MsgVo vvv = response(uuid, 0, msgVo, sdf.format(new Date()), "", "", "","");

        logger.info("时间同步回复：" + XmlTool.documentToJSONObject(vvv.getData()));

        return vvv;
    }

    /**
     * 该方法将指定数据进行格式化处理
     * @param deviceId
     * @param ret
     * @param msgVo
     * @param time
     * @param uploadInterval
     * @param dataStartTime
     * @param dataEndTime
     * @return
     */
    public MsgVo response(String deviceId, int ret, MsgVo msgVo, String time, String uploadInterval, String dataStartTime, String dataEndTime,String timeZone) {
//        传入设备id,ret的成功值为0,失败为1,信息对象,当前时间,上传间隔,开始时间与结束时间,进行格式化处理
        MsgVo responseMsg = new MsgVo();
        responseMsg.setSeq(msgVo.getSeq());
        responseMsg.setType(msgVo.getType());
        StringBuffer sb = new StringBuffer();
        sb.append("<TIME_SYSNC_RES>");
        sb.append("<uuid>").append(deviceId).append("</uuid>");
        sb.append("<ret>").append(ret).append("</ret>");
        sb.append("<time>").append(time).append("</time>");
        sb.append("<uploadInterval>").append(uploadInterval).append("</uploadInterval>");
        sb.append("<dataStartTime>").append(dataStartTime).append("</dataStartTime>");
        sb.append("<dataEndTime>").append(dataEndTime).append("</dataEndTime>");
        sb.append("<timeZone>").append(timeZone).append("</timeZone>");
        sb.append("</TIME_SYSNC_RES>");
        String pakBody = sb.toString();
        responseMsg.setLen(pakBody.length());
        responseMsg.setData(pakBody);
        return responseMsg;
    }

}
