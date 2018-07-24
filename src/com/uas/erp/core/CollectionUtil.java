package com.uas.erp.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.util.StringUtils;

import com.uas.erp.core.support.KeyEntity;

public class CollectionUtil {

	public static final int EXCLUDE = 0;
	public static final int INCLUDE = 1;

	/**
	 * 取List里面的field的值
	 */
	public static Object[] pluck(List<Map<Object, Object>> list, String field) {
		Object[] d = new Object[list.size()];
		int index = 0;
		for (Map<Object, Object> m : list) {
			if (m.containsKey(field)) {
				d[index++] = m.get(field);
			}
		}
		return d;
	}

	public static Object[] pluck(ArrayList<HashMap<Object, Object>> list, String field) {
		Object[] d = new Object[list.size()];
		int index = 0;
		for (Map<Object, Object> m : list) {
			if (m.containsKey(field)) {
				d[index++] = m.get(field);
			}
		}
		return d;
	}

	/**
	 * 过滤List
	 */
	public static List<Map<Object, Object>> filter(List<Map<Object, Object>> list, int type, Object... field) {
		int i = 1, len = field.length;
		if (len % 2 != 0) {
			try {
				throw new Exception("参数个数有误!");
			} catch (Exception e) {

			}
		}
		List<Map<Object, Object>> d = new ArrayList<Map<Object, Object>>();
		if (type == CollectionUtil.INCLUDE) {
			for (Map<Object, Object> m : list) {
				i = 0;
				for (Object f : field) {
					if (i % 2 == 0) {
						if (field[i + 1] != m.get(f) && !(field[i + 1]).equals(m.get(f))) {
							break;
						}
					}
					i++;
					if (i == len) {
						d.add(m);
					}
				}
			}
		} else {
			for (Map<Object, Object> m : list) {
				i = 0;
				for (Object f : field) {
					if (i % 2 == 0) {
						if (field[i + 1] == m.get(f) || (field[i + 1]).equals(m.get(f))) {
							break;
						}
					}
					i++;
					if (i == len) {
						d.add(m);
					}
				}
			}
		}
		return d;
	}

	public static Map<String, Object> findRecord(List<Map<String, Object>> list, String field, Object value) {
		if (value != null) {
			for (Map<String, Object> m : list) {
				if (value.equals(m.get(field))) {
					return m;
				}
			}
		}
		return null;
	}

	/**
	 * 字段{field}的值是否重复
	 * 
	 * @param list
	 * @param field
	 * @return
	 */
	public static boolean isUnique(Collection<? extends Map<?, Object>> list, String field) {
		if (list != null) {
			Set<Object> objects = new HashSet<Object>();
			Object object = null;
			for (Map<?, Object> m : list) {
				object = m.get(field);
				if (object != null) {
					if (objects.contains(object))
						return false;
					objects.add(object);
				}
			}
		}
		return true;
	}

	/**
	 * 取字段{field}的重复项
	 * 
	 * @param list
	 * @param field
	 * @return
	 */
	public static String getRepeats(Collection<? extends Map<?, Object>> list, String field) {
		if (list != null) {
			Set<Object> objects = new HashSet<Object>();
			Object object = null;
			StringBuffer sb = new StringBuffer();
			for (Map<?, Object> m : list) {
				object = m.get(field);
				if (object != null) {
					if (objects.contains(object))
						sb.append(object).append(" ");
					objects.add(object);
				}
			}
			if (sb.length() > 0)
				return sb.toString();
		}
		return null;
	}

	public static String toString(List<Map<Object, Object>> list) {
		JSONArray arr = new JSONArray();
		JSONObject obj = null;
		for (Map<Object, Object> map : list) {
			if (map != null) {
				obj = new JSONObject();
				for (Object key : map.keySet()) {
					obj.put(key, map.get(key));
				}
				arr.add(obj);
			}
		}
		return arr.toString();
	}

	public static String toJSONString(Map<String, ?> map) {
		return JSONObject.fromObject(map).toString();
	}

	public static String toString(Map<String, ?> map) {
		Set<String> keys = map.keySet();
		StringBuffer sb = new StringBuffer();
		for (String k : keys) {
			if (sb.length() > 0)
				sb.append(",");
			if (StringUtil.hasText(k))
				sb.append(k).append(":");
			Object value = map.get(k);
			if (value instanceof Double) {
				value = NumberUtil.parseBigDecimal(Double.parseDouble(String.valueOf(value)));
			}
			sb.append(value);
		}
		return sb.toString();
	}

	public static String toString(Collection<String> paramArray) {
		return toString(paramArray, ",");
	}

	public static String toString(Object[] strs) {
		StringBuffer sb = new StringBuffer();
		for (Object k : strs) {
			if (!StringUtils.isEmpty(k)) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(k);
			}
		}
		return sb.toString();
	}

	public static String toString(Collection<String> paramArray, String separator) {
		StringBuffer sb = new StringBuffer();
		for (String k : paramArray) {
			if (!StringUtils.isEmpty(k)) {
				if (sb.length() > 0)
					sb.append(separator);
				sb.append(k);
			}
		}
		return sb.toString();
	}

	/**
	 * 用类<T>的变量key的get方法取变量key的值，并用separator连接成字符串
	 * 
	 * @param paramArray
	 * @param key
	 * @param separator
	 * @return
	 */
	public static <T> String getParamString(Collection<T> paramArray, String key, String separator) {
		StringBuffer sb = new StringBuffer();
		if (!isEmpty(paramArray)) {
			String methodGet = "get" + StringUtils.capitalize(key);
			Method method = null;
			for (T param : paramArray) {
				try {
					if (method == null)
						method = param.getClass().getMethod(methodGet, new Class[] {});
					Object val = method.invoke(param, new Object[] {});
					if (val != null) {
						if (sb.length() > 0)
							sb.append(separator);
						sb.append(val);
					}
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 用继承自KeyEntity的类，重写的getKey方法来取值，并用separator连接成字符串
	 * 
	 * @param paramArray
	 * @param separator
	 * @return
	 */
	public static String getKeyString(Collection<? extends KeyEntity> paramArray, String separator) {
		StringBuffer sb = new StringBuffer();
		if (!isEmpty(paramArray)) {
			for (KeyEntity param : paramArray) {
				Object val = param.getKey();
				if (val != null) {
					if (sb.length() > 0)
						sb.append(separator);
					sb.append(val);
				}
			}
		}
		return sb.toString();
	}

	public static String toSqlString(Set<String> strs) {
		StringBuffer sb = new StringBuffer();
		for (String k : strs) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append("'").append(k).append("'");
		}
		return sb.toString();
	}

	public static String toSqlString(String[] strs) {
		StringBuffer sb = new StringBuffer();
		for (String k : strs) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append("'").append(k).append("'");
		}
		return sb.toString();
	}

	public static String pluckSqlString(Collection<? extends Map<?, Object>> list, String field) {
		StringBuffer sb = new StringBuffer();
		for (Map<?, Object> m : list) {
			if (m.containsKey(field)) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append("'").append(m.get(field).toString().replaceAll("'","''")).append("'");
			}
		}
		return sb.toString();
	}

	/**
	 * 按给定size切割
	 * 
	 * @param paramList
	 * @param paramSize
	 * @return
	 */
	public static <T> List<List<T>> split(List<T> paramList, int paramSize) {
		List<List<T>> lists = new ArrayList<List<T>>();
		for (int j = 0, l = paramList.size(), i = l / paramSize; j <= i && l != j * paramSize; j++) {
			lists.add(paramList.subList(j * paramSize, Math.min((j + 1) * paramSize, l)));
		}
		return lists;
	}

	/**
	 * 按给定size切割
	 * 
	 * @param paramArray
	 * @param paramSize
	 * @return
	 */
	public static Object[] split(Object[] paramArray, int paramSize) {
		int len = paramArray.length;
		Object[] arrays = new Object[(int) Math.ceil((double) len / paramSize)];
		for (int j = 0, l = arrays.length; j < l; j++) {
			arrays[j] = Arrays.copyOfRange(paramArray, j * paramSize, Math.min((j + 1) * paramSize, len));
		}
		return arrays;
	}

	public static boolean isEmpty(Collection<?> paramArray) {
		return (paramArray == null) || (paramArray.size() == 0);
	}

	/**
	 * 给list倒序
	 * */
	public static <T> List<T> reverse(List<T> lists) {
		Collections.reverse(lists);
		return lists;
	}
}
