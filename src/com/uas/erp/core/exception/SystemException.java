package com.uas.erp.core.exception;

/**
 * 系统程序执行异常
 * 
 * @author yingp
 * 
 */
public class SystemException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4218425517031998401L;

	public SystemException() {
	}

	public SystemException(String paramString) {
		super(paramString);
	}

	public SystemException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public SystemException(Throwable paramThrowable) {
		super(paramThrowable);
	}

}
