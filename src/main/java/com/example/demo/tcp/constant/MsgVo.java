package com.example.demo.tcp.constant;



public class MsgVo {

	/**
	 * 起始字节3个字节
	 */
	private byte[] head = new byte[] { (byte) 0xFA, (byte) 0xF5, (byte) 0xF6 };
	/**
	 * 流水号 2个字节
	 */
	private int seq;
	/**
	 * 消息类型 1个字节
	 */
	private byte type;
	/**
	 * 包体 数据长度 4个字节
	 */
	private int len;
	/**
	 * 包体 包数据
	 */
	private String data;
	/**
	 * 结束字节 3 个字节
	 */
	private byte[] tail = new byte[] { (byte) 0xFA, (byte) 0xF6, (byte) 0xF5 };

	public MsgVo() {

	}

	public MsgVo(byte[] head, int seq, byte type, int len, String data, byte[] tail) {
		this.head = head;
		this.seq = seq;
		this.type = type;
		this.len = len;
		this.data = data;
		this.tail = tail;
	}

	public byte[] getHead() {
		return head;
	}

	public void setHead(byte[] head) {
		this.head = head;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
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

	public byte[] getTail() {
		return tail;
	}

	public void setTail(byte[] tail) {
		this.tail = tail;
	}

	public MsgVo(short seq, byte type, int len, String data) {
		this.seq = seq;
		this.type = type;
		this.len = len;
		this.data = data;
	}

	public static String addXmlHead(String content) {
		return "<?xml version=\"1.0\" encoding=\" UTF-8\" ?>" + content;
	}
}
