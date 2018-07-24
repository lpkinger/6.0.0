package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import com.uas.erp.core.DateUtil;
import com.uas.erp.model.InitData;
import com.uas.erp.model.InitDetail;
import com.uas.erp.model.InitNodes;

/**
 * 
 * 初始化校验
 * 
 * @author yingp
 */
public class CheckUtil {

	public static final String UNIQUE = "unique";
	public static final String TRIM = "trim";
	public static final String COMBO = "combo";
	public static final String DIFFENCE = "diffence";
	public static final String ACCORD = "accord";
	public static final String MIN_VALUE = "minValue";
	public static final String UPPER = "upper";

	public static final String VARCHAR = "varchar2";
	public static final String NUMBER = "number";
	public static final String DATE = "date";

	public static final String DATE_REG1 = "\\d{4}-\\d{1,2}-\\d{1,2}";
	public static final String DATE_REG2 = "\\d{4}/\\d{1,2}/\\d{1,2}";
	public static final String TIME_REG = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}";

	public StringBuffer errorNodes;
	public List<InitNodes> nodes;

	private int ilid;
	private List<InitDetail> inits;
	private List<InitData> datas;
	private final ExecutorService threadPool;
	private final static int size_pre_thread = 5000;

	public CheckUtil() {
		// 定义一个缓冲的线程值 线程池的大小根据任务变化
		threadPool = Executors.newCachedThreadPool();
	}

	public CheckUtil(int ilid, List<InitDetail> inits, List<InitData> datas) {
		this();
		this.errorNodes = new StringBuffer();
		this.nodes = new ArrayList<InitNodes>();
		this.ilid = ilid;
		this.inits = inits;
		this.datas = datas;
		process();
	}

	/**
	 * 
	 */
	private void process() {
		for (int i = 0, size = this.datas.size(), count = (int) Math.ceil((float) size / size_pre_thread); i < count; i++) {
			threadPool.execute(new Checker(datas.subList(i * size_pre_thread, Math.min((i + 1) * size_pre_thread, size))));
		}
		threadPool.shutdown();
		try {
			// 设置最长等待20秒
			threadPool.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {

		}
	}

	/**
	 * 错误信息
	 */
	public StringBuffer getErrorNodes() {
		return errorNodes;
	}

	/**
	 * 待校验的unique和accord类型单元格
	 */
	public List<InitNodes> getNodes() {
		return nodes;
	}

	/**
	 * 将需要unique和accord校验的单元格的信息记录下来，
	 */
	private synchronized void logNodes(String table, String logic, String field, String value, int id, int type) {
		this.nodes.add(new InitNodes(table, ilid, logic, field, value, id, type));
	}

	/**
	 * 记录错误单元格信息
	 * 
	 * @param key
	 *            InitData.id_id
	 * @param field
	 *            字段名
	 */
	public synchronized void logError(int key, String field) {
		errorNodes.append(",");
		errorNodes.append(key);
		errorNodes.append(":");
		errorNodes.append(field);
	}

	public static boolean isVarcharType(String type) {
		return type.startsWith(CheckUtil.VARCHAR);
	}

	public static boolean isNumberType(String type) {
		return type.startsWith(CheckUtil.NUMBER);
	}

	public static boolean isDateType(String type) {
		return type.startsWith(CheckUtil.DATE);
	}

	/**
	 * 检测工具
	 * 
	 * @author yingp
	 *
	 */
	private class Checker implements Runnable {

		private final List<InitData> datas;
		private JSONObject currentData;

		public Checker(List<InitData> datas) {
			this.datas = datas;
		}

		@Override
		public void run() {
			if (this.datas == null)
				return;
			String table = null;
			String field = null;
			Object val = null;
			String value = null;
			String logic = null;
			String type = null;
			int key = 0;
			for (InitData d : this.datas) {
				currentData = JSONObject.fromObject(d.getId_data());
				key = d.getId_id();
				for (InitDetail t : inits) {
					table = t.getId_table();
					field = t.getId_field();
					val = currentData.get(field);
					if(val==null||val instanceof net.sf.json.JSONNull){
						value = null;
					}else{						
						value = ((val == null || ((String) val).trim().equals("")) ? null : val.toString());
					}
					// 必填
					if ((value == null || "".equals(value)) && t.getId_need() == 1) {
						logError(key, field);
						continue;
					}
					logic = t.getId_logic();
					type = t.getId_type();
					// 先验证基本数据类型
					if (type != null && isTypeOf(type, value)) {
						// 再验证逻辑表达式
						if (logic != null && !isExpOf(table, logic, field, value, key)) {
							logError(key, field);
						}
					} else {
						logError(key, field);
					}
				}
			}
		}

		/**
		 * 数据类型校验
		 * 
		 * @param type
		 *            字段类型
		 */
		public boolean isTypeOf(String type, String value) {
			if (value == null)
				return true;
			if (isVarcharType(type)) {
				return checkVarchar(type, value);
			} else if (isNumberType(type)) {
				return checkNumber(type, value);
			} else if (isDateType(type)) {
				return checkDate(type, value);
			}
			return true;
		}

		public boolean checkVarchar(String type, String value) {
			if (type.length() > CheckUtil.VARCHAR.length()) {
				int max = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.lastIndexOf(")")));
				return max > value.length();
			}
			return true;
		}

		/**
		 * 校验日期型的格式 配置date(),date(yyyy-mm-dd),date(yyyy/mm/dd)...
		 * 
		 * @param type
		 * @param value
		 * @return
		 */
		public boolean checkDate(String type, String value) {
			if (type.length() > CheckUtil.DATE.length() && value.length() > 0) {
				String reg = type.substring(type.indexOf("(") + 1, type.lastIndexOf(")"));
				if (reg.length() > 0)
					return value.matches(reg.replace("yyyy", "\\d{4}").replace("MM", "\\d{1,2}").replace("mm", "\\d{1,2}")
							.replace("dd", "\\d{1,2}").replace("HH", "\\d{1,2}").replace("ss", "\\d{1,2}"));
				return (value.matches(DATE_REG1) && DateUtil.isValidDate(value, "yyyy-MM-dd"))
						|| (value.matches(DATE_REG2) && DateUtil.isValidDate(value, "yyyy/MM/dd"))			
						|| (value.matches(TIME_REG) && DateUtil.isValidDate(value, "yyyy-MM-dd HH:mm:ss"));
			}
			return true;
		}

		/**
		 * 校验number类型 例如 number(7,1) 拼出正则表达式 ^(\\d{1,6})(\\.\\d{1})?$ 进行比较
		 */
		public boolean checkNumber(String type, String value) {
			if (type.length() > CheckUtil.NUMBER.length()) {
				int max = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.lastIndexOf(",")));
				int sMax = Integer.parseInt(type.substring(type.lastIndexOf(",") + 1, type.lastIndexOf(")")));
				StringBuffer sb = new StringBuffer("^-?(\\d{0,");
				sb.append(max - sMax);
				sb.append("})(\\.\\d{0,");
				sb.append(sMax);
				sb.append("})?$");
				return value.matches(sb.toString());
			}
			return value.matches("^-?(\\d{0,30})(\\.\\d{0,20})?$");
		}

		/**
		 * 表达式校验
		 * 
		 * @param exps
		 *            表达式 ，多个之间用;隔开
		 */
		public boolean isExpOf(String table, String exps, String field, String value, int key) {
			String[] es = exps.split(";");
			boolean bool=true;
			for (String exp : es) {
				if (exp != null) {
					if (exp.startsWith(CheckUtil.UNIQUE)) {
						if (value != null && value.trim().length() > 0) {
							logNodes(table, exp, field, value, key, 0);
						}
					} else if (exp.startsWith(CheckUtil.TRIM)) {
						bool=bool && checkTrim(exp, value);
					} else if (exp.startsWith(CheckUtil.COMBO)) {
						bool=bool && checkCombo(exp, value);
					} else if (exp.startsWith(CheckUtil.DIFFENCE)) {
						bool=bool && checkDiffence(exp, value);
					} else if (exp.startsWith(CheckUtil.ACCORD)) {
						if (value != null && value.trim().length() > 0) {
							logNodes(table, exp, field, value, key, 1);
						}
					} else if (exp.startsWith(CheckUtil.MIN_VALUE)) {
						bool=bool && checkMinValue(exp, value);
					} else
						continue;
				}
			}
			return bool;
		}

		/**
		 * 是否有特殊字符
		 * 
		 * @param exp
		 *            trim(&,')
		 * @return true 否
		 */
		public boolean checkTrim(String exp, String value) {
			if (value != null) {
				exp = exp.substring(exp.indexOf("(") + 1, exp.lastIndexOf(")")).replaceAll(",", "|");
				return !matcher(value, exp);
			}
			return true;
		}

		public boolean matcher(String str, String regx) {
			if (str != null) {
				str = str.trim();
				Pattern p = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(str);
				return m.find();
			}
			return false;
		}

		/**
		 * 是否为指定选择内容
		 * 
		 * @param exp
		 *            combo(MRP,MPS)
		 * @return true 是
		 */
		public boolean checkCombo(String exp, String value) {
			if (value != null && !"".equals(value.trim())) {
				boolean bool=false;
				exp = exp.substring(exp.indexOf("(") + 1, exp.lastIndexOf(")"));
				String[] exps=exp.split(",");
				for (String e : exps) {
					if (e != null && !"".equals(e.trim())) {
						bool=bool || (value.equals(e));
						if(bool) return true;
					}
				}
				return bool;
			}
			return true;
		}

		public boolean checkDiffence(String exp, String value) {
			if (value != null) {
				exp = exp.substring(exp.indexOf("(") + 1, exp.lastIndexOf(")"));
				return !value.equals(currentData.get(exp));
			}
			return true;
		}

		public boolean checkMinValue(String exp, String value) {
			if (value != null && value.trim().length() > 0) {
				value = value.replace(",", "");
				exp = exp.substring(exp.indexOf("(") + 1, exp.lastIndexOf(")"));
				return Double.parseDouble(value) >= Double.parseDouble(exp);
			}
			return true;
		}

	}
}
