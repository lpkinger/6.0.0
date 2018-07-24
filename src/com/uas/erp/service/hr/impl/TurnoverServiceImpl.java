package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.AccountCenterService;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.hr.TurnoverService;

@Service
public class TurnoverServiceImpl implements TurnoverService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private AccountCenterService accountCenterService;

	@Override
	public void saveTurnover(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Turnover", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存TurnoverDetail
		Object[] td_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			td_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				td_id[i] = baseDao.getSeqId("TurnoverDETAIL_SEQ");
			}
		} else {
			td_id[0] = baseDao.getSeqId("TurnoverDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "TurnoverDetail", "td_id", td_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "to_id", store.get("to_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updateTurnoverById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Turnover", "to_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "TurnoverDetail", "td_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("td_id") == null || s.get("td_id").equals("") || s.get("td_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("TurnoverDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "TurnoverDetail", new String[] { "td_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "to_id", store.get("to_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteTurnover(int to_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, to_id);
		// 删除purchase
		baseDao.deleteById("Turnover", "to_id", to_id);
		// 删除purchaseDetail
		baseDao.deleteById("Turnoverdetail", "td_tpid", to_id);
		// 记录操作
		baseDao.logger.delete(caller, "to_id", to_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, to_id);
	}

	@Override
	public void auditTurnover(int to_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Turnover", "to_statuscode", "to_id=" + to_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, to_id);
		// 执行审核操作
		baseDao.audit("Turnover", "to_id=" + to_id, "to_status", "to_statuscode", "to_auditdate", "to_auditor");
		boolean needConfirm = baseDao.isDBSetting(caller, "needConfirm");// Y/N
		if (!needConfirm) {
			Object to_applymancode = baseDao.getFieldDataByCondition("Turnover", "to_applymancode", "to_id=" + to_id);
			baseDao.updateByCondition("employee", "em_class='离职',em_leavedate=sysdate", "em_code ='" + to_applymancode.toString() + "'");// 修改员工为离职
			baseDao.updateByCondition("Turnover", "to_confirm=1", "to_id =" + to_id);
			Master master = SystemSession.getUser().getCurrentMaster();
			String ma_accesssecret = master.getMa_accesssecret();
			Long en_uu = master.getMa_uu();
			Object em_uu = baseDao.getFieldDataByCondition("employee", "em_uu", "em_code='" + to_applymancode.toString() + "'");
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
						Employee employeeNew = employeeService.getByCondition("em_code = '" + to_applymancode.toString() + "'","Turnover");
						if(employeeNew!=null){
							accountCenterService.unbind(employeeNew, master);
						}
						Response response = HttpUtil.sendDeleteRequest(b2burl + "/erp/account/user/" + em_uu + "?access_id=" + en_uu + "",
								null, true, ma_accesssecret);
						if (response.getStatusCode() == 200) {
							baseDao.execute("update employee set em_uu=null,em_imid=null,em_b2benable=0 where em_code='"
									+ to_applymancode.toString() + "'");
						}
					} else {
						baseDao.execute("update employee set em_uu=null,em_imid=null,em_b2benable=0 where em_code='"
								+ to_applymancode.toString() + "'");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			/*
			 * Object masters = baseDao.getFieldDataByCondition("Master",
			 * "wm_concat(ma_user)", " ma_name<>'" +
			 * SystemSession.getUser().getEm_master()+"'"); if(masters!=null){
			 * String error=baseDao.callProcedure("SYS_POST", new Object[] {
			 * "Turnover!Post", SpObserver.getSp(), masters,
			 * to_applymancode.toString(), SystemSession.getUser().getEm_name(),
			 * SystemSession.getUser().getEm_id() }); if(error!=null)
			 * BaseUtil.showError(error); }
			 */
		}
		// 记录操作
		baseDao.logger.audit(caller, "to_id", to_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, to_id);
	}

	@Override
	public void resAuditTurnover(int to_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Turnover", "to_statuscode", "to_id=" + to_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("Turnover", "to_id=" + to_id, "to_status", "to_statuscode", "to_auditdate", "to_auditor");
		// 记录操作
		baseDao.logger.resAudit(caller, "to_id", to_id);
	}

	@Override
	public void submitTurnover(int to_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Turnover", "to_statuscode", "to_id=" + to_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, to_id);
		if (baseDao.checkIf("Turnover", "to_id=" + to_id + " and nvl(to_needtransfer,0)=-1")) {// 单据中选择需要交接或
			// 离职或调岗类型并且启用任务交接
			int count1 = baseDao.getCount("select count(1) from emptransferCheck where (ec_caller='"+caller+"' or ec_caller like '"+caller+"!%') and ec_keyvalue='" + to_id + "'");// 检查是否录入任务交接单
			if (count1 == 0) {
				BaseUtil.showError("请先录入员工任务交接单");
			} else {
				Object ob = baseDao.getFieldDataByCondition("emptransferCheck", "WMSYS.wm_concat(ec_code) AS ec_code", "(ec_caller='"+caller+"' or ec_caller like '"+caller+"!%') and ec_keyvalue="
				+ to_id + " and ec_statuscode<>'AUDITED'");
				if (ob != null && !"AUDITED".equals(ob.toString())) {
					BaseUtil.showError("要先审核此单据对应的异动任务交接单，单号：" + ob);
				}
			}
		}
		// 执行提交操作
		baseDao.submit("Turnover", "to_id=" + to_id, "to_status", "to_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "to_id", to_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, to_id);
	}

	@Override
	public void resSubmitTurnover(int to_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Turnover", "to_statuscode", "to_id=" + to_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, to_id);
		// 执行反提交操作
		baseDao.resOperate("Turnover", "to_id=" + to_id, "to_status", "to_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "to_id", to_id);
		handlerService.afterResSubmit(caller, to_id);
	}

	@Override
	public String confirmTurnover(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(maps, "to_id");
		baseDao.execute("update employee set em_class='离职' ,em_leavedate=(select to_leavedate from turnover where to_applymancode=em_code and to_id in("
				+ ids + ")) where em_code in (select to_applymancode from turnover where to_id in (" + ids + "))");
		baseDao.execute("update turnover set to_confirm=1 where to_id in (" + ids + ")");
		Object emcodes = baseDao.getFieldDataByCondition("Turnover", "wm_concat(''''||to_applymancode||'''')", "to_id in (" + ids + ")");
		List<Object> obs = baseDao.getFieldDatasByCondition("Master", "ma_user", "ma_name<>'" + SystemSession.getUser().getEm_master()
				+ "'");
		for (Object ma_user : obs) {
			baseDao.execute("update "
					+ ma_user
					+ ".employee a set a.em_class='离职' ,a.em_leavedate=(select b.em_leavedate from employee b where b.em_code=a.em_code) where a.em_code in ( "
					+ emcodes + ")");
		}
		Master master = SystemSession.getUser().getCurrentMaster();
		String ma_accesssecret = master.getMa_accesssecret();
		Long en_uu = master.getMa_uu();
		String emUus = CollectionUtil.pluckSqlString(maps, "em_uu");
		emUus = emUus.replaceAll("'", "");
		if (!"".equals(emUus)) {
			String b2burl = null;
			if (master.getMa_b2bwebsite() == null || master.getMa_b2bwebsite().equals("null")) {
				b2burl = "http://uas.ubtob.com";
			} else {
				b2burl = master.getMa_b2bwebsite();
			}
			try {
				if (en_uu != null && en_uu > 0) {
					// 更新B2B和帐户中心
					List<Employee> employeeList = employeeService.getEmployeesByCondition("em_code in (" + emcodes + ")");
					for( Employee employeeNew : employeeList) {
						accountCenterService.unbind(employeeNew, master);
					}
					Response response = HttpUtil.sendDeleteRequest(b2burl + "/erp/account/user/" + emUus + "?access_id=" + en_uu + "",
							null, true, ma_accesssecret);
					if (response.getStatusCode() == 200) {
						baseDao.execute("update employee set em_uu=null,em_imid=null,em_b2benable=0 where em_code in (" + emcodes + ")");
					}
				} else {
					baseDao.execute("update employee set em_uu=null,em_imid=null,em_b2benable=0 where em_code in (" + emcodes + ")");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "处理成功";
	}
}
