package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.AccountCenterService;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.hr.EmployeeManagerService;
import com.uas.erp.service.hr.TurnpositionService;

@Service
public class TurnpositionServiceImpl implements TurnpositionService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private EmployeeManagerService employeeManagerService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private AccountCenterService accountCenterService;

	@Override
	public void saveTurnposition(String formStore, String gridStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		if ("turnfullmemb".equals(store.get("tp_type"))) {
			Object em_class = baseDao.getFieldDataByCondition("Employee", "em_class", "em_code='" + store.get("tp_emcode") + "'");
			if (!"试用".equals(em_class)) {
				BaseUtil.showError("此员工类型不是'试用'，无法转正！");
			}
		} else if ("leave".equals(store.get("tp_type"))) {
			Object em_class = baseDao.getFieldDataByCondition("Employee", "em_class", "em_code='" + store.get("tp_emcode") + "'");
			if ("离职".equals(em_class)) {
				BaseUtil.showError("此员工类型已为'离职'！");
			}
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Turnposition", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存TurnpositionDetail
		Object[] td_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			td_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				td_id[i] = baseDao.getSeqId("TurnpositionDETAIL_SEQ");
			}
		} else {
			td_id[0] = baseDao.getSeqId("TurnpositionDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "TurnpositionDetail", "td_id", td_id);
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "tp_id", store.get("tp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updateTurnpositionById(String formStore, String gridStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		if ("turnfullmemb".equals(store.get("tp_type"))) {
			Object em_class = baseDao.getFieldDataByCondition("Employee", "em_class", "em_code='" + store.get("tp_emcode") + "'");
			if (!"试用".equals(em_class)) {
				BaseUtil.showError("此员工类型不是'试用'，无法转正！");
			}
		} else if ("leave".equals(store.get("tp_type"))) {
			Object em_class = baseDao.getFieldDataByCondition("Employee", "em_class", "em_code='" + store.get("tp_emcode") + "'");
			if ("离职".equals(em_class)) {
				BaseUtil.showError("此员工类型已为'离职'！");
			}
		}
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Turnposition", "tp_id");
		baseDao.execute(formSql);
		// 修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "TurnpositionDetail", "td_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("td_id") == null || s.get("td_id").equals("") || s.get("td_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("TurnpositionDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "TurnpositionDetail", new String[] { "td_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "tp_id", store.get("tp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteTurnposition(int tp_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { tp_id });
		// 删除
		baseDao.deleteById("Turnposition", "tp_id", tp_id);
		// 删除Detail
		baseDao.deleteById("Turnpositiondetail", "td_tpid", tp_id);
		// 记录操作
		baseDao.logger.delete(caller, "tp_id", tp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { tp_id });
	}

	@Override
	public void auditTurnposition(int tp_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Turnposition", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { tp_id });
		// 执行审核操作
		baseDao.audit("Turnposition", "tp_id=" + tp_id, "tp_status", "tp_statuscode", "tp_auditdate", "tp_auditor");
		Object[] ob = baseDao.getFieldsDataByCondition("Turnposition", new String[] { "tp_type", "tp_emcode" }, "tp_id=" + tp_id);
		if ("turnfullmemb".equals(ob[0].toString())) {
			String sql = "update employee set em_class='正式', em_zzdate=sysdate where em_code='" + ob[1] + "'";
			baseDao.execute(sql);
			Object masters = baseDao.getFieldDataByCondition("Master", "wm_concat(ma_user)", " ma_name<>'"
					+ SystemSession.getUser().getEm_master() + "'");
			if (masters != null) {
				Object em_id = baseDao.getFieldDataByCondition("employee left join Turnposition on em_code=tp_emcode", "em_id", "  tp_id="
						+ tp_id);
				baseDao.callProcedure("SYS_POST", new Object[] { "Turnfullmemb!Post", SpObserver.getSp(), masters, String.valueOf(em_id),
						SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
			}
		} else if ("leave".equals(ob[0].toString())) {
			String sql = "update employee set em_class='离职' ,em_leavedate=sysdate where em_code='" + ob[1] + "'";
			baseDao.execute(sql);
			Object masters = baseDao.getFieldDataByCondition("Master", "wm_concat(ma_user)", " ma_name<>'"
					+ SystemSession.getUser().getEm_master() + "'");
			if (masters != null) {
				baseDao.callProcedure("SYS_POST", new Object[] { "Turnover!Post", SpObserver.getSp(), masters, ob[1].toString(),
						SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
			}
			Master master = SystemSession.getUser().getCurrentMaster();
			String ma_accesssecret = master.getMa_accesssecret();
			Long en_uu = master.getMa_uu();
			Object em_uu = baseDao.getFieldDataByCondition("employee", "em_uu", "em_code='" + ob[1] + "'");
			if (em_uu != null && !"".equals(em_uu) && !"0".equals(em_uu)) {
				String b2burl = null;
				if (master.getMa_b2bwebsite() == null || master.getMa_b2bwebsite().equals("null")) {
					b2burl = "http://uas.ubtob.com";
				} else {
					b2burl = master.getMa_b2bwebsite();
				}
				try {
					if (en_uu != null && en_uu > 0) {
						// 更新B2B和帐户中心
						Employee employeeNew = employeeService.getByCondition("em_code = '" + ob[1] + "'","Turnposition");
						if(employeeNew!=null){
							accountCenterService.unbind(employeeNew, master);
						}
						Response response = HttpUtil.sendDeleteRequest(b2burl + "/erp/account/user/" + em_uu + "?access_id=" + en_uu + "",
								null, true, ma_accesssecret);
						if (response.getStatusCode() == 200) {
							baseDao.execute("update employee set em_uu=null,em_imid=null,em_b2benable=0 where em_code='" + ob[1] + "'");
						}
					} else {
						baseDao.execute("update employee set em_uu=null,em_imid=null where em_code='" + ob[1] + "'");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if ("turnposition".equals(ob[0].toString())) {
			int JobOrgNoRelation = SystemSession.getUser().getJoborgnorelation();
			Object hs_id = baseDao.getFieldDataByCondition("Turnposition", "tp_newpositionid", "tp_id=" + tp_id);
			Object[] ob1 = baseDao.getFieldsDataByCondition("Job", new String[] { "jo_code", "jo_name", "jo_orgid", "jo_orgcode",
					"jo_orgname" }, "jo_id=" + hs_id);
			Object emid = baseDao.getFieldDataByCondition("Employee", "em_id", "em_code='" + ob[1] + "'");
			int ap_id = baseDao.getSeqId("ARCHIVEPOSITION_SEQ");
			Object[] ob2 = baseDao.getFieldsDataByCondition("Hrorg", new String[] { "or_departmentcode", "or_department" }, "or_id="
					+ ob1[2]);
			if (JobOrgNoRelation != 0) {
				Object[] objs = baseDao.getFieldsDataByCondition("Turnposition", new String[] { "tp_neworcode", "tp_neworname",
						"tp_newpositionid", "tp_newposition" }, "tp_id=" + tp_id);
				if (objs[0] == null || "".equals(objs[0])) {
					baseDao.execute("update employee set em_defaulthsid=" + hs_id + ", em_defaulthscode='" + ob1[0]
							+ "',em_defaulthsname='" + ob1[1] + "',em_position='" + ob1[1] + "' where em_code='" + ob[1] + "'");
				} else {
					Object[] objs1 = baseDao.getFieldsDataByCondition("hrorg", new String[] { "or_id", "or_name", "or_department",
							"or_departmentcode" }, "or_code='" + objs[0] + "'");
					String sql = "update employee set em_defaulthsid=" + hs_id + ", em_defaulthscode='" + ob1[0] + "',em_defaulthsname='"
							+ ob1[1] + "',em_position='" + ob1[1] + "'" + ",em_defaultorid=" + objs1[0] + ",em_defaultorcode='" + objs[0]
							+ "',em_defaultorname='" + objs[1] + "' ," + "em_departmentcode='" + objs1[3] + "',em_depart='" + objs1[2]
							+ "' where em_code='" + ob[1] + "'";
					baseDao.execute(sql);
				}
			} else {
				String sql = "update employee set em_defaulthsid=" + hs_id + ", em_defaulthscode='" + ob1[0] + "',em_defaulthsname='"
						+ ob1[1] + "',em_position='" + ob1[1] + "'" + ",em_defaultorid=" + ob1[2] + ",em_defaultorcode='" + ob1[3]
						+ "',em_defaultorname='" + ob1[4] + "' ," + "em_departmentcode='" + ob2[0] + "',em_depart='" + ob2[1]
						+ "' where em_code='" + ob[1] + "'";
				baseDao.execute(sql);
				// 更新到人员档案中的异动情况
				Object detno = baseDao.getFieldDataByCondition("ARCHIVEPOSITION left join employee on ap_arid=em_id",
						"nvl(max(ap_detno),0)+1", "em_code='" + ob[1] + "'");
				Object[] ob3 = baseDao.getFieldsDataByCondition(
						"job left join hrorg on jo_orgid=or_id left join Turnposition on jo_id=tp_oldpositionid", new String[] {
								"nvl(jo_name,'')", "nvl(or_department,'')", "tp_date" }, "tp_id=" + tp_id);
				baseDao.execute("insert into archiveposition(ap_id,ap_detno,ap_arid,ap_oripos,ap_oridepart,ap_newpos,ap_newdepart,ap_time,ap_recordtime) "
						+ "values("
						+ ap_id
						+ ","
						+ detno
						+ ","
						+ emid
						+ ",'"
						+ ob3[0]
						+ "','"
						+ ob3[1]
						+ "','"
						+ ob1[1]
						+ "','"
						+ ob2[1]
						+ "',sysdate,to_date('" + ob3[2] + "', 'yyyy-mm-dd hh24:mi:ss'))");
				baseDao.deleteByCondition("HrorgEmployees", "om_emid=" + emid);
				insertHrorgEmp(Integer.parseInt(emid.toString()), Integer.parseInt(ob1[2].toString()));
			}
			List<Object> jo_orgids = baseDao.getFieldDatasByCondition("job", "jo_orgid",
					"jo_id in(select job_id from empsjobs where emp_id=" + emid + ")");
			for (Object jo_orgid : jo_orgids) {
				if (jo_orgid != null && !"".equals(jo_orgid.toString())) {
					insertHrorgEmp(Integer.parseInt(emid.toString()), Integer.parseInt(jo_orgid.toString()));
				}
			}
			Object masters = baseDao.getFieldDataByCondition("Master", "wm_concat(ma_user)", " ma_name<>'"
					+ SystemSession.getUser().getEm_master() + "'");
			if (masters != null) {
				baseDao.callProcedure("SYS_POST", new Object[] { "Turnposition!Post", SpObserver.getSp(), masters, String.valueOf(ap_id),
						SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
			}
		}
		// 记录操作;
		baseDao.logger.audit(caller, "tp_id", tp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { tp_id });
	}

	@Override
	public void resAuditTurnposition(int tp_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.handler("Turnposition", "resAudit", "before", new Object[] { tp_id });
		Object status = baseDao.getFieldDataByCondition("Turnposition", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("Turnposition", "tp_id=" + tp_id, "tp_status", "tp_statuscode", "tp_auditdate", "tp_auditor");
		// 记录操作
		baseDao.logger.resAudit(caller, "tp_id", tp_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { tp_id });
	}

	@Override
	public void submitTurnposition(int tp_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Turnposition", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { tp_id });
		int count = baseDao.getCount("select count(1) from Turnposition where tp_type " + "in ('turnposition','leave') and tp_id=" + tp_id);// 判断是否是离职或调岗
		boolean empTransfercheck = baseDao.isDBSetting(caller, "empTransfercheck");// Y/N
		if (baseDao.checkIf("Turnposition", "tp_id=" + tp_id + " and nvl(TP_NEEDTRANSFER,0)=-1") || (count > 0 && empTransfercheck)) {// 单据中选择需要交接或
																																		// 离职或调岗类型并且启用任务交接
			int count1 = baseDao.getCount("select count(1) from emptransferCheck where (ec_caller='"+caller+"' or ec_caller like '"+caller+"!%') and ec_keyvalue='" + tp_id + "'");// 检查是否录入任务交接单
			if (count1 == 0) {
				BaseUtil.showError("请先录入员工任务交接单");
			} else {
				Object ob = baseDao.getFieldDataByCondition("emptransferCheck", "WMSYS.wm_concat(ec_code) AS ec_code", "(ec_caller='"+caller+"' or ec_caller like '"+caller+"!%') and ec_keyvalue="
						+ tp_id + " and ec_statuscode<>'AUDITED'");
				if (ob != null && !"AUDITED".equals(ob.toString())) {
					BaseUtil.showError("要先审核此单据对应的异动任务交接单，单号：" + ob);
				}
			}
		}
		// 执行提交操作
		baseDao.submit("Turnposition", "tp_id=" + tp_id, "tp_status", "tp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "tp_id", tp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { tp_id });
	}

	@Override
	public void resSubmitTurnposition(int tp_id, String caller) {
		handlerService.beforeResSubmit(caller, new Object[] { tp_id });
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Turnposition", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("Turnposition", "tp_id=" + tp_id, "tp_status", "tp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "tp_id", tp_id);
		handlerService.afterResSubmit(caller, new Object[] { tp_id });
	}

	private void insertHrorgEmp(int em_id, int or_id) {
		int count = baseDao.getCountByCondition("HrorgEmployees", "om_emid=" + em_id + " and om_orid=" + or_id);
		if (count == 0) {
			baseDao.execute("insert into HrorgEmployees(om_emid,om_orid) values (" + em_id + "," + or_id + ") ");
			Object or_subof = baseDao.getFieldDataByCondition("hrorg", "or_subof", "or_id=" + or_id);
			if (or_subof != null && Integer.parseInt(or_subof.toString()) != 0) {
				insertHrorgEmp(em_id, Integer.parseInt(or_subof.toString()));
			}
		}
	}
}
