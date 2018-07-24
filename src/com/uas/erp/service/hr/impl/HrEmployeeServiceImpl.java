package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.AccountCenterService;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.hr.HrEmployeeService;
import com.uas.erp.service.scm.BatchDealService;

@Service("hrEmployeeService")
public class HrEmployeeServiceImpl implements HrEmployeeService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private BatchDealService batchDealService;

	@Autowired
	private AccountCenterService accountCenterService;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private EmployeeService employeeService;

	@Override
	public void saveEmployee(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Employee", "em_code='" + store.get("em_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存Employee
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Employee", new String[] { "em_enid" }, new Object[] { SystemSession
				.getUser().getEm_enid() });
		baseDao.execute(formSql);
		// 保存HrJob
		Object[] jo_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			jo_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				jo_id[i] = baseDao.getSeqId("HRJOB_SEQ");
			}
		} else {
			jo_id[0] = baseDao.getSeqId("HRJOB_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "HrJob", "jo_id", jo_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "em_id", store.get("em_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteEmployee(int em_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Employee", "em_statuscode", "em_id=" + em_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { em_id });
		// 删除Employee
		baseDao.deleteById("Employee", "em_id", em_id);
		// 删除HrJob
		baseDao.deleteById("HrJob", "jo_emid", em_id);
		// 记录操作
		baseDao.logger.delete(caller, "em_id", em_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { em_id });
	}

	@Override
	public void updateEmployeeById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改Employee
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Employee", "em_id");
		baseDao.execute(formSql);
		// 修改HrJob
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "HrJob", "jo_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("jo_id") == null || s.get("jo_id").equals("") || s.get("jo_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("HRJOB_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "HrJob", new String[] { "jo_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "em_id", store.get("em_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void printEmployee(int em_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("Employee", "em_statuscode", "em_id=" + em_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { em_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "em_id", em_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { em_id });
	}

	@Override
	public void auditEmployee(int em_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Employee employee = employeeService.getEmployeeById(em_id);
		StateAssert.auditOnlyCommited(employee.getEm_statuscode());
		/**
		 * 问题反馈单号：2016120871
		 * UAS标准版:
		 * 		人员资料:新增一个参数设置->人员资料员工名称不允许重复    默认启用.
		 * @author wsy
		 */
		boolean bool = baseDao.isDBSetting(caller,"EM_NAMERepeat");
		if(bool==true){
			Object em_name = baseDao.getFieldDataByCondition("Employee", "em_name", "em_id="+em_id);
			List<Object> codes = baseDao.getFieldDatasByCondition("Employee", "em_code", "em_name='"+em_name+"' and em_id<>"+em_id);
			if(codes.size()>0){
				BaseUtil.showError("员工姓名重复！员工姓名:'"+em_name+"'已在员工编号:'"+codes.get(0)+"'中出现，请修改！");
			}
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { em_id });
		// 执行审核操作
		baseDao.audit("Employee", "em_id=" + em_id, "em_status", "em_statuscode", "em_auditdate", "em_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "em_id", em_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { em_id });

		String error = employeeService.postToAccountCenter(employee);
		if (!StringUtils.isEmpty(error)) {
			BaseUtil.showErrorOnSuccess("账户无法同步到云，原因:" + error);
		}
	}

	@Override
	public void resAuditEmployee(int em_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Employee", "em_statuscode", "em_id=" + em_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("Employee", "em_id=" + em_id, "em_status", "em_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "em_id", em_id);
	}

	@Override
	public void submitEmployee(int em_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Employee", "em_statuscode", "em_id=" + em_id);
		StateAssert.submitOnlyEntering(status);
		/**
		 * 问题反馈单号：2016120871
		 * UAS标准版:
		 * 		人员资料:新增一个参数设置->人员资料员工名称不允许重复    默认启用.
		 * @author wsy
		 */
		boolean bool = baseDao.isDBSetting(caller,"EM_NAMERepeat");
		if(bool==true){
			Object em_name = baseDao.getFieldDataByCondition("Employee", "em_name", "em_id="+em_id);
			List<Object> codes = baseDao.getFieldDatasByCondition("Employee", "em_code", "em_name='"+em_name+"' and em_id<>"+em_id);
			//List<Object> codes = baseDao.getFieldDatasByCondition("Employee", "em_code", "em_name=(select em_name from employee where em_id="+em_id+") and em_id<>"+em_id);
			if(codes.size()>0){
				BaseUtil.showError("员工姓名重复！员工姓名:'"+em_name+"'已在员工编号:'"+codes.get(0)+"'中出现，请修改！");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { em_id });
		// 执行提交操作
		baseDao.submit("Employee", "em_id=" + em_id, "em_status", "em_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "em_id", em_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { em_id });
	}

	@Override
	public void resSubmitEmployee(int em_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Employee", "em_statuscode", "em_id=" + em_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("Employee", "em_id=" + em_id, "em_status", "em_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "em_id", em_id);
		handlerService.afterResSubmit(caller, new Object[] { em_id });
	}

}
