package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.BarPrintService;

@Service
public class BarPrintServiceImpl implements BarPrintService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBarPrint(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("bp_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BarPrint", "bp_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {store, grid});
		// 保存BarPrint
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BarPrint", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存BarPrintDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "BarPrintDetail", "bpd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "bp_id", store.get("bp_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {store, grid});
	}

	@Override
	public void updateBarPrint(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {store, gstore});
		// 修改BarPrint
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BarPrint", "bp_id");
		baseDao.execute(formSql);
		// 修改BarPrintDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(gstore, "BarPrintDetail", "bpd_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "bp_id", store.get("bp_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}

	@Override
	public String Subpackage(int bp_id, double qty, String caller) {
		int barNum = 0;
		double packageqty = 0;
		double vadqty = 0;
		double remainQty = 0;
		double tqty = 0;
		int count = baseDao.getCountByCondition("BarPrintDetail", "bpd_bpid=" + bp_id);
		if (count > 0) {
			BaseUtil.showError("已经有分装明细,如果需要重新分装请通过[清除分装明细]按钮先清除后再进行分装!");
		}
		baseDao.execute("update BarPrint set bp_unitpackage=? where bp_id=?", qty, bp_id);
		SqlRowList rs = baseDao.queryForRowSet("SELECT bp_qty,bp_unitpackage FROM BarPrint WHERE bp_id=" + bp_id);
		while (rs.next()) {
			packageqty = rs.getDouble("bp_unitpackage");
			vadqty = rs.getDouble("bp_qty");
			if (packageqty > 0 && vadqty > 0) {
				barNum = (int) (Math.ceil(vadqty / packageqty));
				remainQty = vadqty;
				for (int i = 1; i <= barNum; i++) {
					if (remainQty >= packageqty) {
						tqty = packageqty;
					} else {
						tqty = remainQty;
					}
					baseDao.execute("insert into BarPrintDetail(bpd_id,bpd_bpid,bpd_detno,bpd_qty) values(?,?,?,?)",
							baseDao.getSeqId("BARPRINTDETAIL_SEQ"), bp_id, i, tqty);
					remainQty = remainQty - tqty;
					if (remainQty <= 0) {
						break;
					}
				}
			}
		}
		return "分装确认成功!";
	}

	@Override
	public String ClearSubpackage(int bp_id, String caller) {
		baseDao.execute("delete from BarPrintDetail where bpd_bpid=" + bp_id);
		return "清除分装明细成功!";
	}

	@Override
	public String[] printBar(int bp_id, String caller, String reportName, String condition) {
		boolean bool = baseDao.checkIf("BarPrint", "bp_id=" + bp_id
				+ " and round(bp_qty,2)<>(select round(sum(bpd_qty),2) from BarPrintDetail where bpd_bpid=bp_id)");
		if (bool) {
			BaseUtil.showError("当前的总数量与分装明细总数不等,不能打印条码!");
		}
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		return keys;
	}
}
