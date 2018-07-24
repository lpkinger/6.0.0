package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.StockTakingDao;
import com.uas.erp.service.scm.StockTakingService;

@Service("StockTakingService")
public class StockTakingServiceImpl implements StockTakingService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private StockTakingDao stockTakingDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void deleteStockTaking(int st_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("StockTaking", "st_statuscode", "st_id=" + st_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { st_id });
		// 删除StockTaking
		baseDao.deleteById("StockTaking", "st_id", st_id);
		// 删除StockTakingDetail
		baseDao.deleteById("StockTakingdetail", "std_stid", st_id);
		// 记录操作
		baseDao.logger.delete(caller, "st_id", st_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { st_id });
	}

	@Override
	public void updateStockTakingById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("StockTaking", "st_statuscode", "st_id=" + store.get("st_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改StockTaking
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "StockTaking", "st_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "StockTakingDetail", "std_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("std_id") == null || s.get("std_id").equals("") || s.get("std_id").equals("0")
					|| Integer.parseInt(s.get("std_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("STOCKTAKINGDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "StockTakingDetail", new String[] { "std_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "st_id", store.get("st_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	@Transactional
	public void auditStockTaking(int st_id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { st_id });
		// 执行审核操作
		StringBuffer sb = new StringBuffer();
		String piclass = null;
		String call = null;
		Object[] pi = null;
		String code = null;
		Object statuscode = baseDao.getFieldDataByCondition("StockTaking", "st_statuscode", "st_id=" + st_id);
		if (!"ENTERING".equals(statuscode)) {
			BaseUtil.showError("只能对在录入的单据进行审核操作!");
		}
		int i = 1;
		int count = 1;
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from StockTakingDetail left join StockTaking on std_stid=st_id where std_stid=? and nvl(std_batchqty,0)<>nvl(std_actqty,0) and nvl(std_actqty,0)<nvl(std_batchqty,0)",
						st_id);
		while (rs.next()) {
			if (i == 1) {
				piclass = "盘亏调整单";
				call = "ProdInOut!StockLoss";
				pi = stockTakingDao.turnProdIO(piclass, rs.getString("st_whcode"), rs.getString("st_code"), call);
				sb.append("转入成功,盘亏调整单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi[1]
						+ "&gridCondition=pd_piidIS" + pi[1] + "&whoami=ProdInOut!StockLoss')\">" + pi[0] + "</a>&nbsp;<br>");
			}
			if (pi != null) {
				code = pi[0].toString();
				baseDao.execute("update StockTakingDetail set std_outcode = '" + code + "' where std_id=" + rs.getInt("std_id"));
				stockTakingDao.turnProdIODetail(rs.getInt("std_id"), count++, code, pi[1], piclass);
			}
			i = i + 1;
		}
		//2018-05-24 dyl 反馈2018050197
		//按批次盘点时，若盘点单的 批号、生产日期、有效日期不为空 按盘点明细的生产日期、有效日期更新批次的生产日期、有效日期
		if(baseDao.isDBSetting("Inventory","inventoryByBatch")){
			baseDao.execute("update batch set ba_date=(select trunc(std_prodmadedate) from StockTakingDetail left join StockTaking on std_stid=st_id where std_stid="+st_id+" and ba_code=std_batchcode and ba_prodcode=std_prodcode and ba_whcode=st_whcode)"
					+ "where (BA_CODE,BA_PRODCODE,BA_WHCODE) IN (SELECT std_batchcode,std_prodcode,st_whcode from StockTakingDetail left join StockTaking on std_stid=st_id where std_stid="+st_id+" and nvl(std_batchcode,' ')<>' ' and std_prodmadedate is not null)");
			baseDao.execute("update batch set ba_validtime=(select trunc(std_validtime) from StockTakingDetail left join StockTaking on std_stid=st_id where std_stid="+st_id+" and ba_code=std_batchcode and ba_prodcode=std_prodcode and ba_whcode=st_whcode)"
					+ "where (BA_CODE,BA_PRODCODE,BA_WHCODE) IN (SELECT std_batchcode,std_prodcode,st_whcode from StockTakingDetail left join StockTaking on std_stid=st_id where std_stid="+st_id+" and nvl(std_batchcode,' ')<>' ' and std_validtime is not null)");
		
		}
		rs = baseDao
				.queryForRowSet(
						"select * from StockTakingDetail left join StockTaking on std_stid=st_id where std_stid=? and nvl(std_batchqty,0)<>nvl(std_actqty,0) and nvl(std_actqty,0)>nvl(std_batchqty,0)",
						st_id);
		i = 1;
		count = 1;
		while (rs.next()) {
			if (i == 1) {
				piclass = "盘盈调整单";
				call = "ProdInOut!StockProfit";
				pi = stockTakingDao.turnProdIO(piclass, rs.getString("st_whcode"), rs.getString("st_code"), call);
				sb.append("转入成功,盘盈调整单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi[1]
						+ "&gridCondition=pd_piidIS" + pi[1] + "&whoami=ProdInOut!StockProfit')\">" + pi[0] + "</a>&nbsp;<br>");
			}
			if (pi != null) {
				code = pi[0].toString();
				baseDao.execute("update StockTakingDetail set std_incode = '" + code + "' where std_id=" + rs.getInt("std_id"));
				stockTakingDao.turnProdIODetail(rs.getInt("std_id"), count++, code, pi[1], piclass);
			}
			i = i + 1;
		}
		baseDao.audit("StockTaking", "st_id=" + st_id, "st_status", "st_statuscode", "st_auditdate", "st_auditman");
		// 更新物料资料中的上次盘点日期
		baseDao.execute("update product set pr_precheckdate=(select st_date from StockTaking where st_id=" + st_id
				+ ") where exists (select 1 from StockTakingDetail where std_prodcode=pr_code and std_stid=" + st_id + ")");
		baseDao.logger.audit(caller, "st_id", st_id);
		if (sb.length() > 0) {
			BaseUtil.appendError(sb.toString());
		}
	}

	@Override
	public void resAuditStockTaking(int st_id, String caller) {
		// 只能反审核已审核的资料!
		Object status = baseDao.getFieldDataByCondition("StockTaking", "st_statuscode", "st_id=" + st_id);
		StateAssert.resAuditOnlyAudit(status);
		Object ios = baseDao.getFieldDataByCondition("ProdIoDetail", "WMSYS.WM_CONCAT(distinct pd_piclass||':'||pd_inoutno)",
				"pd_piclass in ('盘盈调整单','盘亏调整单') " + "AND pd_ordercode=(select st_code from StockTaking where st_id=" + st_id + ")");
		if (ios != null) {
			BaseUtil.showError("有相关联的盘盈、盘亏单,无法反审核!相关单据(" + ios + ")");
		}
		handlerService.handler(caller, "resAudit", "before", new Object[] { st_id });
		baseDao.resOperate("StockTaking", "st_id=" + st_id, "st_status", "st_statuscode");
		baseDao.logger.resAudit(caller, "st_id", st_id);
		handlerService.handler(caller, "resAudit", "after", new Object[] { st_id });
	}
}
