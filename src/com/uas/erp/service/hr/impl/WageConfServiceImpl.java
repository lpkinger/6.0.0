package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.WageConfService;

@Service
public class WageConfServiceImpl implements WageConfService {
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public List<Map<String, Object>> getOverWorkConf() {
		return baseDao.queryForList("select * from WageOverWorkConf");
	}

	@Override
	public Map<String, Object> getBaseConf() {
		return baseDao.getJdbcTemplate().queryForMap("select * from WageConf");
	}

	@Override
	public List<Map<String, Object>> getAbsenceConf() {
		return baseDao.queryForList("select * from WageAbsenceConf");
	}

	@Override
	public List<Map<String, Object>> getPersonTaxConf() {
		return baseDao.queryForList("select * from WagePersonTaxConf");
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void update(String formStore, String owgridStore, String ptgridStore, String abgridStore) {
		Map<Object, Object> formMap = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> owgridMaps = BaseUtil.parseGridStoreToMaps(owgridStore);
		List<Map<Object, Object>> ptgridMaps = BaseUtil.parseGridStoreToMaps(ptgridStore);
		List<Map<Object, Object>> abgridMaps = BaseUtil.parseGridStoreToMaps(abgridStore);
		//基础配置
		if (formMap!=null) {
			String formsql = SqlUtil.getUpdateSqlByFormStore(formMap, "WageConf", "WC_ID");
			baseDao.execute(formsql);
		}
		//加班配置
		
		if (owgridMaps!=null) {
			List<String> owgridsqls = SqlUtil.getUpdateSqlbyGridStore(owgridMaps, "WageOverWorkConf", "WO_ID");
			for (Map<Object, Object> owgridMap : owgridMaps) {
				
				Object wo_id = owgridMap.get("WO_ID");
				if (wo_id == null || wo_id.equals("") || wo_id.equals("0") || Integer.parseInt(wo_id.toString()) == 0) {
					Object wo_type = owgridMap.get("WO_TYPE");
					boolean bool = baseDao.checkByCondition("WageOverWorkConf", "wo_type='"+wo_type+"'");
					if (!bool) {
						BaseUtil.showError("当前加班类型已存在！");
					}					
					baseDao.execute(SqlUtil.getInsertSql(owgridMap, "WageOverWorkConf", "WO_ID"));
				}
			}
			baseDao.execute(owgridsqls);
		}
		//个税配置
		if (ptgridMaps!=null) {
			List<String> ptgridsqls = SqlUtil.getUpdateSqlbyGridStore(ptgridMaps, "WagePersonTaxConf", "WP_ID");
			for (Map<Object, Object> ptgridMap : ptgridMaps) {
				Object wp_id = ptgridMap.get("WP_ID");
				if (wp_id == null || wp_id.equals("") || wp_id.equals("0") || Integer.parseInt(wp_id.toString()) == 0) {
					baseDao.execute(SqlUtil.getInsertSql(ptgridMap, "WagePersonTaxConf", "WP_ID"));
				}
			}
			baseDao.execute(ptgridsqls);
		}
		//缺勤设置
		if (abgridMaps!=null) {
			List<String> abgridsqls = SqlUtil.getUpdateSqlbyGridStore(abgridMaps, "WageAbsenceConf", "WAC_ID");
			for (Map<Object, Object> abgridMap : abgridMaps) {
				
				Object wac_id = abgridMap.get("WAC_ID");
				if (wac_id == null || wac_id.equals("") || wac_id.equals("0") || Integer.parseInt(wac_id.toString()) == 0) {
					Object wac_type = abgridMap.get("WAC_TYPE");
					boolean bool = baseDao.checkByCondition("WageAbsenceConf", "wac_type='"+wac_type+"' and wac_type<>'sickleave' and wac_type<>'absent'    ");
					if (!bool) {
						BaseUtil.showError("当前缺勤类型已存在！");
					}					
					baseDao.execute(SqlUtil.getInsertSql(abgridMap, "WageAbsenceConf", "WAC_ID"));
				}
			}
			baseDao.execute(abgridsqls);
		}
	}

}
