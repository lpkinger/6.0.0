package com.uas.erp.service.plm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.ProjectColorService;

@Service
public class ProjectColorServiceImpl implements ProjectColorService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProjectColor(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProjectColor", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "pc_id", store.get("pc_id"));
	}

	@Override
	public void updateProjectColor(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProjectColor", "pc_id");
		baseDao.execute(formSql);
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
	}

	@Override
	public void deleteProjectColor(int id, String caller) {
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除ProjectColor
		baseDao.deleteById("ProjectColor", "pc_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "pc_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	public void auditProjectColor(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int id = Integer.parseInt(store.get("pc_id").toString());
		Object status = baseDao.getFieldDataByCondition("ProjectColor", "pc_statuscode", "pc_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		// List
		String str = " insert into colordetail cd_id,cd_prjid,cd_color,cd_type values(?,?,?,?)";
		for (int i = 1; i < 11; i++) {
			String color = store.get("pc_" + i + "color").toString();
			if (!store.get("pc_" + i + "color").equals("")) {
				int seq = baseDao.getSeqId("COLORDETAIL_SEQ");
				baseDao.execute(str, new Object[] { seq, store.get("pc_prjid"), color, store.get("pc_" + i + "type") });
			}
		}
		baseDao.audit("ProjectColor", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "pc_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void submitProjectColor(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectColor", "pc_statuscode", "pc_id=" + id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit(caller, id);
		// 执行反审核操作
		baseDao.submit("ProjectColor", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", id);
		handlerService.afterSubmit(caller, id);
	}

	@Override
	public void resSubmitProjectColor(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectColor", "pc_statuscode", "pc_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("ProjectColor", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", id);
	}

	@Override
	public void resAuditProjectColor(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectColor", "pc_statuscode", "pc_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("ProjectColor", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pc_id", id);
	}
}
