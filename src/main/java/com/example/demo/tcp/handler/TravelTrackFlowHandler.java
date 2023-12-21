package com.example.demo.tcp.handler;


import com.example.demo.tcp.annotation.RequestHandler;
import com.example.demo.tcp.constant.CmdType;
import com.example.demo.tcp.constant.MsgVo;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lzl
 * @description:
 * @Package com.dqwl.environmental.monitor.tcp.handler
 * @date 2021/12/27 9:16
 */
@Slf4j
@Component
@RequestHandler(type = CmdType.TRAVEL_TRACK_FLOW)
public class TravelTrackFlowHandler<T> implements BaseHandler {
//
//    @Resource
//    SysUserService sysUserService;
//
//    @Resource
//    DeviceGatewayDao deviceGatewayDao;
//
//    @Resource
//    EntityFlowService entityFlowService;

    @Override
    public MsgVo handle(MsgVo msgVo, ChannelHandlerContext ctx) {
//        TravelTrackFlowVO travelTrackFlowVO = JSONObject.parseObject(msgVo.getData(),TravelTrackFlowVO.class);
//        log.info(travelTrackFlowVO.toString());
//        EntityFlowEntity entityFlow = new EntityFlowEntity();
//        BeanUtil.copyProperties(travelTrackFlowVO,entityFlow);



//        entityFlow.setCreateTime(new Date());
//        DeviceGatewayEntity device = deviceGatewayDao.selectOne(
//                new QueryWrapper<DeviceGatewayEntity>().lambda().eq(DeviceGatewayEntity::getDeviceSn,travelTrackFlowVO.getDeviceSn()));
//        if (device == null) {
//                找不到此设备,发送错误信息
//            log.warn("平台不存在该设备：{}", travelTrackFlowVO.getDeviceSn());
//            return response(travelTrackFlowVO.getDeviceSn(), 2, msgVo);
//        }
//            为绑定出入口或者实体,发送错误信息
//        if (device.getDoorId() == null || device.getEntityId() == null) {
//            log.warn("该设备未绑定门店：{}", travelTrackFlowVO.getDeviceSn());
//            return response(travelTrackFlowVO.getDeviceSn(), 2, msgVo);
//        }
//        String timeZone = sysUserService.getById(device.getCreateUserId()).getTimeZone();
//        if (StringUtils.isNotBlank(timeZone)) {
//            entityFlow.setCreateTime(TimeZoneUtils.getDate(new Date(), timeZone));
//        }
//        将设备数据的名称与时间点放入map中
//        Map<String, Object> params = new HashMap<>();
//        params.put("name", entityFlow.getDeviceSn());
//        params.put("timepoint", DateUtils.formatDateTime(entityFlow.getTimepoint()));
//            通过此数据进行查询有无重复数据,entityFlowService可以替换为各自网关设备人流数据记录表所对应的服务接口
//        List<EntityFlowEntity> entityFlowEntities = entityFlowService.queryAll(params);
//        if (entityFlowEntities.size() > 0) {
//                若已存在数据,则发送错误信息
//            log.warn("上传失败,记录已存在：deviceSn={},time={}", travelTrackFlowVO.getDeviceSn(), entityFlow.getTimepoint());
//            return response(travelTrackFlowVO.getDeviceSn(), 0, msgVo);
//        }
//        设置设备对应的实体id和门id
//        entityFlow.setEntityId(Long.valueOf(device.getEntityId()));
//        entityFlow.setDoorId(device.getDoorId());
//            在人流数据表中保存此数据,entityFlowService可以替换为各自网关设备人流数据记录表所对应的服务接口
//        boolean b = entityFlowService.save(entityFlow);
//        if(b){
//            return response(travelTrackFlowVO.getDeviceSn(),0,msgVo);
//        }
//        log.warn("上传失败,参数为:{}",entityFlow.toString());
//        return response(travelTrackFlowVO.getDeviceSn(), 1, msgVo);

        return response("123",1,msgVo);
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
        sb.append("{");
        sb.append("deviceSn:\"").append(deviceId).append("\"");
        sb.append("code:\"").append(ret).append("\"");
        sb.append("}");
        String pakBody = sb.toString();
        responseMsg.setLen(pakBody.length());
        responseMsg.setData(pakBody);
        return responseMsg;
    }
}
