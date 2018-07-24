package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.VendorRemarkService;

@Service("vendorRemarkService")
public class VendorRemarkServiceImpl implements VendorRemarkService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVendorRemark(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VendorRemark", "vr_code='" + store.get("vr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("VendorRemark", "save", "before", new Object[] { store, grid });
		// 保存
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "VendorRemark"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "VendorRemarkDetail", "vrd_id"));
		baseDao.logger.save(caller, "vr_id", store.get("vr_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("VendorRemark", "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deleteVendorRemark(int vr_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler("VendorRemark", "delete", "before", new Object[] { vr_id });
		baseDao.delCheck("VendorRemark", vr_id);
		// 删除VendorRemark
		baseDao.deleteById("VendorRemark", "vr_id", vr_id);
		// 删除VendorRemarkDetail
		baseDao.deleteById("vendorremarkdetail", "vrd_vrid", vr_id);
		// 记录操作
		baseDao.logger.delete(caller, "vr_id", vr_id);
		// 执行删除后的其它逻辑
		handlerService.handler("VendorRemark", "delete", "after", new Object[] { vr_id });
	}

	@Override
	public void updateVendorRemarkById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.handler("VendorRemark", "save", "before", new Object[] { store, gstore });
		// 修改
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "VendorRemark", "vr_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "VendorRemarkDetail", "vrd_id"));
		// 记录操作
		baseDao.logger.update(caller, "vr_id", store.get("vr_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("VendorRemark", "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void bannedVendorRemark(int vr_id, String caller) {
		baseDao.banned("VendorRemark", "vr_id=" + vr_id, "vr_status", "vr_statuscode");
		baseDao.logger.banned(caller, "vr_id", vr_id);
	}

	@Override
	public void resBannedVendorRemark(int vr_id, String caller) {
		baseDao.updateByCondition("VendorRemark", "vr_status=null, vr_statuscode=null", "vr_id=" + vr_id);
		baseDao.logger.resBanned(caller, "vr_id", vr_id);
	}
}
