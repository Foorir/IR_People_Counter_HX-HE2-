package com.example.demo.tcp5g.msg;

/**
 * @author TRH
 * @description:
 * @Package com.example.demo.tcp5g.msg
 * @date 2023/3/27 16:12
 */
public class MsgVo5g {

    /**
     * 起始字节1个字节
     */
    private byte head = (byte) 0x7E;
    /**
     * 设备sn4个字节
     */
    private byte[] sn = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
    /**
     * 消息指令类型 1个字节
     */
    private byte type;
    /**
     * 参数部分 1个字节
     */
    private byte params;
    /**
     * 包体 数据长度 2个字节
     */
    private int len;
    /**
     * 包体 包数据
     */
    private String data;

    /**
     * crc数据校验低
     */
    private byte    crcLow;

    /**
     * crc数据校验 高
     */
    private byte    crcHigh;

    /**
     * 帧尾
     */
    private Byte   tail= (byte) 0x7D;

    public MsgVo5g() {
    }

    public MsgVo5g(byte head, byte[] sn, byte type, byte params, int len, String data, byte crcLow, byte crcHigh, Byte tail) {
        this.head = head;
        this.sn = sn;
        this.type = type;
        this.params = params;
        this.len = len;
        this.data = data;
        this.crcLow = crcLow;
        this.crcHigh = crcHigh;
        this.tail = tail;
    }

    public byte getHead() {
        return head;
    }

    public void setHead(byte head) {
        this.head = head;
    }

    public byte[] getSn() {
        return sn;
    }

    public void setSn(byte[] sn) {
        this.sn = sn;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getParams() {
        return params;
    }

    public void setParams(byte params) {
        this.params = params;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte getCrcLow() {
        return crcLow;
    }

    public void setCrcLow(byte crcLow) {
        this.crcLow = crcLow;
    }

    public byte getCrcHigh() {
        return crcHigh;
    }

    public void setCrcHigh(byte crcHigh) {
        this.crcHigh = crcHigh;
    }

    public Byte getTail() {
        return tail;
    }

    public void setTail(Byte tail) {
        this.tail = tail;
    }
}
