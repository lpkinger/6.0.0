package com.uas.erp.core.support;

import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.exception.SystemException;

/**
 * 对参数进行合法性检查<br>
 * 如果参数不符合要求，方法将通过抛出异常的方式中断
 * 
 * @author yingp
 * 
 */
public class Assert {

	/**
	 * 检测实际值是否相等期望值 <br>
	 * 如果不相等则抛出异常
	 * 
	 * @param message
	 *            异常信息
	 * @param expected
	 *            期望值
	 * @param actual
	 *            实际值
	 */
	public static void isEquals(String message, Object expected, Object actual) {
		if ((expected == null) && (actual == null))
			return;
		if ((expected != null) && (expected.equals(actual)))
			return;
		if (message == null)
			message = String.format("实际值与期望值不符。期望值%s 实际值 %s", new Object[] { expected, actual });
		else
			message = BaseUtil.getLocalMessage(message);
		throw new SystemException(message);
	}

	/**
	 * 检测参数不能为 null或空白字符
	 * 
	 * @param text
	 *            参数
	 * @param message
	 *            异常信息
	 */
	public static void hasText(String text, String message) {
		if (!(StringUtils.hasText(text)))
			throw new SystemException(BaseUtil.getLocalMessage(message));
	}

	/**
	 * 检测表达式是否为true <br>
	 * 如果非true，则抛出异常
	 * 
	 * @param expression
	 * @param message
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression)
			throw new SystemException(BaseUtil.getLocalMessage(message));
	}

}
