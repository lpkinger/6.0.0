package com.uas.erp.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.model.Master;

public class BaseUtil implements ApplicationContextAware {

	private static Map<String, Object> setting;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		ContextUtil.setApplicationContext(context);
		// 创建数据源
		createDataSource();
	}

	/**
	 * 取系统根路径
	 * 
	 * @param request
	 * @return
	 */
	public static String getBasePath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
	}

	/**
	 * 将formStore解析成一个map
	 * 
	 * @param formStore
	 *            字符串形式的表单数据
	 * @return map形式的表单数据
	 */
	public static Map<Object, Object> parseFormStoreToMap(String formStore) {
		try {
			return FlexJsonUtil.fromJson(formStore);
		} catch (Exception e) {
			return JSONUtil.toMap(formStore);
		}
	}

	public static String parseMap2Str(Map<?, Object> map) {
		if (map != null) {
			return JacksonUtil.toJson(map);
		}
		return null;
	}

	/**
	 * 将gridStore解析成maps
	 * 
	 * @param gridStore
	 *            字符串形式的grid数据
	 * @return map形式的grid数据
	 */
	public static List<Map<Object, Object>> parseGridStoreToMaps(String gridStore) {
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		try {
			if (!gridStore.startsWith("[")) {
				gridStore = "[" + gridStore + "]";
			}
			if (gridStore.length() > 409600) {
				list = JacksonUtil.fromJsonArray(gridStore);
			} else {
				list = FlexJsonUtil.fromJsonArray(gridStore, HashMap.class);
			}
		} catch (Exception e) {
			list = JSONUtil.toMapList(gridStore);
		}
		return list;
	}

	public static String parseGridStore2Str(List<Map<String, Object>> list) {
		return JacksonUtil.toJsonArray(list);
	}

	/**
	 * 以抛出异常的方式将信息交给MyExceptionHandler MyExceptionHandler会捕捉信息并传给前台
	 * 前台捕捉错误信息，并显示给用户
	 * 
	 * @param error
	 *            要在前台显示的信息
	 */
	public static void showError(String error) {
		if (error != null && error.length() > 0)
			throw new SystemException(error);
	}

	public static void showErrorOnSuccess(String error) {
		if (error != null && error.length() > 0)
			throw new SystemException("AFTERSUCCESS" + StringUtil.nvl(SystemSession.getErrors(), "") + "<br>" + error);
	}

	/**
	 * 返回true表示含有重复项，返回false表示不含有重复项
	 */
	public static boolean checkDuplicateArray(Object[] array) {
		Set<Object> set = new HashSet<Object>();
		for (Object str : array) {
			set.add(str);
		}
		if (set.size() != array.length) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 不中断程序的情况下添加错误信息
	 * 
	 * @param error
	 *            错误信息
	 */
	public static void appendError(String error) {
		SystemSession.appendError(error);
	}

	/**
	 * 根据语言及对应的消息名称，获得消息内容
	 */
	public static String getLocalMessage(String msgName) {
		return getLocalMessage(msgName, SystemSession.getLang());
	}

	/**
	 * 根据语言及对应的消息名称，获得消息内容
	 */
	public static String getLocalMessage(String msgName, String language) {
		try {
			// language = language == null ? "zh_CN" : language;
			language = "zh_CN";
			return ContextUtil.getApplicationContext().getMessage(msgName, null, Locale.CHINA);
		} catch (Exception e) {
			return msgName;
		}
	}

	public static JSONArray sortJsonArray(JSONArray array, String property) {
		JSONObject jObject = null;
		for (int i = 0; i < array.size(); i++) {
			Float l = Float.parseFloat(array.getJSONObject(i).get(property).toString());
			for (int j = i + 1; j < array.size(); j++) {
				Float nl = Float.parseFloat(array.getJSONObject(j).get(property).toString());
				if (l < nl) {
					jObject = array.getJSONObject(j);
					array.set(j, array.getJSONObject(i));
					array.set(i, jObject);
				}
			}
		}
		return array;
	}

	/**
	 * 字符串转化成数组, null和空字符自动去掉
	 * 
	 * @param str
	 *            待转化字段
	 * @param ch
	 *            分割符
	 */
	public static String[] parseStr2Array(String str, String ch) {
		String[] arr = new String[] {};
		for (String s : str.split(ch)) {
			if (s != null && !s.trim().equals("")) {
				arr = Arrays.copyOf(arr, arr.length + 1);
				arr[arr.length - 1] = s;
			}
		}
		return arr;
	}

	/**
	 * 字符串转化成List集合, null和空字符自动去掉
	 * 
	 * @param str
	 *            待转化字段
	 * @param ch
	 *            分割符
	 * @param repeat
	 *            是否去重 {true-是、false-否}
	 */
	public static List<String> parseStr2List(String str, String ch, boolean repeat) {
		List<String> list = new ArrayList<String>();
		for (String s : str.split(ch)) {
			if (s != null && !s.trim().equals("")) {
				if (repeat) {
					if (!list.contains(s)) {
						list.add(s);
					}
				} else {
					list.add(s);
				}
			}
		}
		return list;
	}

	/**
	 * 数组转化成字符串, null和空字符自动去掉
	 * 
	 * @param arr
	 *            待转化数组
	 * @param ch
	 *            分割符
	 */
	public static String parseArray2Str(Object[] arr, String ch) {
		StringBuffer sb = new StringBuffer();
		for (Object s : arr) {
			if (s != null && !s.toString().trim().equals("")) {
				sb.append(s);
				sb.append(ch);
			}
		}
		if (sb.length() > 0 && ch.length() > 0) {
			return sb.substring(0, sb.lastIndexOf(ch));
		}
		return sb.toString();
	}

	/**
	 * List集合转化成字符串, null和空字符自动去掉
	 * 
	 * @param list
	 *            待转化集合
	 * @param ch
	 *            分割符
	 * @param repeat
	 *            是否去重 {true-是、false-否}
	 */
	public static String parseList2Str(List<?> list, String ch, boolean repeat) {
		StringBuffer sb = new StringBuffer();
		for (Object s : list) {
			if (s != null && !s.toString().trim().equals("")) {
				if (repeat) {
					if (!sb.toString().contains(s + ch)) {
						sb.append(s);
						sb.append(ch);
					}
				} else {
					sb.append(s);
					sb.append(ch);
				}
			}
		}
		if (sb.length() > 0 && ch.length() > 0) {
			return sb.substring(0, sb.lastIndexOf(ch));
		}
		return sb.toString();
	}

	/**
	 * 把一个list集合的map按指定字段{groupField}分组
	 */
	public static Map<Object, List<Map<Object, Object>>> groupMap(List<Map<Object, Object>> maps, String groupField) {
		Map<Object, List<Map<Object, Object>>> set = new HashMap<Object, List<Map<Object, Object>>>();
		List<Map<Object, Object>> list = null;
		for (Map<Object, Object> map : maps) {
			Object key = map.get(groupField);
			if (set.containsKey(key)) {
				list = set.get(key);
			} else {
				list = new ArrayList<Map<Object, Object>>();
			}
			list.add(map);
			set.put(key, list);
		}
		return set;
	}

	public static Map<Object, List<Map<Object, Object>>> groupsMap(List<Map<Object, Object>> maps, Object[] objects) {
		Map<Object, List<Map<Object, Object>>> set = new HashMap<Object, List<Map<Object, Object>>>();
		List<Map<Object, Object>> list = null;
		String keyValue = null;
		Object value = null;
		for (Map<Object, Object> map : maps) {
			keyValue = null;
			for (Object field : objects) {
				if (keyValue != null) {
					keyValue += "#";
				} else {
					keyValue = "";
				}
				value = map.get(field);
				keyValue += value == null ? "" : value;
			}
			// Object key = map.get(groupField);
			if (keyValue != null && !keyValue.equals("") && set.containsKey(keyValue)) {
				list = set.get(keyValue);
			} else {
				list = new ArrayList<Map<Object, Object>>();
			}
			list.add(map);
			set.put(keyValue, list);
		}
		return set;
	}

	/**
	 * Map集合按照value值排序 只适用于key{String}value{Integer}
	 * 
	 * @param type
	 *            0-升序/1-降序
	 */
	public static List<String> mapSort(Map<String, Integer> map, int type) {
		ArrayList<Entry<String, Integer>> e = new ArrayList<Entry<String, Integer>>(map.entrySet());
		if (type == 0) {
			Collections.sort(e, new Comparator<Entry<String, Integer>>() {
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					return (o1.getValue() - o2.getValue());
				}
			});
		} else if (type == 1) {
			Collections.sort(e, new Comparator<Entry<String, Integer>>() {
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					return (o2.getValue() - o1.getValue());
				}
			});
		}
		Iterator<Entry<String, Integer>> iterator = e.iterator();
		List<String> list = new ArrayList<String>();
		while (iterator.hasNext()) {
			list.add(iterator.next().getKey());
		}
		return list;
	}

	/**
	 * 设置下载文件中文件的名称
	 * 
	 * @param filename
	 * @param request
	 * @return
	 */
	public static String encodeFilename(String filename, HttpServletRequest request) {
		/**
		 * 获取客户端浏览器和操作系统信息 在IE浏览器中得到的是：User-Agent=Mozilla/4.0 (compatible; MSIE
		 * 6.0; Windows NT 5.1; SV1; Maxthon; Alexa Toolbar)
		 * 在Firefox中得到的是：User-Agent=Mozilla/5.0 (Windows; U; Windows NT 5.1;
		 * zh-CN; rv:1.7.10) Gecko/20050717 Firefox/1.0.6
		 */
		String agent = request.getHeader("USER-AGENT");
		try {
			if ((agent != null) && (-1 != agent.indexOf("MSIE"))) {
				String newFileName = URLEncoder.encode(filename, "UTF-8");
				newFileName = newFileName.replace("+", "%20");
				if (newFileName.length() > 150) {
					newFileName = new String(filename.getBytes("GB2312"), "ISO8859-1");
					newFileName = newFileName.replace(" ", "%20");
				}
				return newFileName;
			}
			if ((agent != null) && (-1 != agent.indexOf("Mozilla")))
				return MimeUtility.encodeText(filename, "UTF-8", "B");
			return filename;
		} catch (Exception ex) {
			return filename;
		}
	}

	/**
	 * 报表参数加密
	 * 
	 * @param key
	 * @param reportName
	 * @param condition
	 * @return
	 */
	public static String[] reportEncrypt(String key, String reportName, String condition) {
		String[] keys = new String[4];
		Des de = new Des();
		try {
			String name = URLEncoder.encode(reportName, "utf-8").toLowerCase();
			keys[0] = de.toHexString(de.encrypt(name, key)).toUpperCase();

			String skey = URLEncoder.encode(key, "utf-8").toLowerCase();
			keys[1] = de.toHexString(de.encrypt(skey, key)).toUpperCase();

			String cond = URLEncoder.encode(condition, "utf-8").toLowerCase();
			keys[2] = de.toHexString(de.encrypt(cond, key)).toUpperCase();

			String lyTime = DateUtil.parseDateToString(new Date(), Constant.YMD_HM);
			String time = URLEncoder.encode(lyTime, "utf-8").toLowerCase();
			keys[3] = de.toHexString(de.encrypt(time, key)).toUpperCase();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keys;
	}

	/**
	 * 取config.xml中about配置
	 */
	@SuppressWarnings("unchecked")
	public static String getXmlSetting(String key) {
		if (setting == null) {
			setting = (Map<String, Object>) ContextUtil.getBean("about");
		}
		if (setting != null && setting.containsKey(key)) {
			return String.valueOf(setting.get(key));
		}
		return null;
	}

	/**
	 * 动态创建dataSource
	 */
	private final void createDataSource() {
		String defaultSource = getXmlSetting("defaultSob");
		ApplicationContext context = ContextUtil.getApplicationContext();
		if (defaultSource != null && context.containsBean(defaultSource) && context.containsBean("enterpriseDao")) {
			DruidDataSource dataSource = (DruidDataSource) context.getBean(defaultSource);
			@SuppressWarnings("resource")
			ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) context;
			DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
			SpObserver.putSp(defaultSource);
			EnterpriseDao dao = (EnterpriseDao) context.getBean("enterpriseDao");
			List<Master> masters = dao.getMasters();
			for (Master master : masters) {
				// 比较特殊的，请直接在db-config.xml配置bean
				if (!beanFactory.containsBean(master.getMa_name())) {
					BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(DruidDataSource.class);
					bdb.getBeanDefinition().setAttribute("id", master.getMa_name());
					bdb.setDestroyMethodName("close");
					bdb.setLazyInit(true);
					// 支持多种类型数据源
					bdb.addPropertyValue("driverClassName", StringUtil.nvl(master.getMa_driver(), dataSource.getDriverClassName()));
					bdb.addPropertyValue("url", StringUtil.nvl(master.getMa_url(), dataSource.getUrl()));
					bdb.addPropertyValue("username", master.getMa_user());
					bdb.addPropertyValue("password", master.getMs_pwd());
					bdb.addPropertyValue("initialSize", dataSource.getInitialSize());
					bdb.addPropertyValue("maxActive", dataSource.getMaxActive());
					bdb.addPropertyValue("minIdle", dataSource.getMinIdle());
					bdb.addPropertyValue("poolPreparedStatements", dataSource.isPoolPreparedStatements());
					bdb.addPropertyValue("maxPoolPreparedStatementPerConnectionSize",
							dataSource.getMaxPoolPreparedStatementPerConnectionSize());
					bdb.addPropertyValue("testOnBorrow", dataSource.isTestOnBorrow());
					bdb.addPropertyValue("testOnReturn", dataSource.isTestOnReturn());
					bdb.addPropertyValue("testWhileIdle", dataSource.isTestWhileIdle());
					bdb.addPropertyValue("validationQuery", dataSource.getValidationQuery());
					bdb.addPropertyValue("minEvictableIdleTimeMillis", dataSource.getMinEvictableIdleTimeMillis());
					bdb.addPropertyValue("timeBetweenEvictionRunsMillis", dataSource.getTimeBetweenEvictionRunsMillis());
					bdb.addPropertyValue("filters", "stat");
					bdb.addPropertyValue("removeAbandoned", dataSource.isRemoveAbandoned());
					bdb.addPropertyValue("removeAbandonedTimeout", dataSource.getRemoveAbandonedTimeout());
					beanFactory.registerBeanDefinition(master.getMa_name(), bdb.getBeanDefinition());
				}
			}
		}
	}

	/**
	 * 按master记录，动态创建dataSource<br>
	 * 比如修改master表记录之后
	 * 
	 * @param master
	 */
	public static void createDataSource(Master master) {
		String defaultSource = getXmlSetting("defaultSob");
		ApplicationContext context = ContextUtil.getApplicationContext();
		DruidDataSource dataSource = (DruidDataSource) context.getBean(defaultSource);
		@SuppressWarnings("resource")
		ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) context;
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
		// 比较特殊的，请直接在db-config.xml配置bean
		if (!beanFactory.containsBean(master.getMa_name())) {
			BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(DruidDataSource.class);
			bdb.getBeanDefinition().setAttribute("id", master.getMa_name());
			bdb.getBeanDefinition().setAttribute("destroy-method", "close");
			// 支持多种类型数据源
			bdb.addPropertyValue("driverClassName", StringUtil.nvl(master.getMa_driver(), dataSource.getDriverClassName()));
			bdb.addPropertyValue("url", StringUtil.nvl(master.getMa_url(), dataSource.getUrl()));
			bdb.addPropertyValue("username", master.getMa_user());
			bdb.addPropertyValue("password", master.getMs_pwd());
			bdb.addPropertyValue("initialSize", dataSource.getInitialSize());
			bdb.addPropertyValue("maxActive", dataSource.getMaxActive());
			bdb.addPropertyValue("minIdle", dataSource.getMinIdle());
			bdb.addPropertyValue("poolPreparedStatements", dataSource.isPoolPreparedStatements());
			bdb.addPropertyValue("maxPoolPreparedStatementPerConnectionSize", dataSource.getMaxPoolPreparedStatementPerConnectionSize());
			bdb.addPropertyValue("testOnBorrow", dataSource.isTestOnBorrow());
			bdb.addPropertyValue("testOnReturn", dataSource.isTestOnReturn());
			bdb.addPropertyValue("testWhileIdle", dataSource.isTestWhileIdle());
			bdb.addPropertyValue("validationQuery", dataSource.getValidationQuery());
			bdb.addPropertyValue("minEvictableIdleTimeMillis", dataSource.getMinEvictableIdleTimeMillis());
			bdb.addPropertyValue("timeBetweenEvictionRunsMillis", dataSource.getTimeBetweenEvictionRunsMillis());
			bdb.addPropertyValue("filters", "stat");
			bdb.addPropertyValue("removeAbandoned", dataSource.isRemoveAbandoned());
			bdb.addPropertyValue("removeAbandonedTimeout", dataSource.getRemoveAbandonedTimeout());
			beanFactory.registerBeanDefinition(master.getMa_name(), bdb.getBeanDefinition());
		}
	}

	/**
	 * 系统类型 city: 产城 saas: 优企云服 uas: UAS系统
	 * 
	 * @return
	 */
	public static String getAppId() {
		String appId = BaseUtil.getXmlSetting("appId");
		if (StringUtils.isEmpty(appId)) {
			appId = BaseUtil.getXmlSetting("saas.domain") != null ? "saas" : "uas";
		}
		return appId;
	}
	/***
	 * 是否为集团版
	 * 
	 * */
   public static boolean isGroup(){
	   return "true".equals(BaseUtil.getXmlSetting("group"));
   }
}
