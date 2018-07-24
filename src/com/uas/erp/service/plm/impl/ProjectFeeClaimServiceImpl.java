package com.uas.erp.service.plm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.ProjectFeeClaimService;

@Service
public class ProjectFeeClaimServiceImpl implements ProjectFeeClaimService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProjectFeeClaim(String formStore, String gridStore, String caller) {
		Map<Object, Object> formstore = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { formStore, gridstore });
		// 执行保存操作
		float count = 0;
		Object[] pcd_id = new Object[gridstore.size()];
		for (int i = 0; i < gridstore.size(); i++) {
			pcd_id[i] = baseDao.getSeqId("PROJECTFEECLAIMDETAIL_SEQ");
			count += Float.parseFloat(gridstore.get(i).get("pcd_claimamount").toString());
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "PROJECTFEECLAIMDETAIL", "pcd_id", pcd_id);
		baseDao.execute(gridSql);
		formstore.put("pc_claimamount", count);
		String formSql = SqlUtil.getInsertSqlByFormStore(formstore, "ProjectFeeClaim", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "pc_id", formstore.get("pc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { formStore, gridstore });
	}

	@Override
	public void updateProjectFeeClaim(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(param);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gridstore });
		// 修改ProjectFeeClaim
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridstore, "PROJECTFEECLAIMDETAIL", "pcd_id");
		for (Map<Object, Object> s : gridstore) {
			if (s.get("pcd_id") == null || s.get("pcd_id").equals("") || s.get("pcd_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PROJECTFEECLAIMDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PROJECTFEECLAIMDETAIL", new String[] { "pcd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProjectFeeClaim", "pc_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gridstore });
	}

	@Override
	public void deleteProjectFeeClaim(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除
		baseDao.deleteById("ProjectFeeClaim", "pc_id", id);
		baseDao.deleteById("ProjectFeeClaimdetail", "pcd_pcid", id);
		// 记录操作
		baseDao.logger.delete(caller, "pc_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	public void auditProjectFeeClaim(int id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		// 执行审核操作
		baseDao.audit("ProjectFeeClaim", "pc_id=" + id, "pc_status", "pc_statuscode", "pc_auditdate", "pc_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "pc_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void resAuditProjectFeeClaim(int id, String caller) {
		// 执行反审核操作
		baseDao.resOperate("ProjectFeeClaim", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pc_id", id);
	}

	@Override
	public void submitProjectFeeClaim(int id, String caller) {
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.submit("ProjectFeeClaim", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitProjectFeeClaim(int id, String caller) {
		// 执行反提交操作
		baseDao.resOperate("ProjectFeeClaim", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", id);
	}
}
