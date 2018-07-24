package com.uas.erp.service.ma.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Configs;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.SysnavigationService;
import com.uas.erp.service.ma.ConfigService;

@Service
public class ConfigServiceImpl implements ConfigService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SysnavigationService sysnavigationService;

	@Override
	public List<Configs> getConfigsByCaller(String caller, HttpSession session) {
		try {
			Object printtype = session.getAttribute("en_admin");
			List<Configs> configs;
			if (printtype != null && printtype.equals("jasper")) {
				configs = baseDao
						.getJdbcTemplate()
						.query("select * from Configs where caller=? and code not like '%printNeed%' and code not like '%printNo%' order by data_type desc,id",
								new BeanPropertyRowMapper<Configs>(Configs.class), caller);
			} else {
				configs = baseDao.getJdbcTemplate().query("select * from Configs where caller=? order by data_type desc,id",
						new BeanPropertyRowMapper<Configs>(Configs.class), caller);
			}
			for (Configs config : configs) {
				if ("RADIO".equals(config.getData_type())) {
					List<Configs.Properties> properties = baseDao.getJdbcTemplate().query(
							"select * from ConfigProps where config_id=? order by value",
							new BeanPropertyRowMapper<Configs.Properties>(Configs.Properties.class), config.getId());
					config.setProperties(properties);
				}
			}
			return configs;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	@CacheEvict(value = "configs", allEntries = true)
	public void saveConfigs(List<Map<Object, Object>> updated) {
		baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(updated, "Configs", "id"));
		log(updated);
	}

	private void log(List<Map<Object, Object>> updated) {
		String sql = null;
		Employee user = SystemSession.getUser();
		StringBuffer ids = new StringBuffer();
		for (Map<Object, Object> update : updated) {
			ids.append(update.get("id") + ",");
		}
		if (ids.length() > 0) {
			sql = "insert into messagelog(ml_id,ml_date,ml_man,ml_content,ml_result,ml_search) "
					+ " select messagelog_seq.nextval,sysdate,'" + user.getEm_name() + "(" + user.getEm_code()
					+ ")','参数配置',act,caller||'|Config Caller' from ( " + " select (case data_type "
					+ " when 'YN' then '更新为:'||(case when data='1' then '开启' else '关闭' end) " + " when 'N' then '更新为:'||data"
					+ " when 'NUMBER' then '更新为:'||data" + " when 'VARCHAR' then '更新为:'||data" + " when 'VARCHAR2' then '更新为:'||data"
					+ " when 'RADIO' then '选择:'||display" + " else null end)||'	'||title act,caller  from ("
					+ " SELECT configs.*,ConfigProps.*,rownum rn "
					+ " FROM configs left join ConfigProps on config_id=id and value=data  where id in ("
					+ ids.substring(0, ids.length() - 1) + ")" + " ))";
			baseDao.execute(sql);
		}
	}

	@Override
	public void beforeSave(String updated) {
		String result = baseDao.callProcedure("SP_CONFIGS", new Object[] { updated, 0 });
		if (result != null && !"".equals(result)) {
			BaseUtil.showError(result);
		}
	}

	@Override
	public void afterSave(String updated) {
		String result = baseDao.callProcedure("SP_CONFIGS", new Object[] { updated, 1 });
		if (result != null && !"".equals(result)) {
			if (result.contains("SYSNAVIGATION")) {// 清除导航缓存
				sysnavigationService.refreshSysnavigation();
			}
		}
	}

	@Override
	public JSONObject getConfigByCallerAndCode(String caller, String code) {
		Configs configs = baseDao.configFactory.getConfigs(SpObserver.getSp(), caller, code);
		if (configs != null) {
			JSONObject object = new JSONObject();
			object.put("data", configs.getData());
			object.put("data_type", configs.getData_type());
			object.put("multi", configs.getMulti());
			return object;
		}
		return null;
	}

	@Override
	public List<Configs> getConfigsByCondition(String condition) {
		try {
			List<Configs> configs = baseDao.getJdbcTemplate().query(
					"select * from Configs where " + condition + " order by data_type desc,id",
					new BeanPropertyRowMapper<Configs>(Configs.class));
			for (Configs config : configs) {
				if ("RADIO".equals(config.getData_type())) {
					List<Configs.Properties> properties = baseDao.getJdbcTemplate().query(
							"select * from ConfigProps where config_id=? order by value",
							new BeanPropertyRowMapper<Configs.Properties>(Configs.Properties.class), config.getId());
					config.setProperties(properties);
				}
			}
			return configs;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Configs getConfig(String caller, String code) {
		return baseDao.configFactory.getConfigs(SpObserver.getSp(), caller, code);
	}

}
