package com.uas.erp.core.bind;

import com.uas.erp.core.BaseUtil;

/**
 * 系统常量
 * 
 * @author yingp
 * 
 */
public class Constant {

	// 日期格式

	public final static String YM = "yyyy-MM";

	public final static String ym = "yyyyMM";

	public final static String YMD = "yyyy-MM-dd";

	public final static String YMD_HM = "yyyy-MM-dd HH:mm";

	public final static String YMD_HMS = "yyyy-MM-dd HH:mm:ss";

	public final static String ORACLE_YMD = "yyyy-MM-dd";

	public final static String ORACLE_YMD_HMS = "yyyy-MM-dd HH24:mi:ss";

	public static final String REGEXP_MOBILE = "^[1|8][3-9]\\d{9}$|^([6|9])\\d{7}$|^[0][9]\\d{8}$|^[6]([8|6])\\d{5}$|^(886|0)[9]\\d{8}$";

	public static final String REGEXP_EMAIL = "^([\\w-])+(\\.\\w+)*@([\\w-])+((\\.\\w{2,3}){1,3})$";

	/**
	 * 求和
	 * */
	public final static String SUMMARY_SUM = "sum";

	/**
	 * 最大值
	 * */
	public final static String SUMMARY_MAX = "max";
	/**
	 * 最小值
	 * */
	public final static String SUMMARY_MIN = "min";
	/**
	 * 平均值
	 * */
	public final static String SUMMARY_AVERAGE = "average";

	/**
	 * 是
	 */
	public static final short YES = 1;
	/**
	 * 是
	 */
	public static final short yes = -1;
	/**
	 * 是
	 */
	public static final String TRUE = "T";
	/**
	 * 否
	 */
	public static final short NO = 0;
	/**
	 * 否
	 */
	public static final String FALSE = "F";

	public static final int ORACLE_MAX_TABLE_SIZE = 999;

	/**
	 * 优软云
	 * */
	public static final String UAS_CLOUD = "CLOUD";

	/**
	 * @return 管理平台地址
	 */
	public static String manageHost() {
		return BaseUtil.getXmlSetting("host.manage");
	}

	/**
	 * @return B2B平台地址
	 */
	public static String b2bHost() {
		return BaseUtil.getXmlSetting("host.b2b");
	}

	/**
	 * @return 正式B2C平台地址
	 */
	public static String b2cHost() {
		return BaseUtil.getXmlSetting("host.b2c");
	}

	/**
	 * @return 测试B2c平台地址
	 */
	public static String b2cTestHost() {
		return BaseUtil.getXmlSetting("host.b2ctest");
	}
	/**
	 * @return 公共物料库正式地址
	 */
	public static String publicProductHost() {
		return BaseUtil.getXmlSetting("host.publicProduct");
	}
	/**
	 * 类型--"是否"字段
	 */
	public static final String TYPE_YN = "yn";

	/**
	 * 类型--"下拉框"
	 */
	public static final String TYPE_COMBO = "combo";

	/**
	 * 正则表达式：数字
	 */
	public static final String REG_NUM = "^-?[0-9]+(.[0-9]+)?";

	/**
	 * 正则表达式：日期
	 */
	public static final String REG_DATE = "\\d{2,4}-\\d{1,2}-\\d{1,2}";

	/**
	 * 正则表达式：时间
	 */
	public static final String REG_DATETIME = "\\d{2,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}";

	/**
	 * excel导出阈值
	 */
	public static final int EXCEL_LG_SIZE = 5000;

	/**
	 * excel导出最大条数
	 */
	public static final int EXCEL_MAX_SIZE = 100000;

	/**
	 * 正则表达式：ipv4
	 */
	public static final String REG_IPV4 = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
	
	/**
	 * 临时表名称
	 */
	public static final String TEMP_TABLE_NAME = "TEMP_TABLE";

}
