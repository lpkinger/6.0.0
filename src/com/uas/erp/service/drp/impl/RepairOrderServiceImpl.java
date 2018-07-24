package com.uas.erp.service.drp.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;

import com.uas.erp.model.Employee;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.drp.RepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RepairOrderServiceImpl implements RepairOrderService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private SingleFormItemsService singleformitemsservice;

	@Override
	public void saveRepairOrder(String formStore, String gridStore,
			String caller) {
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"RepairOrder", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Object[] rwd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			rwd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				rwd_id[i] = baseDao.getSeqId("REPAIRORDERDETAIL_SEQ");
			}
		} else {
			rwd_id[0] = baseDao.getSeqId("REPAIRORDERDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"RepairOrderDetail", "rod_id", rwd_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ro_id", store.get("ro_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });

	}

	@Override
	public void updateRepairOrderById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"RepairOrder", "ro_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"RepairOrderDetail", "rod_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("rod_id") == null || s.get("rod_id").equals("")
					|| s.get("rod_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("REPAIRORDERDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "RepairOrderDetail",
						new String[] { "rod_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ro_id", store.get("ro_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteRepairOrder(int ro_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ro_id);
		// 删除purchase
		baseDao.deleteById("RepairOrder", "ro_id", ro_id);
		// 删除purchaseDetail
		baseDao.deleteById("RepairOrderDetail", "rod_roid", ro_id);
		// 记录操作
		baseDao.logger.delete(caller, "ro_id", ro_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ro_id);

	}

	@Override
	public void auditRepairOrder(int ro_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("RepairOrder",
				"ro_statuscode", "ro_id=" + ro_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ro_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"RepairOrder",
				"ro_statuscode='AUDITED',ro_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ro_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',ro_auditdate=sysdate", "ro_id=" + ro_id);
		// 记录操作
		baseDao.logger.audit(caller, "ro_id", ro_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ro_id);

	}

	@Override
	public void resAuditRepairOrder(int ro_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("RepairOrder",
				"ro_statuscode", "ro_id=" + ro_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ro_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"RepairOrder",
				"ro_statuscode='ENTERING',ro_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ro_auditer='',ro_auditdate=null", "ro_id=" + ro_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ro_id", ro_id);
		handlerService.afterResAudit(caller, ro_id);

	}

	@Override
	public void submitRepairOrder(int ro_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("RepairOrder",
				"ro_statuscode", "ro_id=" + ro_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ro_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"RepairOrder",
				"ro_statuscode='COMMITED',ro_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ro_id="
						+ ro_id);
		baseDao.updateByCondition("RepairOrderDetail", "rod_isturn='未转'",
				"rod_roid=" + ro_id);
		// 记录操作
		baseDao.logger.submit(caller, "ro_id", ro_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ro_id);

	}

	@Override
	public void resSubmitRepairOrder(int ro_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("RepairOrder",
				"ro_statuscode", "ro_id=" + ro_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ro_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"RepairOrder",
				"ro_statuscode='ENTERING',ro_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ro_id="
						+ ro_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ro_id", ro_id);
		handlerService.afterResSubmit(caller, ro_id);

	}

	@Override
	public String turnRepairWork(String caller, int roid) {
		SqlRowList list = baseDao
				.queryForRowSet("SELECT * FROM RepairOrder WHERE ro_ID = "
						+ roid);
		Employee employee = SystemSession.getUser();
		Map<String, Object> repairOrder = list.getResultList().get(0);
		String sql = "";
		int rw_id;
		SqlRowList repairOrderDetails = baseDao
				.queryForRowSet("SELECT * FROM RepairOrderDetail WHERE rod_roid="
						+ roid);
		StringBuffer sb = new StringBuffer();
		sb.append("转入成功,维修单单号:");
		sb.append("<br>");
		for (Map detail : repairOrderDetails.getResultList()) {
			rw_id = baseDao.getSeqId("REPAIRWORK_SEQ");
			String code = singleformitemsservice.getCodeString("RepairWork", "RepairWork", 2);
			try {
				sql = String
						.format("INSERT INTO REPAIRWORK(RW_ID,RW_CODE,RW_STATUS,RW_REPAIREMID,RW_REPAIREMNAME,RW_OTHERENID,RW_OTHERENNAME,RW_ENID,RW_ENNAME,RW_EMID,RW_EMNAME,"
								+ "RW_RECORDDATE,RW_SOURCEID,RW_SOURCEDETAILID,RW_PRODCODE,RW_PRODNAME,RW_SPEC,RW_UNIT,RW_BATCHCODE,RW_ISOK,RW_REMARK,RW_ClASS,RW_STATUSCODE) "
								+ "VALUES(%d,'%s','%s',%d,'%s',%d,'%s',%d,'%s',%d,'%s',%s,%d,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
								rw_id,
								code,
								"在录入",
								((BigDecimal) repairOrder.get("ro_REPAIREMID"))
										.intValue(),
								repairOrder.get("ro_REPAIREMNAME"),
								((BigDecimal) repairOrder.get("ro_OTHERENID"))
										.intValue(),
								repairOrder.get("ro_OTHERENNAME"),
								((BigDecimal) repairOrder.get("ro_ENID"))
										.intValue(),
								repairOrder.get("ro_ENNAME"),
								employee.getEm_id(),
								employee.getEm_name(),
								"to_date('"
										+ new SimpleDateFormat("yyyy-MM-dd")
												.format(new Date())
										+ "','yyyy-MM-dd')", roid,
								detail.get("rod_ID") + "", detail
										.get("rod_PRODCODE"), detail
										.get("rod_PRODNAME"), detail
										.get("rod_SPEC"), detail
										.get("rod_UNIT"), detail
										.get("rod_BATCHCODE"), detail
										.get("rod_ISOK"), detail
										.get("rod_REMARK"), repairOrder
										.get("ro_CLASS"), "ENTERING");
			} catch (Exception e) {
				e.printStackTrace();
			}
			baseDao.execute(sql);
			String log  = "<a href=\"javascript:openUrl('jsps/drp/aftersale/repairwork.jsp?whoami=RepairWork&formCondition=rw_idIS"+rw_id+"&gridCondition=rwd_rwidIS" + rw_id
					+ "')\">" + code+ "</a>&nbsp;<br>";
			sb.append(log);
		}
		return sb.toString();
	}

	@Override
	@Transactional
	public String batchCreateRepairOrder(String data, String caller) {
		Employee employee = SystemSession.getUser();
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int id = baseDao.getSeqId("RepairOrder_SEQ");
		String code = baseDao.sGetMaxNumber("RepairOrder", 2);
		int detno = 1;
		String insertSql = "insert into RepairOrder(ro_id,ro_code,ro_status,ro_statuscode,ro_emname,ro_recorddate) values (?,?,?,?,?,sysdate)";
		String insertDetSql = "insert into RepairOrderDetail(rod_detno,rod_prodcode,rod_prodname,rod_spec,rod_unit,rod_qty,rod_id,rod_roid) values (?,?,?,?,?,?,RepairOrderDetail_seq.nextval,?)";
		for (Map<Object, Object> map : maps) {
			baseDao.execute(
					insertDetSql,
					new Object[] { detno++, map.get("pw_prodcode"),
							map.get("pr_detail"), map.get("pr_spec"),
							map.get("pr_unit"), map.get("need"), id });
		}
		baseDao.execute(insertSql,
				new Object[] { id, code, BaseUtil.getLocalMessage("ENTERING"),
						"ENTERING", employee.getEm_name() });
		return "转入成功,派工单号:<a href=\"javascript:openUrl('jsps/drp/aftersale/repairorder.jsp?whoami=RepairOrder&formCondition=ro_idIS"
				+ id
				+ "&gridCondition=rod_roidIS"
				+ id
				+ "')\">"
				+ code
				+ "</a>&nbsp;";
	}

	@Override
	@Transactional
	public String batchTurnRepairWork(String data, String caller) {
		Employee employee = SystemSession.getUser();
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int id = baseDao.getSeqId("RepairWork_SEQ");
		String code = baseDao.sGetMaxNumber("RepairWork", 2);
		int detno = 1;
		String insertSql = "insert into RepairWork(rw_id,rw_code,rw_status,rw_statuscode,rw_emname,rw_recorddate) values (?,?,?,?,?,sysdate)";
		String insertDetSql = "insert into RepairWorkDetail(rwd_detno,rwd_prodcode,rwd_prodname,rwd_spec,rwd_unit,rwd_qty,rwd_id,rwd_rwid,"
				+ "rwd_resourcecode,rwd_resourcedetdetno,rwd_resourcedetid) values (?,?,?,?,?,?,RepairWorkDetail_seq.nextval,?,?,?,?)";
		for (Map<Object, Object> map : maps) {
			baseDao.updateByCondition("RepairOrderDetail", "rod_isturn='已转'",
					"rod_id=" + map.get("rod_id"));
			baseDao.execute(
					insertDetSql,
					new Object[] { detno++, map.get("rod_prodcode"),
							map.get("rod_prodname"), map.get("rod_spec"),
							map.get("rod_unit"), map.get("rod_qty"), id,
							map.get("ro_code"), map.get("rod_detno"),
							map.get("rod_id") });
		}
		baseDao.execute(insertSql,
				new Object[] { id, code, BaseUtil.getLocalMessage("ENTERING"),
						"ENTERING", employee.getEm_name() });
		return "转入成功,维修单号:<a href=\"javascript:openUrl('jsps/drp/aftersale/repairwork.jsp?whoami=RepairWork&formCondition=rw_idIS"
				+ id
				+ "&gridCondition=rwd_rwidIS"
				+ id
				+ "')\">"
				+ code
				+ "</a>&nbsp;";
	}

}
