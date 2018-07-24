package com.uas.erp.core.exception;

/**
 * API错误异常
 * 
 * @author suntg
 * @since 2016年11月30日下午4:44:21
 */
public class APIErrorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum APIErrorCode {

		/**
		 * 用户未验证
		 */
		NOT_AUTHENTED(1001),
		/**
		 * 非法参数
		 */
		ILLEGAL_ARGUMENTS(1002),
		/**
		 * 单据状态不合法
		 */
		INVALID_ORDER_STATUS(1003),
		/**
		 * 数据未找到
		 */
		DATA_NOT_FOUND(1004),
		/**
		 * 业务错误
		 */
		BUSINESS_FAILED(2001);

		private APIErrorCode(int code) {
			this.code = code;
		}

		private int code;

		public int getValue() {
			return code;
		}
	}

	/**
	 * 
	 * @param code
	 *            状态码
	 * @param message
	 *            错误描述
	 */
	public APIErrorException(APIErrorCode code, String message) {
		super(message);
		this.code = code;
	}

	private APIErrorCode code;

	public APIErrorCode getCode() {
		return code;
	}

	public void setCode(APIErrorCode code) {
		this.code = code;
	}

}
