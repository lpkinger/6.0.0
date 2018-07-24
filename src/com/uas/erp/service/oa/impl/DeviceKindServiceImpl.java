package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.DeviceKindService;

@Service
public class DeviceKindServiceImpl implements DeviceKindService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDeviceKind(String formStore, String gridStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("DeviceKind",
				"dk_code='" + store.get("dk_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		 bool = baseDao.checkByCondition("DeviceKind",
				"dk_name='" + store.get("dk_name") + "'");
		if (!bool) {
			BaseUtil.showError("存在相同的设备种类名称");
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "DeviceKindAttribute","dka_id");
		baseDao.execute(gridSql);	
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DeviceKind",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "dk_id", store.get("dk_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateDeviceKindById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DeviceKind",
				"dk_id");
		// 修改Detail 
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "DeviceKindAttribute", "dka_id");
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "dk_id", store.get("dk_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteDeviceKind(int dk_id, String caller) {
	/*	Object status = baseDao.getFieldDataByCondition("BadCode",
				"bc_statuscode", "bc_id=" + bc_id);
		StateAssert.delOnlyEntering(status);*/
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { dk_id });
		// 删除
		baseDao.deleteById("DeviceKind", "dk_id", dk_id);
		// 删除Detail
		baseDao.deleteById("DeviceKindAttribute", "dka_dkid", dk_id);
		// 记录操作
		baseDao.logger.delete(caller, "dk_id", dk_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { dk_id });
	}

}
