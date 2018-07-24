package com.uas.erp.service.fa.impl;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.SecondCategoryBaseService;

@Service("SecondCategoryBaseService")
public class SecondCategoryBaseServiceImpl implements SecondCategoryBaseService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSecondCategoryBase(String formStore, String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Category", "ca_code='" + store.get("ca_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist", language));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("SecondCategory!Base", "save", "before", new Object[] { store, language });
		// 科目描述
		Object pcode = store.get("ca_pcode");
		if (pcode != null && pcode.toString().trim().length() > 0) {
			Object pDesc = baseDao.getFieldDataByCondition("Category", "nvl(ca_description, ca_name)", "ca_code='"
					+ pcode + "'");
			if (pDesc != null) {
				store.put("ca_description", pDesc + ":" + store.get("ca_name"));
			} else {
				store.put("ca_description", store.get("ca_name"));
			}
		} else {
			store.put("ca_description", store.get("ca_name"));
		}
		// 不允许和父级科目名称一样
		boolean err = baseDao.checkIf("Category", "ca_code='" + pcode + "' and ca_name='" + store.get("ca_name") + "'");
		if (err)
			BaseUtil.showError("不允许和父级科目名称一样!");
		// 保存
		String formSql = SqlUtil.getInsertSqlByMap(store, "Category");
		baseDao.updateByCondition("Category A",
				"ca_subof=nvl((select ca_id from Category B where B.ca_code=A.ca_pcode),0)",
				"ca_id=" + store.get("ca_id"));
		baseDao.updateByCondition("Category", "ca_isleaf=0", "ca_id=" + store.get("ca_id")
				+ " and ca_id in (select nvl(ca_subof,0) from Category)");
		baseDao.updateByCondition("Category", "ca_isleaf=1", "ca_id=" + store.get("ca_id")
				+ " and ca_id not in (select nvl(ca_subof,0) from Category)");
		baseDao.updateByCondition("Category A",
				"ca_level=nvl((select nvl(ca_level,1)+1 from Category B where B.ca_code=A.ca_pcode),1)", "ca_id="
						+ store.get("ca_id"));
		baseDao.execute("update Category set ca_typename=case when ca_type=0 then '借' when ca_type=1 then '贷' when ca_type=2 then '借或贷' end where ca_id="
				+ store.get("ca_id"));
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.save", language),
					BaseUtil.getLocalMessage("msg.saveSuccess", language), "SecondCategory!Base|ca_id=" + store.get("ca_id")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.handler("SecondCategory!Base", "save", "after", new Object[] { store, language });
	}

	@Override
	public void deleteSecondCategoryBase(int ca_id, String language, Employee employee) {
		// 已做凭证的科目无法删除
		boolean bool = baseDao.checkByCondition("VoucherDetail", "vd_catecode = (select ca_code from Category "
				+ "where ca_id=" + ca_id + ")");
		if (!bool) {
			BaseUtil.showError("该科目已使用，无法删除!");
		}
		// 执行删除前的其它逻辑
		handlerService.handler("SecondCategory!Base", "delete", "before", new Object[] { ca_id, language, employee });
		// 删除
		baseDao.deleteById("Category", "ca_id", ca_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.delete", language),
				BaseUtil.getLocalMessage("msg.deleteSuccess", language), "SecondCategory!Base|ca_id=" + ca_id));
		isLeaf();
		// 执行删除后的其它逻辑
		handlerService.handler("SecondCategory!Base", "delete", "after", new Object[] { ca_id, language, employee });
	}

	@Override
	public void updateSecondCategoryBaseById(String formStore, String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.handler("SecondCategory!Base", "save", "before", new Object[] { store, language });
		// 科目描述
		Object pcode = store.get("ca_pcode");
		if (pcode != null && pcode.toString().trim().length() > 0) {
			Object pDesc = baseDao.getFieldDataByCondition("Category", "nvl(ca_description, ca_name)", "ca_code='"
					+ pcode + "'");
			if (pDesc != null) {
				store.put("ca_description", pDesc + ":" + store.get("ca_name"));
			} else {
				store.put("ca_description", store.get("ca_name"));
			}
		} else {
			store.put("ca_description", store.get("ca_name"));
		}
		// 不允许和父级科目名称一样
		boolean err = baseDao.checkIf("Category", "ca_code='" + pcode + "' and ca_name='" + store.get("ca_name") + "'");
		if (err)
			BaseUtil.showError("不允许和父级科目名称一样!");
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Category", "ca_id");
		baseDao.execute(formSql);
		baseDao.updateByCondition("Category A",
				"ca_subof=nvl((select ca_id from Category B where B.ca_code=A.ca_pcode),0)",
				"ca_id=" + store.get("ca_id"));
		baseDao.updateByCondition("Category", "ca_isleaf=0", "ca_id=" + store.get("ca_id")
				+ " and ca_id in (select nvl(ca_subof,0) from Category)");
		baseDao.updateByCondition("Category", "ca_isleaf=1", "ca_id=" + store.get("ca_id")
				+ " and ca_id not in (select nvl(ca_subof,0) from Category)");
		baseDao.updateByCondition("Category A",
				"ca_level=nvl((select nvl(ca_level,1)+1 from Category B where B.ca_code=A.ca_pcode),1)", "ca_id="
						+ store.get("ca_id"));
		baseDao.execute("update Category set ca_typename=case when ca_type=0 then '借' when ca_type=1 then '贷' when ca_type=2 then '借或贷' end where ca_id="
				+ store.get("ca_id"));
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.update", language),
				BaseUtil.getLocalMessage("msg.updateSuccess", language), "SecondCategory!Base|ca_id=" + store.get("ca_id")));
		// 执行修改后的其它逻辑
		handlerService.handler("SecondCategory!Base", "save", "after", new Object[] { store, language });
	}

	@Override
	public void auditSecondCategory(int ca_id, String language, Employee employee) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Category", "ca_statuscode", "ca_id=" + ca_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited", language));
		}
		// 执行审核前的其它逻辑
		handlerService.handler("SecondCategory!Base", "audit", "before", new Object[] { ca_id, language, employee });
		// 执行提交操作
		baseDao.updateByCondition("Category",
				"ca_statuscode='AUDITED',ca_status='" + BaseUtil.getLocalMessage("AUDITED", language) + "'", "ca_id="
						+ ca_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.audit", language),
				BaseUtil.getLocalMessage("msg.auditSuccess", language), "SecondCategory!Base|ca_id=" + ca_id));
		// 执行审核后的其它逻辑
		handlerService.handler("SecondCategory!Base", "audit", "after", new Object[] { ca_id, language, employee });
	}

	@Override
	public void submitSecondCategory(int ca_id, String language, Employee employee) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Category", "ca_statuscode", "ca_id=" + ca_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering", language));
		}
		// 执行提交前的其它逻辑
		handlerService.handler("SecondCategory!Base", "commit", "before", new Object[] { ca_id, language, employee });
		// 执行提交操作
		baseDao.updateByCondition("Category",
				"ca_statuscode='COMMITED',ca_status='" + BaseUtil.getLocalMessage("COMMITED", language) + "'", "ca_id="
						+ ca_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.submit", language),
				BaseUtil.getLocalMessage("msg.submitSuccess", language), "SecondCategory!Base|ca_id=" + ca_id));
		isLeaf();
		// 执行提交后的其它逻辑
		handlerService.handler("SecondCategory!Base", "commit", "after", new Object[] { ca_id, language, employee });
	}

	@Override
	public void resSubmitSecondCategory(int ca_id, String language, Employee employee) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Category", "ca_statuscode", "ca_id=" + ca_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited", language));
		}
		// 执行反提交前的其它逻辑
		handlerService.handler("SecondCategory!Base", "resCommit", "before", new Object[] { ca_id, language, employee });
		// 执行反提交操作
		baseDao.updateByCondition("Category",
				"ca_statuscode='ENTERING',ca_status='" + BaseUtil.getLocalMessage("ENTERING", language) + "'", "ca_id="
						+ ca_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resSubmit", language),
				BaseUtil.getLocalMessage("msg.resSubmitSuccess", language), "SecondCategory!Base|ca_id=" + ca_id));
		// 执行反提交后的其它逻辑
		handlerService.handler("SecondCategory!Base", "resCommit", "after", new Object[] { ca_id, language, employee });
	}

	private void isLeaf() {
		baseDao.updateByCondition("Category", "ca_isleaf=0", "ca_id in(select ca_subof from Category)");
		baseDao.updateByCondition("Category", "ca_isleaf=1", "ca_id not in(select ca_subof from Category)");
	}
}
