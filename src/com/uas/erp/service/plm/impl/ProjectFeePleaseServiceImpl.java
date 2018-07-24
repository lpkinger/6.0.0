package com.uas.erp.service.plm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ProjectFeePleaseDao;
import com.uas.erp.service.plm.ProjectFeePleaseService;

@Service
public class ProjectFeePleaseServiceImpl implements ProjectFeePleaseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ProjectFeePleaseDao projectFeePleaseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProjectFeePlease(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gridstore });
		// 执行保存操作
		float count = 0;
		Object[] pfd_id = new Object[gridstore.size()];
		for (int i = 0; i < gridstore.size(); i++) {
			pfd_id[i] = baseDao.getSeqId("PROJECTFEEPLEASEDETAIL_SEQ");
			count += Float.parseFloat(gridstore.get(i).get("pfd_amount").toString());
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "ProjectFeePleaseDetail", "pfd_id", pfd_id);
		baseDao.execute(gridSql);
		store.put("pf_amount", count);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProjectFeePlease", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "pf_id", store.get("pf_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gridstore });
	}

	@Override
	@Transactional
	public void updateProjectFeePlease(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(param);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gridstore });
		// 修改ProjectFeePlease
		float count = 0;
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridstore, "ProjectFeePleaseDetail", "pfd_id");
		for (Map<Object, Object> s : gridstore) {
			if (s.get("pfd_id") == null || s.get("pfd_id").equals("") || s.get("pfd_id").equals("0")
					|| Integer.parseInt(s.get("pfd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ProjectFeePleaseDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProjectFeePleaseDetail", new String[] { "pfd_id" }, new Object[] { id });
				gridSql.add(sql);
				count += NumberUtil.subFloat(Float.parseFloat(s.get("pfd_amount").toString()), 2);
			}
		}
		baseDao.execute(gridSql);
		store.put("pf_amount", NumberUtil.formatDouble(Double.parseDouble(store.get("pf_amount").toString()), 2) + count);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProjectFeePlease", "");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pf_id", store.get("pf_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gridstore });
	}

	@Override
	public void deleteProjectFeePlease(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		baseDao.deleteById("ProjectFeePlease", "pf_id", id);
		baseDao.deleteById("ProjectFeePleasedetail", "pfd_pfid", id);
		// 记录操作
		baseDao.logger.delete(caller, "pf_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	public void auditProjectFeePlease(int id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		// 执行审核操作
		baseDao.audit("ProjectFeePlease", "pf_id=" + id, "pf_status", "pf_statuscode", "pf_auditdate", "pf_auditman");
		turnProjectFee(id, caller);
		// 记录操作
		baseDao.logger.audit(caller, "pf_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void resAuditProjectFeePlease(int id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProjectFeePlease", "pf_statuscode", "pf_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resOperate("ProjectFeePlease", "pf_id=" + id, "pf_status", "pf_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pf_id", id);
	}

	@Override
	public void submitProjectFeePlease(int id, String caller) {
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.submit("ProjectFeePlease", "pf_id=" + id, "pf_status", "pf_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pf_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitProjectFeePlease(int id, String caller) {
		// 执行反提交操作
		baseDao.resOperate("ProjectFeePlease", "pf_id=" + id, "pf_status", "pf_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pf_id", id);
	}

	/**
	 * 申请单转费用报销单
	 * 
	 * @author madan
	 */
	@Override
	public int turnProjectFee(int pf_id, String caller) {
		int pcid = 0;
		// 判断该申请单是否已经转入过费用报销单
		Object[] code = baseDao.getFieldsDataByCondition("ProjectFeePlease", "pf_code", "pf_id=" + pf_id);
		code = baseDao.getFieldsDataByCondition("ProjectFeeClaim", new String[] { "pc_code", "pc_id" }, "pc_pleasecode='" + code[0] + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("pml.cost.projectfeeplease.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/plm/cost/ProjectFeeClaim.jsp?formCondition=pc_idIS" + code[1]
					+ "&gridCondition=pcd_pcidIS" + code[1] + "')\">" + code[0] + "</a>&nbsp;");
		} else {
			// 转费用报销单
			pcid = projectFeePleaseDao.turnProjectFeeClaim(pf_id);
			baseDao.logger.turn("转费用报销单", caller, "pf_id", pf_id);
		}
		return pcid;
	}
}
