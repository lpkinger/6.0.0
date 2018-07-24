package com.uas.erp.service.crm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.SellerSaleReportService;

@Service
public class SellerSaleReportServiceImpl implements SellerSaleReportService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSellerSaleReport(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "projschedule",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "ps_id", store.get("ps_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteSellerSaleReport(int ps_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ps_id);
		// 删除purchase
		baseDao.deleteById("projschedule", "ps_id", ps_id);
		// baseDao.deleteById("SellerSaleReportDet", "ssd_ssid", id);
		// 记录操作
		baseDao.logger.delete(caller, "ps_id", ps_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ps_id);

	}

	@Override
	public void updateSellerSaleReport(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改Schedule
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "projschedule",
				"ps_id");
		baseDao.execute(formSql);
		/*
		 * List<Map<Object, Object>> grid2 = BaseUtil
		 * .parseGridStoreToMaps(gridStore); List<String> gridSql2 = null; if
		 * (grid2.size() > 0) { gridSql2 =
		 * SqlUtil.getUpdateSqlbyGridStore(grid2, "SellerSaleReportDET",
		 * "ssd_id"); for (Map<Object, Object> s : grid2) { Object aid =
		 * s.get("ssd_id"); if (aid == null || "".equals(aid.toString()) ||
		 * Integer.parseInt(aid.toString()) == 0) { int id =
		 * baseDao.getSeqId("SellerSaleReportdet_SEQ"); String sql =
		 * SqlUtil.getInsertSqlByMap(s, "SellerSaleReportDET", new String[] {
		 * "ssd_id" }, new Object[] { id }); gridSql2.add(sql); } }
		 * baseDao.execute(gridSql2); }
		 */
		/*
		 * List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
		 * "SellerSaleReportDet", "ssd_id"); baseDao.execute(gridSql);
		 */
		// 记录操作
		baseDao.logger.update(caller, "ps_id", store.get("ps_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
}
