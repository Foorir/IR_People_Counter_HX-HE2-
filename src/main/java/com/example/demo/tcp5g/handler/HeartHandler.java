package com.example.demo.tcp5g.handler;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.tcp5g.annotation.RequestHandler5g;
import com.example.demo.tcp5g.msg.CmdType5g;
import com.example.demo.tcp5g.msg.MsgVo5g;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author TRH
 * @description: 心跳
 * @Package com.example.testdemo.tcpkeliu1.handler
 * @date 2023/3/23 17:19
 */
@Slf4j
@Component
@RequestHandler5g(type = CmdType5g.HEART_UPLOAD)
@RequiredArgsConstructor
public class HeartHandler<T>  implements BaseHandler5g {

    @Override
    public MsgVo5g handle(MsgVo5g msgVo, ChannelHandlerContext ctx) {
        log.info("Received heartbeat：{}", msgVo.getData());
        String data = msgVo.getData();
        String[] split = data.split(",");
//      设备当前时间
        LocalDateTime now = LocalDateTime.now();
        String time = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);
//      开始时间
        String startTime = "0800";
//      结束时间
        String endTime = "2000";
//      间隔
        Integer interval = 1;
        String sn = split[0];

        int isLevelUp = 0;
        String updateUrl = "0";
        int velocity = 0;
        int direction = 0;
        int length = 465;
        boolean isDisable = false;

        String verDir = "/opt/upgradeSns";
        File allUpgrade = new File(verDir + File.separator + "000000");
        if(allUpgrade.exists()){
            //log.info("000000升级!");
            if(!Constants.upSns.contains(sn)){
                String url = readUpgradeUrl(allUpgrade);
                isLevelUp = 1;
                updateUrl = url;
                Constants.upSns.add(sn);
            }

        }else{
            File file = new File(verDir);
            ArrayList<String> sns = new ArrayList<>();
            if(file.exists()){
                File[] snFiles = file.listFiles();
                for (File snFile : snFiles) {
                    sns.add(snFile.getName());
                }
            }
            if(sns.contains(sn)){
                log.info(sn +" upgrade!");
                if(!Constants.upSns.contains(sn)){
                    String url = readUpgradeUrl(new File(verDir + File.separator + sn));
                    if(!"".equals(url)){
                        isLevelUp = 1;
                        updateUrl = url;

                    }
                    Constants.upSns.add(sn);
                }
            }
        }

        return response(msgVo, 0, time, interval, startTime,endTime,velocity,direction,isLevelUp, updateUrl,length, isDisable);

    }

    public MsgVo5g response(MsgVo5g msgVo5g, int code, String time, Integer interval, String startTime,
                            String endTime, int velocity,int direction,int update, String updateUrl, int length, boolean isDisable) {
        MsgVo5g msgvo = new MsgVo5g();
        msgvo.setType(msgVo5g.getType());
        msgvo.setParams(msgVo5g.getType());

        String jarPath = System.getProperty("user.dir");
        String srcPath = jarPath + File.separator + "HeartBeatResponse.json";
        File file = new File(srcPath);
        if(file.exists()){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null){
                        sb.append(line);
                }
                line = sb.toString();
                JSONObject jsonObject = JSONObject.parseObject(line);
                log.info("Response heartbeat：{}", jsonObject);

                StringBuilder data = new StringBuilder();
                //状态码
                data.append(jsonObject.getString("code"));
                data.append(",");
                //时间
                data.append(jsonObject.getString("time"));
                data.append(",");
                //开始时间
                data.append(jsonObject.getString("startTime"));
                data.append(",");
                //结束时间
                data.append(jsonObject.getString("endTime"));
                data.append(",");
                //上传间隔
                data.append(jsonObject.getString("interval"));
                data.append(",");
                //探测速度 高中低模式
                data.append(jsonObject.getString("velocity"));
                data.append(",");
                //检测方向
                data.append(jsonObject.getString("direction"));
                data.append(",");
                //是否升级
                data.append(jsonObject.getIntValue("update"));

                if(jsonObject.getIntValue("update") == 0){
                    data.append(",0");
                }else{
                    //更新的路径
                    data.append(",");
                    data.append(jsonObject.getString("updateUrl"));
                    //url链接包头长度
                    data.append(",");
                    data.append(length);
                }

                if(isDisable) {
                    data.append(",1");
                }else{
                    data.append(",0");
                }
                String s = data.toString();

                msgvo.setData(s);
                msgvo.setLen(s.length());
                msgvo.setCrcHigh(msgVo5g.getCrcHigh());
                msgvo.setCrcLow(msgVo5g.getCrcLow());
                return msgvo;

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        StringBuilder data = new StringBuilder();
//        状态码
        data.append(code);
        data.append(",");
//        时间
        data.append(time);
        data.append(",");
//        开始时间
        data.append(startTime);
        data.append(",");
//        结束时间
        data.append(endTime);
        data.append(",");
//        上传间隔
        data.append(interval);
        data.append(",");
//        探测速度 高中低模式
        data.append(velocity);
        data.append(",");
//        检测方向
        data.append(direction);
        data.append(",");
//        是否升级
        data.append(update);

        if(update == 0){
            data.append(",0");
        }else{
            //更新的路径
            data.append(",");
            data.append(updateUrl);
            //url链接包头长度
            data.append(",");
            data.append(length);
        }

        if(isDisable) {
            data.append(",1");
        }else{
            data.append(",0");
        }
        String s = data.toString();

        msgvo.setData(s);
        msgvo.setLen(s.length());
        msgvo.setCrcHigh(msgVo5g.getCrcHigh());
        msgvo.setCrcLow(msgVo5g.getCrcLow());
        return msgvo;
    }

    public String readUpgradeUrl(File file){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String url = reader.readLine();
            return url;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
