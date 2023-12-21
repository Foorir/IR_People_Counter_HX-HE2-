package com.example.demo.tcp.constant;

public enum CmdType {

	DATA_UPLOAD((byte) 0x21, "数据上报"),
	TIME_SYNC((byte) 0x22, "时间同步"),
	TRAVEL_TRACK_FLOW((byte) 0x23,"行程轨迹"),
	;

	private byte typeCode;
	private String descs;

	CmdType(byte typeCode, String descs) {
		this.typeCode = typeCode;
		this.descs = descs;
	}

	public static String getDescsByCode(byte typeCode) {
		CmdType[] enums = CmdType.values();
		for (CmdType e : enums) {
			if (e.typeCode == typeCode) {
				return e.descs;
			}
		}
		return null;
	}

	public byte getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(byte typeCode) {
		this.typeCode = typeCode;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
	
	

}
