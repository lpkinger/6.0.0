package com.uas.erp.service.drp.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AskRepairDao;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.MessageLog;

import com.uas.erp.model.Enterprise;

import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.drp.AskRepairService;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 客户报修单 service 实现
 */
@Service
public class AskRepairServiceImpl implements AskRepairService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private AskRepairDao askRepairDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private DataListComboDao dataListComboDao;

	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	public void saveAskRepair(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"CustomerRepair", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "cr_id", store.get("cr_id"));
		// 保存Detail
		Object[] id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			id[i] = baseDao.getSeqId("CUSTOMERREPAIRDETAIL_SEQ");
			map.put("crd_id", id[i]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"CUSTOMERREPAIRDETAIL");
		baseDao.execute(gridSql);

		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void updateAskRepair(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);

		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });

		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"CustomerRepair", "cr_id");
		baseDao.execute(formSql);

		// 修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,
				"CustomerRepairDetail", "crd_id");
		for (Map<Object, Object> s : gstore) {
			Object vadid = s.get("crd_id");
			if (vadid == null || vadid.equals("") || vadid.equals("0")
					|| Integer.parseInt(vadid.toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CUSTOMERREPAIRDETAIL_SEQ");
				// s.put("crd_status", "ENTERING");
				s.put("crd_id", id);
				String sql = SqlUtil.getInsertSqlByMap(s,
						"CustomerRepairDetail", new String[] { "crd_id" },
						new Object[] { id });
				gridSql.add(sql);
			} else {

			}
		}
		baseDao.execute(gridSql);

		// 记录操作
		baseDao.logger.update(caller, "cr_id", store.get("cr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteAskRepair(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		// 删除
		baseDao.deleteById("CustomerRepair", "cr_id", id);
		// 删除明细
		baseDao.deleteByCondition("CustomerRepairDetail", "crd_crid=" + id);

		// 记录操作
		baseDao.logger.delete(caller, "cr_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, id);
	}

	@Override
	public List<DataListCombo> getRepairOrderType(String caller) {
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(
				caller, SpObserver.getSp());
		if (combos != null && combos.size() > 0) {
			return combos;
		}
		return Collections.emptyList();
	}

	@Override
	public void turnRepairOrder(String caller, int crid, int em_uu,
			String em_name, String rotype, String crdids) {
		SqlRowList list = baseDao
				.queryForRowSet("SELECT * FROM CUSTOMERREPAIRDETAIL WHERE crd_id in ("
						+ crdids + ")");
		int ro_id = baseDao.getSeqId("REPAIRORDER_SEQ");
		Enterprise enterprise = enterpriseService
				.getEnterpriseById(SystemSession.getUser().getEm_enid());
		String mainSql = String
				.format("INSERT INTO REPAIRORDER(RO_ID,RO_CODE,RO_CLASS,RO_STATUS,RO_REPAIREMID,RO_REPAIREMNAME,RO_OTHERENID,"
						+ "RO_OTHERENNAME,RO_ENID,RO_ENNAME,RO_EMID,RO_EMNAME,RO_RECORDDATE,RO_SOURCEID,RO_REMARK,RO_STATUSCODE) "
						+ "VALUES(%d,'%s','%s','%s',%d,'%s',%d,%s,%d,'%s',%d,'%s',%s,%d,%s,'%s')",
						ro_id,
						ro_id + "",
						rotype,
						"在录入",
						em_uu,
						em_name,
						0,
						"null",
						enterprise.getEn_uu(),
						enterprise.getEn_Name(),
						SystemSession.getUser().getEm_uu(),
						SystemSession.getUser().getEm_name(),
						"to_date('"
								+ new SimpleDateFormat("yyyy-MM-dd")
										.format(new Date()) + "','yyyy-MM-dd')",
						crid, "null", "ENTERING");
		baseDao.execute(mainSql);

		// insert detail
		String detail = "";
		int detno = 1;
		for (Map itemMap : list.getResultList()) {
			detail = String
					.format("INSERT INTO REPAIRORDERDETAIL(ROD_ID,ROD_ROID,ROD_DETNO,ROD_PRODCODE,ROD_PRODNAME,ROD_SPEC,ROD_UNIT,ROD_BATCHCODE,ROD_ISOK,ROD_FAULT,ROD_STATUS,ROD_REMARK)"
							+ " VALUES(%d,%d,%d,'%s','%s','%s','%s','%s','%s','%s','%s','%s')",
							baseDao.getSeqId("REPAIRORDERDETAIL_SEQ"), ro_id,
							detno++, itemMap.get("CRD_PRODCODE"),
							itemMap.get("CRD_PRODNAME"),
							itemMap.get("CRD_SPEC"), itemMap.get("CRD_UNIT"),
							itemMap.get("CRD_BATCHCODE"),
							itemMap.get("CRD_ISOK"), itemMap.get("CRD_FAULTT"),
							itemMap.get("CRD_STATUS"),
							itemMap.get("CRD_REMARK"));
			baseDao.execute(detail);
		}
	}

	@Override
	public void resAuditAskRepair(int cr_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CustomerRepair",
				"cr_statuscode", "cr_id=" + cr_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, cr_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"CustomerRepair",
				"cr_statuscode='ENTERING',cr_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',cr_auditer='',cr_auditdate=null", "cr_id=" + cr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "cr_id", cr_id);
		handlerService.afterResAudit(caller, cr_id);

	}

	@Override
	public void auditAskRepair(int cr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("CustomerRepair",
				"cr_statuscode", "cr_id=" + cr_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, cr_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"CustomerRepair",
				"cr_statuscode='AUDITED',cr_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',cr_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',cr_auditdate=sysdate", "cr_id=" + cr_id);
		// 记录操作
		baseDao.logger.approve(caller, "cr_id", cr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, cr_id);

	}

	@Override
	public void resSubmitAskRepair(int cr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CustomerRepair",
				"cr_statuscode", "cr_id=" + cr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, cr_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"CustomerRepair",
				"cr_statuscode='ENTERING',cr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "cr_id="
						+ cr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "cr_id", cr_id);
		handlerService.afterResSubmit(caller, cr_id);

	}

	@Override
	public void submitAskRepair(int cr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CustomerRepair",
				"cr_statuscode", "cr_id=" + cr_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, cr_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"CustomerRepair",
				"cr_statuscode='COMMITED',cr_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "cr_id="
						+ cr_id);
		baseDao.updateByCondition("CustomerRepairDetail", "crd_isturn='未转'",
				"crd_crid=" + cr_id);
		// 记录操作
		baseDao.logger.submit(caller, "cr_id", cr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, cr_id);

	}

	@Override
	public String batchTurnRepairOrder(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object repairman = null;
		Object vCode = null;
		Object code = null;
		String log = null;
		JSONObject j = null;
		StringBuffer sb = new StringBuffer();
		Set<String> rocodes = new HashSet<String>();
		int index = 0;
		Map<String, List<Map<Object, Object>>> custs = new HashMap<String, List<Map<Object, Object>>>();
		if (maps.get(0).get("em_name") != null
				&& !"".equals(maps.get(0).get("em_name").toString())) {
			repairman = maps.get(0).get("em_name");
		} else {
			repairman = SystemSession.getUser().getEm_name();
		}

		for (Map<Object, Object> map : maps) {
			vCode = map.get("cr_cucode");
			// 没有指定供应商，按照明细供应商号+币别分组
			List<Map<Object, Object>> list = null;
			if (!custs.containsKey(vCode)) {
				list = new ArrayList<Map<Object, Object>>();
			} else {
				list = custs.get(vCode);
			}
			list.add(map);
			custs.put(vCode.toString(), list);
		}
		Set<String> mapSet = custs.keySet();
		String custcode = "";
		for (String s : mapSet) {
			List<Map<Object, Object>> list = custs.get(s);
			custcode = s;
			j = askRepairDao.turnRepairOrder(SystemSession.getLang(),
					SystemSession.getUser(), custcode, repairman.toString());
			if (j != null) {
				code = j.getString("ro_code");
				rocodes.add(code.toString());
				for (Map<Object, Object> map : custs.get(s)) {
					int crid = Integer.parseInt(map.get("crd_id").toString());
					index++;
					askRepairDao.toRepairOrderDetail(code.toString(), crid,
							SystemSession.getLang(), SystemSession.getUser());

					// 记录日志
					Object[] cts = baseDao.getFieldsDataByCondition(
							"CustomerRepairDetail", "crd_crid,crd_detno",
							"crd_id=" + crid);
					baseDao.logMessage(new MessageLog(SystemSession.getUser()
							.getEm_name(), BaseUtil
							.getLocalMessage("msg.turnRepairOrder"), BaseUtil
							.getLocalMessage("msg.turnSuccess")
							+ ","
							+ BaseUtil.getLocalMessage("msg.detail") + cts[1],
							"CustomerRepair|cr_id=" + cts[0]));
					baseDao.execute("update CustomerRepairDetail set crd_status='已转派工单' where crd_id="
							+ crid);
				}
				log = "转入成功,派工单:"
						+ "<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_idIS"
						+ j.get("pu_id") + "&gridCondition=pd_puidIS"
						+ j.get("pu_id") + "')\">" + code + "</a>&nbsp;";
				sb.append(index + ":" + log + "<hr/>");
			}
		}

		/*
		 * if(!adidstr.equals("")){ adidstr=adidstr.substring(1); SqlRowList rs
		 * = baseDao.queryForRowSet(
		 * "select  count(1) n from (select distinct NVL(pk_mrp,0) kind from application,applicationdetail,purchasekind where ap_id=ad_apid and ad_id in ("
		 * +adidstr+") and ap_kind=pk_name)"); if (rs.next()) { if
		 * (rs.getInt("n")>1){ BaseUtil.showError("参与MRP运算的请购必须与不参与的请购分开下达!"); }
		 * } }
		 */

		/*
		 * j = askRepairDao.newPurchaseWithVendor(language,
		 * SystemSession.getUser(),custcode);
		 * 
		 * int ro_id = baseDao.getSeqId("REPAIRORDER_SEQ"); String rocode =
		 * baseDao.sGetMaxNumber("RepairOrder", 2); String mainSql =
		 * String.format(
		 * "INSERT INTO REPAIRORDER(RO_ID,RO_CODE,RO_CLASS,RO_STATUS,RO_REPAIREMNAME,"
		 * + "RO_EMNAME,RO_RECORDDATE,RO_REMARK,RO_STATUSCODE) " +
		 * "VALUES(%d,'%s','%s','%s','%s',%s,'%s','%s','%s')", ro_id, rocode +
		 * "", "", "在录入", repairman, SystemSession.getUser().getEm_name(),
		 * "to_date('" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) +
		 * "','yyyy-MM-dd')", "null", "ENTERING"); baseDao.execute(mainSql);
		 * SqlRowList list = baseDao.queryForRowSet(
		 * "SELECT * FROM CUSTOMERREPAIRDETAIL left join CUSTOMERREPAIR on crd_crid=cr_id WHERE crd_id in ("
		 * + adidstr + ")"); String detail = "";
		 * 
		 * int detno = 1; for (Map itemMap : list.getResultList()) { detail =
		 * String.format(
		 * "INSERT INTO REPAIRORDERDETAIL(ROD_ID,ROD_ROID,ROD_DETNO,ROD_PRODCODE,ROD_PRODNAME,ROD_SPEC,ROD_UNIT,ROD_BATCHCODE,ROD_ISOK,ROD_FAULT,ROD_STATUS,ROD_REMARK)"
		 * + " VALUES(%d,%d,%d,'%s','%s','%s','%s','%s','%s','%s','%s','%s')",
		 * baseDao.getSeqId("REPAIRORDERDETAIL_SEQ"), ro_id, detno++,
		 * itemMap.get("CRD_PRODCODE"), itemMap.get("CRD_PRODNAME"),
		 * itemMap.get("CRD_SPEC"), itemMap.get("CRD_UNIT"),
		 * itemMap.get("CRD_BATCHCODE"), itemMap.get("CRD_ISOK"),
		 * itemMap.get("CRD_FAULTT"), itemMap.get("CRD_STATUS"),
		 * itemMap.get("CRD_REMARK")); baseDao.execute(detail); }
		 */
		return sb.toString();
	}

	@Override
	@Transactional
	public String batchTurnPartCheck(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		baseDao.updateByCondition("CustomerRepair", "cr_isturn='已转分检'",
				"cr_code='" + maps.get(0).get("cr_code") + "'");// 现在都是整个单一起转，所以编号都是一样的，直接取一个就好
		int id = baseDao.getSeqId("PartCheck_SEQ");
		String code = baseDao.sGetMaxNumber("PartCheck", 2);
		String insertSql = "insert into PartCheck(pc_id,pc_code,pc_recorder,pc_recorddate,pc_status,pc_statuscode) values (?,?,?,sysdate,?,?)";
		int detno = 1;
		String insertDetSql = "insert into partcheckdet(pcd_id,pcd_pcid,pcd_detno,pcd_firstcheckcode,pcd_firstcheck,"
				+ "pcd_date,pcd_prodcode,pcd_prodname,pcd_prodspec,pcd_num,pcd_trouble,pcd_resourcecode,pcd_resourcedetid,pcd_resourcedetno) values (partcheckdet_seq.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		for (Map<Object, Object> map : maps) {
			Timestamp ts = null;
			if (map.get("crd_faultdate").toString() != null
					&& !"".equals(map.get("crd_faultdate").toString())) {
				try {
					ts = Timestamp.valueOf(map.get("crd_faultdate").toString());
				} catch (Exception e) {
					ts = null;
				}
			}
			baseDao.updateByCondition("CustomerRepairDetail",
					"crd_isturn='已转'", "crd_id=" + map.get("crd_id"));
			double num = 0;
			num = Double.parseDouble(map.get("crd_qty").toString());
			for (int i = 0; i < num; i++) {// 拆分，每个明细数量都为1
				baseDao.execute(
						insertDetSql,
						new Object[] { id, detno++, map.get("cr_cucode"),
								map.get("cr_otherenname"), ts,
								map.get("crd_prodcode"),
								map.get("crd_prodname"), map.get("crd_spec"),
								1, map.get("crd_faultt"), map.get("cr_code"),
								map.get("crd_id"), map.get("crd_detno") });
			}
		}
		baseDao.execute(insertSql,
				new Object[] { id, code, SystemSession.getUser().getEm_name(),
						BaseUtil.getLocalMessage("ENTERING"), "ENTERING" });
		return "转入成功,分检信息单号:<a href=\"javascript:openUrl('jsps/drp/aftersale/partCheck.jsp?formCondition=pc_idIS"
				+ id
				+ "&gridCondition=pcd_pcidIS"
				+ id
				+ "')\">"
				+ code
				+ "</a>&nbsp;";
	}

	@Override
	public void confirmCustomerRepair(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CustomerRepair",
				"cr_statuscode", "cr_id=" + id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.confirm_onlyAudit"));
		}
		// 执行反审核操作
		baseDao.updateByCondition("CustomerRepair",
				"cr_confirmtime=sysdate,cr_confirmman='"
						+ SystemSession.getUser().getEm_name()
						+ "',cr_confirmstatus='已处理'", "cr_id=" + id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.confirm"), BaseUtil
						.getLocalMessage("msg.confirmSuccess"),
				"CustomerRepair|cr_id=" + id));
	}
}
