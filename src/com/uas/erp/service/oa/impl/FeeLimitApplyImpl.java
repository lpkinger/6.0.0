package com.uas.erp.service.oa.impl;

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
import com.uas.erp.service.oa.FeeLimitApplyService;

@Service
public class FeeLimitApplyImpl implements FeeLimitApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFeeLimitApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });

		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("fad_id", baseDao.getSeqId("FeeLimitApplydetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "FeeLimitApplydetail");
		String check = baseDao.executeWithCheck(gridSql, null, "select wm_concat(fad_detno) from  FeeLimitApplyDetail where fad_faid="
				+ store.get("fa_id") + "  group  by  fad_emcode,fad_type  having  count(fad_emcode) > 1");
		if (check != null && check.length() > 0) {
			BaseUtil.showError("明细行存在重复行");
		}
		// 保存FeeLimitApply
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FeeLimitApply", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.execute("update FeeLimitApplyDetail set fad_deptcode=(select dp_code from department where fad_deptname=dp_name)"
				+ " where fad_faid=" + store.get("fa_id") + " and nvl(fad_deptcode,' ')=' '");
		// 记录操作
		baseDao.logger.save(caller, "fa_id", store.get("fa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteFeeLimitApply(int fa_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("FeeLimitApply", "fa_statuscode", "fa_id=" + fa_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { fa_id });
		// 删除FeeLimitApply
		baseDao.deleteById("FeeLimitApply", "fa_id", fa_id);
		// 删除Contact
		baseDao.deleteById("FeeLimitApplydetail", "fad_faid", fa_id);
		// 记录操作
		baseDao.logger.delete(caller, "fa_id", fa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { fa_id });

	}

	@Override
	public void updateFeeLimitApplyById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("FeeLimitApply", "fa_statuscode", "fa_id=" + store.get("fa_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });

		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "FeeLimitApplydetail", "fad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("fad_id") == null || s.get("fad_id").equals("") || s.get("fad_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("FeeLimitApplydetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "FeeLimitApplydetail", new String[] { "fad_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		String check = baseDao.executeWithCheck(gridSql, null, "select wm_concat(fad_detno) from  FeeLimitApplyDetail where fad_faid="
				+ store.get("fa_id") + "  group  by  fad_emcode,fad_type  having  count(fad_emcode) > 1");
		if (check != null && check.length() > 0) {
			BaseUtil.showError("明细行存在重复行");
		}
		// 修改FeeLimitApply
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FeeLimitApply", "fa_id");
		baseDao.execute(formSql);
		baseDao.execute("update FeeLimitApplyDetail set fad_deptcode=(select dp_code from department where fad_deptname=dp_name)"
				+ " where fad_faid=" + store.get("fa_id") + " and nvl(fad_deptcode,' ')=' '");
		// 记录操作
		baseDao.logger.update(caller, "fa_id", store.get("fa_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });

	}

	@Override
	public void submitFeeLimitApply(int fa_id, String caller) {
		// 更新人员部门
		baseDao.execute("update feelimitapplydetail set (fad_deptcode,fad_deptname)=(select em_departmentcode,em_depart from employee where em_code= fad_emcode)"
				+ " where nvl(fad_emname,' ')<>' ' and nvl(fad_deptname,' ')=' ' and fad_faid=" + fa_id);
		baseDao.execute("update FeeLimitApplyDetail set fad_deptcode=(select dp_code from department where fad_deptname=dp_name)"
				+ " where fad_faid=" + fa_id + " and nvl(fad_deptcode,' ')=' '");
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FeeLimitApply", "fa_statuscode", "fa_id=" + fa_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { fa_id });
		// 执行提交操作
		baseDao.submit("FeeLimitApply", "fa_id=" + fa_id, "fa_status", "fa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "fa_id", fa_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { fa_id });
	}

	@Override
	public void resSubmitFeeLimitApply(int fa_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FeeLimitApply", "fa_statuscode", "fa_id=" + fa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { fa_id });
		// 执行反提交操作
		baseDao.resOperate("FeeLimitApply", "fa_id=" + fa_id, "fa_status", "fa_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "fa_id", fa_id);
		handlerService.afterResSubmit(caller, new Object[] { fa_id });
	}

	@Override
	@Transactional
	public void auditFeeLimitApply(int fa_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FeeLimitApply", "fa_statuscode", "fa_id=" + fa_id);
		Object fad_type = baseDao.getFieldDataByCondition("FeeLimitApplyDetail", "fad_type", "fad_faid=" + fa_id);
		StateAssert.auditOnlyCommited(status);
		Object monthday = baseDao.getFieldDataByCondition("FeeLimitApply", "fa_monthday", "fa_id=" + fa_id);
		Object[] datas = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "fk_controlway" }, "fk_name='" + fad_type + "'");

		if (baseDao.getCount("select count(1) from FeeLimit where fl_yearmonth=" + monthday) != 1) {
			BaseUtil.showError("请先结转或手工创建申请月份的额度数据!");
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { fa_id });
		// 执行审核操作
		baseDao.audit("FeeLimitApply", "fa_id=" + fa_id, "fa_status", "fa_statuscode", "fa_auditdate", "fa_auditman");
		Object fl_id = baseDao.getFieldDataByCondition("feelimit", "fl_id", "fl_yearmonth=" + monthday);
		// 插入不存在数据
		if (datas != null) {
			Object detno = baseDao.getFieldDataByCondition("feelimitdetail", "max(fld_detno)", "fld_flid=" + fl_id);
			String insertSql = "insert into FeeLimitdetail(fld_id,fld_flid,fld_detno,fld_departmentcode,fld_departmentname,fld_class,fld_amount)"
					+ " select FeeLimitdetail_seq.nextval,"
					+ fl_id
					+ ","
					+ detno
					+ "+rownum,fad_deptcode,fad_deptname,fad_type,0 from FeeLimitApplydetail,FeeKind where fad_faid="
					+ fa_id
					+ " and fk_name=fad_type and fk_controlway in ('DEP','部门') and nvl(fad_fee1,0)<>0 and (fad_deptcode,fad_type) not in (select fld_departmentcode,fld_class from FeeLimitdetail where nvl(fld_emname,' ')=' ' and fld_flid="
					+ fl_id + ")";
			baseDao.execute(insertSql);
			baseDao.execute("update FeeLimitdetail set fld_amount=nvl(fld_amount,0)+nvl((select fad_fee1 from FeeLimitApply,FeeLimitApplydetail,FeeKind where fa_id=fad_faid and "
					+ " fa_status='已审核' and nvl(fad_emname,' ')=' ' and fk_name=fad_type and fk_controlway in ('DEP','部门') and fa_id="
					+ fa_id
					+ " and fad_deptcode=fld_departmentcode and nvl(fad_type,' ')=nvl(fld_class,' ')),0) where nvl(fld_emname,' ')=' ' and fld_flid="
					+ fl_id);
			detno = baseDao.getFieldDataByCondition("feelimitdetail", "max(fld_detno)", "fld_flid=" + fl_id);
			insertSql = "insert into FeeLimitdetail(fld_id,fld_flid,fld_detno,fld_emcode,fld_emname,fld_emid,fld_class,fld_amount,fld_departmentcode,fld_departmentname)"
					+ " select FeeLimitdetail_seq.nextval,"
					+ fl_id
					+ ","
					+ detno
					+ "+rownum,fad_emcode,fad_emname,0,fad_type,0,fad_deptcode,fad_deptname from FeeLimitApplydetail,FeeKind where fad_faid="
					+ fa_id
					+ " and fk_name=fad_type and fk_controlway in ('EMP','个人') and nvl(fad_fee1,0)<>0 and fad_emcode||fad_type not in (select fld_emcode||fld_class from FeeLimitdetail where fld_flid="
					+ fl_id + " ) ";
			baseDao.execute(insertSql);
			// 更新累计额度
			baseDao.execute("update FeeLimitdetail set fld_amount=nvl(fld_amount,0)+nvl((select fad_fee1 from FeeLimitApply,FeeLimitApplydetail,FeeKind where fa_id=fad_faid and "
					+ " fa_status='已审核' and fk_name=fad_type and fk_controlway in ('EMP','个人') and fa_id="
					+ fa_id
					+ " and fad_emcode=fld_emcode and nvl(fad_type,' ')=nvl(fld_class,' ')),0) where fld_flid=" + fl_id);
		} else {
			BaseUtil.showError("请先去费用类型界面设置基本信息");
		}
		// 记录操作
		baseDao.logger.audit(caller, "fa_id", fa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { fa_id });
	}

	@Override
	@Transactional
	public void resAuditFeeLimitApply(int fa_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.handler("FeeLimitApply", "resAudit", "before", new Object[] { fa_id });
		Object fad_type = baseDao.getFieldDataByCondition("FeeLimitApplyDetail", "fad_type", "fad_faid=" + fa_id);
		Object[] datas = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "nvl(fk_controlway,0)" }, "fk_name='" + fad_type + "'");
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FeeLimitApply", "fa_statuscode", "fa_id=" + fa_id);
		StateAssert.resAuditOnlyAudit(status);
		Object monthday = baseDao.getFieldDataByCondition("FeeLimitApply", "fa_monthday", "fa_id=" + fa_id);
		Object fl_id = baseDao.getFieldDataByCondition("feelimit", "fl_id", "fl_yearmonth=" + monthday);
		if (datas != null) {
			baseDao.execute("update FeeLimitdetail set fld_amount=nvl(fld_amount,0)-nvl((select fad_fee1 from FeeLimitApply,FeeLimitApplydetail,FeeKind where fa_id=fad_faid and "
					+ " fa_status='已审核' and fk_name=fad_type and fk_controlway in ('DEP','部门') and nvl(fad_emname,' ')=' ' and fa_id="
					+ fa_id
					+ " and fad_deptcode=fld_departmentcode and nvl(fad_type,' ')=nvl(fld_class,' ') and nvl(fld_emname,' ')=' '),0) where fld_flid="
					+ fl_id);
			baseDao.execute("update FeeLimitdetail set fld_amount=nvl(fld_amount,0)-nvl((select fad_fee1 from FeeLimitApply,FeeLimitApplydetail,FeeKind where fa_id=fad_faid and "
					+ " fa_status='已审核' and fk_name=fad_type and fk_controlway in ('EMP','个人') and fa_id="
					+ fa_id
					+ " and fad_emcode=fld_emcode and nvl(fad_type,' ')=nvl(fld_class,' ')),0) where fld_flid=" + fl_id);
		} else {
			BaseUtil.showError("请先去费用类型界面设置基本信息");
		}
		// 执行反审核操作
		baseDao.resAudit("FeeLimitApply", "fa_id=" + fa_id, "fa_status", "fa_statuscode", "fa_auditdate", "fa_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "fa_id", fa_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { fa_id });

	}

}
