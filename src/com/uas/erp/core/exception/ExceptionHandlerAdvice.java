package com.uas.erp.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLRecoverableException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.ErrorCode;
import com.uas.erp.core.logging.BufferedLoggerManager;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.common.impl.DebugBufferedLogger;
import com.uas.mes.api.core.BaseApiController;

/**
 * <p>
 * 基于Application的异常处理,以AOP的形式注册到SpringMVC的处理链
 * </p>
 * <p>
 * 正常的业务流程,只需抛出对应的异常和相关的信息
 * </P>
 * <p>
 * 不同的错误，对应不同的方法来处理
 * </p>
 * 
 * @author yingp
 * 
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {

	@Autowired
	private BaseDao baseDao;

	private final static Logger logger = Logger.getLogger(ExceptionHandlerAdvice.class);

	/**
	 * 异步记录debug日志的工具
	 */
	private DebugBufferedLogger debugLogger = BufferedLoggerManager.getLogger(DebugBufferedLogger.class);

	/**
	 * 处理未被发现处理的运行时抛出异常
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ModelMap handleUnexpectedServerError(RuntimeException ex, HttpServletRequest request) {
		ModelMap map = new ModelMap();
		if (!"ERR_NETWORK_SESSIONOUT".equals(ex.getMessage())) {
			logger.error(ex);
			ex.printStackTrace();
			map.put("exceptionInfo", getErrorStack(request, ex, "程序错误"));
		} else {
			map.put("exceptionInfo", ex.getMessage());
		}
		logErrorDebug(request);
		return map;
	}

	/**
	 * 处理通过BaseUtil.showError抛出的异常
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(SystemException.class)
	@ResponseBody
	public ResponseEntity<ModelMap> handleSystemError(SystemException ex, HttpServletRequest request) {
		ModelMap map = new ModelMap();
		if (!"ERR_NETWORK_SESSIONOUT".equals(ex.getMessage())) {
			logger.error(ex);
		}
		map.put("exceptionInfo", ex.getMessage());
		logErrorDebug(request);
		// TODO
		// debug: AFTERSUCCESS不返回500
		HttpStatus status = ex.getMessage().startsWith("AFTERSUCCESS") ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return new ResponseEntity<ModelMap>(map, status);
	}

	/**
	 * 处理thread.interrupt抛出的异常
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(InterruptedException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ModelMap handleInterruptedExceptionError(InterruptedException ex, HttpServletRequest request) {
		ModelMap map = new ModelMap();
		map.put("exceptionInfo", "处理超时");
		logTimeoutDebug(request);
		return map;
	}

	/**
	 * 处理连接池的连接失效抛出异常
	 * 
	 * @see SQLRecoverableException
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(SQLRecoverableException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ModelMap handleSQLRecoverableExceptionError(SQLRecoverableException ex, HttpServletRequest request) {
		ModelMap map = new ModelMap();
		map.put("exceptionInfo", getErrorStack(request, ex, "连接异常"));
		logErrorDebug(request);
		return map;
	}

	/**
	 * 违反唯一约束条件抛出异常
	 * 
	 * @see DuplicateKeyException
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(DuplicateKeyException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ModelMap handleDuplicateKeyExceptionError(DuplicateKeyException ex, HttpServletRequest request) {
		// 取得唯一约束条件
		String cause = ex.getCause().toString();
		String causeIndex = cause.substring(cause.lastIndexOf("(") + 1, cause.lastIndexOf(")"));
		if (causeIndex.contains("."))
			causeIndex = causeIndex.substring(causeIndex.indexOf(".") + 1);
		String desc = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(comments) from user_ind_columns a left join user_col_comments b on a.table_name=b.table_name and a.column_name=b.column_name where index_name=? ",
						String.class, causeIndex);
		if (desc == null) {
			desc = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(comments) from user_cons_columns a left join user_col_comments b on a.table_name=b.table_name and a.column_name=b.column_name where constraint_name=? ",
							String.class, causeIndex);
		}
		if (desc == null)
			desc = "违反唯一约束条件";
		else
			desc = "(" + desc + ") 重复";
		ModelMap map = new ModelMap();
		ex.printStackTrace();
		map.put("exceptionInfo", desc);
		logErrorDebug(request);
		return map;
	}

	/**
	 * 处理参数错误抛出异常
	 * 
	 * @see IllegalArgumentException
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ModelMap handleIllegalArgumentExceptionError(IllegalArgumentException ex, HttpServletRequest request) {
		logger.error(ex.getCause());
		ex.printStackTrace();
		ModelMap map = new ModelMap();
		map.put("exceptionInfo", getErrorStack(request, ex, "参数错误"));
		logErrorDebug(request);
		return map;
	}

	/**
	 * SQL语法错误
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(BadSqlGrammarException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ModelMap handleBadSqlGrammarExceptionError(BadSqlGrammarException ex, HttpServletRequest request) {
		logger.error(ex);
		ModelMap map = new ModelMap();
		ex.printStackTrace();
		map.put("exceptionInfo", getErrorStack(request, ex, "程序错误"));
		logErrorDebug(request);
		return map;
	}

	static final String VALUE_TOO_LARGE_CAUSE_CN = "java.sql.SQLException: ORA-12899: 列 \"%s\".\"%s\".\"%s\" 的值太大 (实际值: %s, 最大值: %s)";

	static final String VALUE_TOO_LARGE_CAUSE_EN = "java.sql.SQLException: ORA-12899: value too large for column \"%s\".\"%s\".\"%s\" (actual: %s, maximum: %s)";

	/**
	 * SQL查询语句异常
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(UncategorizedSQLException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ModelMap handleUncategorizedSQLExceptionError(UncategorizedSQLException ex, HttpServletRequest request) {
		logger.error(ex);
		ex.printStackTrace();
		int errorCode = ex.getSQLException().getErrorCode();
		String exInfo = getErrorStack(request, ex, "程序错误");
		// 值太大
		if (errorCode == ErrorCode.VALUE_TOO_LARGE.code()) {
			String cause = ex.getCause().toString();
			String[] params = StringUtil.parse(cause, StringUtil.hasChinese(cause) ? VALUE_TOO_LARGE_CAUSE_CN : VALUE_TOO_LARGE_CAUSE_EN);
			String desc = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select nvl(b.comments,a.column_name)  from User_Tab_Columns a left join  User_Col_Comments b on A.Table_Name=B.Table_Name and A.Column_Name=B.column_name where a.table_name=? and a.column_name=?",
							String.class, params[1], params[2]);
			if (desc.toUpperCase().toString().equals(params[2].toUpperCase().toString())) {
				desc = params[1] + "." + params[2];
			} else {
				desc += "(" + params[1] + "." + params[2] + ")";
			}
			exInfo = String.format("您填写的数据<u> %s </u>值太大,当前长度: %s,允许最大长度: %s", desc, params[3], params[4]);
		}
		ModelMap map = new ModelMap();
		map.put("exceptionInfo", exInfo);
		logErrorDebug(request);
		return map;
	}

	/**
	 * debug日志里面状态改为执行失败
	 * 
	 * @param request
	 */
	private void logErrorDebug(HttpServletRequest request) {
		if ("true".equals(BaseUtil.getXmlSetting("debug"))) {
			Object debugId = request.getAttribute(DebugBufferedLogger.debugAttribute);
			if (debugId != null)
				debugLogger.failure(request, debugId.toString());
		}
	}

	/**
	 * debug日志里面状态改为执行超时
	 * 
	 * @param request
	 */
	private void logTimeoutDebug(HttpServletRequest request) {
		if ("true".equals(BaseUtil.getXmlSetting("debug"))) {
			Object debugId = request.getAttribute(DebugBufferedLogger.debugAttribute);
			if (debugId != null)
				debugLogger.timeout(request, debugId.toString());
		}
	}

	/**
	 * 封装错误信息，按用户设置是否显示栈信息
	 * 
	 * @param e
	 * @param defaultText
	 * @return
	 */
	private String getErrorStack(HttpServletRequest request, Exception e, String defaultText) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return "<div class=\"error-container\">"
				+ "<a class=\"error-toggle\" onclick=\"document.getElementById('_error_stack').style.display='block';\">"
				+ defaultText + "</a>" + "<div id=\"_error_stack\" class=\"error-body\" style=\"display:none;\">"
				+ writer.toString() + "</div>" + "</div>";
	}
	
	/**
	 * 处理API错误异常
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(APIErrorException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ModelMap handleAPIErrorException(APIErrorException ex, HttpServletRequest request) {
		logger.error(ex);
		ex.printStackTrace();
		ModelMap map = new ModelMap();
		int code = ex.getCode().getValue();
		String message = ex.getMessage();
		map.put(BaseApiController.KEY_SUCCESS, false);
		map.put(BaseApiController.KEY_EXCEPTION_CODE, code);
		map.put(BaseApiController.KEY_EXCEPTION_INFO, message);
		return map;
	}
}
