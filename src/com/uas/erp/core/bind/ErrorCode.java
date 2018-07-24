package com.uas.erp.core.bind;

/**
 * oracle 错误码
 * 
 * @author yingp
 * 
 */
public enum ErrorCode {

	/**
	 * {@code VendorCode 12899 值太大 试图插入超出列允许范围的的值 } <br>
	 * {@code SQLState 72000}
	 */
	VALUE_TOO_LARGE(12899, "值太大"),INVALID_IDENTIFIER(904,"标识符无效");
	
	
	private final int code;

	private final String message;

	private ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int code() {
		return code;
	}

	public String message() {
		return message;
	}

	public static ErrorCode valueOf(int code) {
		for (ErrorCode error : values()) {
			if (error.code == code) {
				return error;
			}
		}
		throw new IllegalArgumentException("没有与编号 [" + code + "]匹配的错误");
	}

}
