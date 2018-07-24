package com.uas.erp.service.hr.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.WageStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: USOFTPC30 Date: 13-6-17 Time: 上午9:26 To
 * change this template use File | Settings | File Templates.
 */
@Service
public class WageStandardServiceImpl implements WageStandardService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveWageStandard(String formStore, String gridStore,
			String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] {store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"WageStandard", new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		Object[] wsd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			wsd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				wsd_id[i] = baseDao.getSeqId("WAGESTANDARDDETAIL_SEQ");
			}
		} else {
			wsd_id[0] = baseDao.getSeqId("WAGESTANDARDDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"WageStandardDetail", "wsd_id", wsd_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ws_id", store.get("ws_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store});
	}

	@Override
	public void updateWageStandardById(String formStore, String gridStore,
			String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] {store,gstore});
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"WageStandard", "ws_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"WageStandardDetail", "wsd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("wsd_id") == null || s.get("wsd_id").equals("")
					|| s.get("wsd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("WAGESTANDARDDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "WageStandardDetail",
						new String[] { "wsd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ws_id", store.get("ws_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store,gstore});
	}

	@Override
	public void deleteWageStandard(int ws_id, String  caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {ws_id});
		// 删除purchase
		baseDao.deleteById("WageStandard", "ws_id", ws_id);
		// 删除purchaseDetail
		baseDao.deleteById("WageStandardDetail", "wsd_wsid", ws_id);
		// 记录操作
		baseDao.logger.delete(caller, "ws_id", ws_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {ws_id});
	}

	@Override
	public void setEmpWageStandard(int wsid, String condition,
			String caller) {
		baseDao.updateByCondition("employee", "em_wsid=" + wsid, condition);
	}

	@Override
	public void submitWageStandard(int ws_id, String  caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("WageStandard",
				"ws_statuscode", "ws_id=" + ws_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] {ws_id});
		// 执行提交操作
		baseDao.submit("WageStandard", "ws_id=" + ws_id, "ws_status", "ws_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ws_id", ws_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {ws_id});
	}

	@Override
	public void resSubmitWageStandard(int ws_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("WageStandard",
				"ws_statuscode", "ws_id=" + ws_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { ws_id});
		// 执行反提交操作
		baseDao.resOperate("WageStandard", "ws_id=" + ws_id, "ws_status", "ws_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ws_id", ws_id);
		handlerService.afterResSubmit(caller, new Object[] { ws_id});

	}

	@Override
	public void auditWageStandard(int ws_id, String  caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("WageStandard",
				"ws_statuscode", "ws_id=" + ws_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[] {ws_id});
		// 执行审核操作
		baseDao.audit("WageStandard", "ws_id=" + ws_id, "ws_status", "ws_statuscode", "ws_auditdate", "ws_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "ws_id", ws_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] {ws_id});
	}

	@Override
	public void resAuditWageStandard(int ws_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller,new Object[] { ws_id});
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("WageStandard",
				"ws_statuscode", "ws_id=" + ws_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("WageStandard", "ws_id=" + ws_id, "ws_status", "ws_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ws_id", ws_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller,new Object[] { ws_id});
	}

	@Override
	public void payAccount(Integer param, String  caller) {
		String res = null;
		res = baseDao.callProcedure("SP_COUNTWAGESTAND", new Object[]{param});
		if(res != null && !res.trim().equals("OK")){
			BaseUtil.showError(res);
		}
		
	}
}
