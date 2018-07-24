package com.uas.erp.service.ma.impl;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.ma.MADocumentSetupService;

@Service
public class MADocumentSetupServiceImpl implements MADocumentSetupService {
	@Autowired
	private BaseDao baseDao;

	@Override
	@CacheEvict(value = "logic", allEntries = true)
	public void save(String form) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(form);
		// 判断caller是否已存在
		if (!checkCaller((String) store.get("ds_table"))) {
			BaseUtil.showError(BaseUtil.getLocalMessage("ma.documentSetup_callerExist"));
		}
		// 保存documentSetup
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DocumentSetup", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save("DocumentSetup", "ds_id", store.get("ds_id"));
	}

	@Override
	@CacheEvict(value = "logic", allEntries = true)
	public void update(String form) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(form);
		// 更新documentSetup
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DocumentSetup", "ds_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update("DocumentSetup", "ds_id", store.get("ds_id"));
	}

	@Override
	public boolean checkCaller(String caller) {
		return baseDao.checkByCondition("documentSetup", "ds_table='" + caller + "'");
	}

	@Override
	public void delete(int id) {
		// 删除
		baseDao.deleteById("DocumentSetup", "ds_id", id);
		// 删除Detail
		baseDao.deleteById("DocumentHandler", "dh_dsid", id);
		// 记录操作
		baseDao.logger.delete("DocumentSetup", "ds_id", id);
	}

}
