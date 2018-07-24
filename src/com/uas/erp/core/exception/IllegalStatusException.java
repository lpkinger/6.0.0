package com.uas.erp.core.exception;

import com.uas.erp.core.bind.Status;

/**
 * 针对部分操作要求单据的状态必须是在录入或已审核等<br>
 * 操作之前先检测单据状态，如果错误则抛出异常
 * 
 * @author yingp
 * 
 */
public class IllegalStatusException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4218425517031998401L;

	public IllegalStatusException() {
	}

	public IllegalStatusException(String paramString) {
		super(paramString);
	}

	/**
	 * 
	 * 封装成格式为{%s 的状态为 %s}的异常信息并抛出
	 * 
	 * @param paramString
	 *            描述
	 * @param status
	 *            状态
	 */
	public IllegalStatusException(String paramString, Status status) {
		this(String.format("状态错误.%s 的状态为 %s", new Object[] { paramString, status.display() }));
	}

	/**
	 * 
	 * 封装成格式为{%s 的状态为 %s}的异常信息并抛出
	 * 
	 * @param param
	 *            实体
	 * @param status
	 *            状态
	 */
	public IllegalStatusException(Object param, Status status) {
		this(param.toString(), status);
	}

	public IllegalStatusException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public IllegalStatusException(Throwable paramThrowable) {
		super(paramThrowable);
	}

}
