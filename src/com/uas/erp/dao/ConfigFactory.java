package com.uas.erp.dao;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.model.Configs;

/**
 * 
 * 参数配置中心
 * 
 * @author yingp
 * 
 */
@Component
public class ConfigFactory {

	/**
	 * 多个值的分隔符
	 */
	private static final String array_separator = "\n";

	/**
	 * @param caller
	 *            caller
	 * @param code
	 *            编号
	 * @return configs对象
	 */
	private Configs getConfigs(String caller, String code) {
		try {
			BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
			return baseDao.getJdbcTemplate().queryForObject("select *  from configs where caller=? and code=?",
					new BeanPropertyRowMapper<Configs>(Configs.class), caller, code);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * @param sob
	 *            帐套
	 * @param caller
	 *            caller
	 * @param code
	 *            编号
	 * @return configs对象
	 */
	@Cacheable(value = "configs", key = "#sob + '@' + #caller + '.' + #code + '@config'")
	public Configs getConfigs(String sob, String caller, String code) {
		return getConfigs(caller, code);
	}

	/**
	 * @param sob
	 *            帐套
	 * @param caller
	 * @param code
	 *            参数编号
	 * @return 值
	 */
	@Cacheable(value = "configs", key = "#sob + '@' + #caller + '.' + #code + '@get'")
	public String get(String sob, String caller, String code) {
		Configs config = getConfigs(caller, code);
		if (config != null)
			return config.getData();
		return null;
	}

	/**
	 * @param sob
	 *            帐套
	 * @param caller
	 * @param code
	 *            参数编号
	 * @return 值
	 */
	@Cacheable(value = "configs", key = "#sob + '@' + #caller + '.' + #code + '@array'")
	public String[] getArray(String sob, String caller, String code) {
		Configs configs = getConfigs(caller, code);
		if (configs != null) {
			String data = configs.getData();
			if (data != null && configs.getMulti() == Constant.YES) {
				return data.split(array_separator);
			}
			return new String[] { data };
		}
		return null;
	}

	/**
	 * 判断参数是否配置为“是”
	 * 
	 * @param sob
	 *            帐套
	 * @param caller
	 * @param code
	 * @return
	 */
	@Cacheable(value = "configs", key = "#sob + '@' + #caller + '.' + #code + '@is'")
	public boolean is(String sob, String caller, String code) {
		Configs configs = getConfigs(caller, code);
		if (configs != null) {
			String data = configs.getData();
			if ("YN".equals(configs.getData_type())) {
				return String.valueOf(Constant.YES).equals(data);
			}
			return data == null ? false : true;
		}
		return false;
	}

	/**
	 * 取参数值
	 * 
	 * @param sob
	 *            帐套
	 * @param caller
	 * @param code
	 * @return
	 */
	@Cacheable(value = "configs", key = "#sob + '@' + #caller + '.' + #code + '@val'")
	public Object val(String sob, String caller, String code) {
		Configs configs = getConfigs(caller, code);
		if (configs != null) {
			String data = configs.getData();
			if ("YN".equals(configs.getData_type())) {
				return String.valueOf(Constant.YES).equals(data);
			} else if ("NUMBER".equals(configs.getData_type())) {
				return NumberUtils.parseNumber(data, Double.class);
			}
			return data;
		}
		return null;
	}
}
