package com.uas.erp.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.uas.erp.core.bind.Constant;

public class DateUtil {
	static final SimpleDateFormat ym = new SimpleDateFormat("yyyyMM");
	static final SimpleDateFormat YM = new SimpleDateFormat("yyyy-MM");
	static final SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");
	static final SimpleDateFormat YMD_HMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String format(Date date, String f) {
		if (date == null) {
			date = new Date();
		}
		if (f == null) {
			return YMD.format(date);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(f);
		return sdf.format(date);
	}

	public static String format(java.sql.Date date, String f) {
		if (date == null) {
			return currentDateString(f);
		} else {
			if (f == null) {
				return YMD.format(date);
			}
			SimpleDateFormat sdf = new SimpleDateFormat(f);
			return sdf.format(date);
		}
	}

	public static Date parse(String date, String f) {
		if (date == null) {
			return new Date();
		}
		if (f == null) {
			try {
				return date.contains(" ") ? YMD_HMS.parse(date) : YMD.parse(date);
			} catch (ParseException e) {
				return new Date();
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat(f);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			return new Date();
		}
	}

	/**
	 * 获取日期年份
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static int getYear(String date) throws ParseException {
		date = date == null ? format(null, null) : date;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(YMD.parse(date));
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取日期年份
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static int getYear(Date date) throws ParseException {
		date = date == null ? parse(null, null) : date;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取日期月份
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static int getMonth(String date) throws ParseException {
		date = date == null ? format(null, null) : date;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(YMD.parse(date));
		return (calendar.get(Calendar.MONTH) + 1);
	}

	/**
	 * 获取日期月份
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static int getMonth(Date date) throws ParseException {
		date = date == null ? parse(null, null) : date;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return (calendar.get(Calendar.MONTH) + 1);
	}

	/**
	 * 获取日期号
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static int getDay(String date) throws ParseException {
		date = date == null ? format(null, null) : date;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(YMD.parse(date));
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取日期号
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static int getDay(Date date) throws ParseException {
		date = date == null ? parse(null, null) : date;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取日期前一年日期
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getLastYearDay(String date) throws ParseException {
		date = date == null ? format(null, null) : date;
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(YMD.parse(date));
		} catch (ParseException e) {
			calendar.setTime(new Date());
		}
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
		return YMD.format(calendar.getTime());

	}

	/**
	 * 获取月份起始日期
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getMinMonthDate(String date) {
		date = date == null ? format(null, null) : date;
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(YMD.parse(date));
		} catch (ParseException e) {
			calendar.setTime(new Date());
		}
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return YMD.format(calendar.getTime());
	}

	/**
	 * 获取月份起始日期
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getMinMonthDate(Date date) {
		date = date == null ? parse(null, null) : date;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return YMD.format(calendar.getTime());
	}

	/**
	 * 获取月份最后日期
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getMaxMonthDate(String date) {
		date = date == null ? format(null, null) : date;
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(YMD.parse(date));
		} catch (ParseException e) {
			calendar.setTime(new Date());
		}
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return YMD.format(calendar.getTime());
	}

	/**
	 * 获取月份最后日期
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getMaxMonthDate(Date date) {
		date = date == null ? parse(null, null) : date;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return YMD.format(calendar.getTime());
	}

	/**
	 * 当前时间
	 * 
	 * @return <h3>String</h3>
	 */
	public static String getCurrentDate() {
		return format(new Date(), null);
	}

	/**
	 * 截取指定日期的年月
	 * 
	 * @param date
	 * @return
	 */
	public static Integer getYearmonth(Date date) {
		if (date == null) {
			date = new Date();
		}
		return Integer.parseInt(ym.format(date));
	}

	/**
	 * 截取指定日期的年月
	 * 
	 * @param date
	 * @return
	 */
	public static Integer getYearmonth(String date) {
		return Integer.parseInt(ym.format(parse(date, null)));
	}

	/**
	 * 获取当前年月
	 * 
	 * @return
	 */
	public static Integer getYearmonth() {
		return Integer.parseInt(ym.format(new Date()));
	}

	public static int compare(String date1, String date2) {
		try {
			Date dt1 = YMD.parse(date1);
			Date dt2 = YMD.parse(date2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取特定日期是星期几
	 * 
	 * @return
	 */
	public static String getWeekDay(String DateStr) {
		SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");// formatYMD表示的是yyyy-MM-dd格式
		SimpleDateFormat formatD = new SimpleDateFormat("E");// "E"表示"day in week"
		Date d = null;
		String weekDay = "";
		try {
			d = formatYMD.parse(DateStr);// 将String 转换为符合格式的日期
			weekDay = formatD.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("日期:"+DateStr+" ： "+weekDay);
		return weekDay;
	}

	public static int getWeekDay1(String DateStr) {// 返回日期对应数字
		SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");// formatYMD表示的是yyyy-MM-dd格式
		Date d = null;
		int weekDay = 0;
		try {
			d = formatYMD.parse(DateStr);// 将String 转换为符合格式的日期
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weekDay;
	}

	/**
	 * 获取特定时间段内有哪几日
	 * 
	 * @return
	 */
	public static List<Object> findDates(String start_time, String end_time) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = df.parse(start_time);
		Date date2 = df.parse(end_time);
		int s = (int) ((date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000));
		List<Object> objects = new ArrayList<Object>();
		String value = "";
		if (s + 1 > 0) {
			for (int i = 0; i <= s; i++) {
				long todayDate = date1.getTime() + (long) i * 24 * 60 * 60 * 1000;
				Date tmDate = new Date(todayDate);
				value = new SimpleDateFormat("yyyy-MM-dd").format(tmDate);
				objects.add(value);
			}
		}
		return objects;
	}

	/**
	 * 获取特定时间段内有多少天
	 * 
	 * @return
	 */
	public static int countDates(String start_time, String end_time) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = df.parse(start_time);
		Date date2 = df.parse(end_time);
		int s = (int) ((date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000));
		return s;
	}

	/**
	 * 日期转化成oracle格式to_date('2012-12-12', 'yyyy-MM-dd')
	 * 
	 * @param format
	 *            format type,for example: 'yyyy-MM-dd', 'yyyy-MM-dd HH:mm:ss'
	 * @param date
	 *            date{type=java.util.Date}
	 */
	public static String parseDateToOracleString(String format, Date date) {
		if (format == null) {
			format = Constant.YMD;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if (date == null) {
			date = new Date();
		}
		if (Constant.YMD_HMS.toUpperCase().equals(format.toUpperCase())) {
			format = Constant.ORACLE_YMD_HMS;
		} else {
			format = Constant.ORACLE_YMD;
		}
		return "to_date('" + sdf.format(date) + "','" + format + "')";
	}

	/**
	 * 日期转化成oracle格式to_date('2012-12-12', 'yyyy-MM-dd')
	 * 
	 * @param format
	 *            format type,for example: 'yyyy-MM-dd', 'yyyy-MM-dd HH:mm:ss'
	 * @param dateString
	 *            date{type=string}
	 */
	public static String parseDateToOracleString(String format, String dateString) {
		if (format == null) {
			format = Constant.YMD;
		}
		if (dateString == null || "".equals(dateString) || "null".equals(dateString)) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			dateString = sdf.format(new Date());
		}
		if (Constant.YMD_HMS.toUpperCase().equals(format.toUpperCase())) {
			format = Constant.ORACLE_YMD_HMS;
		} else {
			format = Constant.ORACLE_YMD;
		}
		return "to_date('" + dateString + "','" + format + "')";
	}

	/**
	 * Date转化成字符串格式
	 * 
	 * @param f
	 *            format格式;若为空，则默认为yyyy-MM-dd
	 */
	public static String parseDateToString(Date date, String f) {
		if (f == null) {
			f = Constant.YMD;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(f);
		if (date == null) {
			date = new Date();
		}
		return sdf.format(date);
	}

	/**
	 * 形如{2012-12-21}或{2012-12-21 12:12:12}字符串格式的日期转化成java.util.Date类型
	 * 
	 * @param date
	 *            string日期;若为空或格式错误,则返回当前时间
	 * @param f
	 *            format格式;若为空，则默认为yyyy-MM-dd
	 * @return java.util.Date类型日期
	 */
	public static Date parseStringToDate(String date, String f) {
		if (f == null) {
			f = Constant.YMD;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(f);
		if (date == null) {
			return new Date();
		}
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			return new Date();
		}
	}

	/**
	 * 当前时间的字符串格式
	 * 
	 * @param f
	 *            format格式;若为空，则默认为yyyy-MM-dd
	 */
	public static String currentDateString(String f) {
		if (f == null) {
			f = Constant.YMD;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(f);
		return sdf.format(new Date());
	}

	/**
	 * 借贷合同月份处理
	 */
	public static int getMonthSpace(Date date1, Date date2) throws ParseException {
		int iMonth = 0;
		int flag = 0;
		try {
			Calendar objCalendarDate1 = Calendar.getInstance();
			objCalendarDate1.setTime(date1);

			Calendar objCalendarDate2 = Calendar.getInstance();
			objCalendarDate2.setTime(date2);

			if (objCalendarDate2.equals(objCalendarDate1))
				return 0;
			if (objCalendarDate1.after(objCalendarDate2)) {
				Calendar temp = objCalendarDate1;
				objCalendarDate1 = objCalendarDate2;
				objCalendarDate2 = temp;
			}
			if (objCalendarDate2.get(Calendar.DAY_OF_MONTH) > objCalendarDate1.get(Calendar.DAY_OF_MONTH))
				flag = 1;

			if (objCalendarDate2.get(Calendar.YEAR) > objCalendarDate1.get(Calendar.YEAR)) {
				iMonth = ((objCalendarDate2.get(Calendar.YEAR) - objCalendarDate1.get(Calendar.YEAR)) * 12
						+ objCalendarDate2.get(Calendar.MONTH) + flag)
						- objCalendarDate1.get(Calendar.MONTH);
			} else {
				iMonth = objCalendarDate2.get(Calendar.MONTH) + flag - objCalendarDate1.get(Calendar.MONTH);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return iMonth;
	}

	/**
	 * 月份往前或往后n天
	 */
	public static Date overDate(Date date, int num) {
		date = date == null ? new Date() : date;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, num);
		date = calendar.getTime();
		return date;
	}

	/**
	 * 月份加减
	 * 
	 * @param date
	 * @param increase
	 * @return
	 */
	public static Integer addMonth(Date date, int increase) {
		if (date == null)
			date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, increase);
		return getYearmonth(calendar.getTime());
	}

	/**
	 * 月份加减
	 * 
	 * @param month
	 * @param increase
	 * @return
	 */
	public static Integer addMonth(Integer month, int increase) {
		Calendar calendar = new GregorianCalendar();
		try {
			calendar.setTime(ym.parse(String.valueOf(month)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.MONTH, increase);
		return getYearmonth(calendar.getTime());
	}

	/**
	 * 时间添加小时数
	 * */
	public static Date addHours(Date date, float hours) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(Calendar.MINUTE, (int) (hours * 60));
		return ca.getTime();
	}

	/**
	 * 判断日期是否合法
	 */
	public static boolean isValidDate(String dateString, String f) {
		SimpleDateFormat sdf = new SimpleDateFormat(f);
		sdf.setLenient(false);
		try {
			sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
