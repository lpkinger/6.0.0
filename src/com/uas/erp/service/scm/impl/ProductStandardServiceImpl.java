package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductStandardService;
@Service
public class ProductStandardServiceImpl implements ProductStandardService {
	
	@Autowired
	BaseDao baseDao;
	@Autowired
	HandlerService handlerService;
	
	@Override
	public void saveProductStandard(String formStore, String param,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PRODUCTSTANDARDRATE", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		List<String> sqls = new ArrayList<String>();
		for(Map<Object, Object> s:gstore){
			if(s.get("pd_id") == null || s.get("pd_id").equals("") || s.get("pd_id").equals("0")){//新添加的数据，id不存在
				int pd_id = baseDao.getSeqId("PRODUCTSTANDARDRATEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PRODUCTSTANDARDRATEDETAIL", new String[]{"pd_id"}, new Object[]{pd_id});
				sqls.add(sql);
			}
		}
		baseDao.execute(sqls);
		//记录操作
		baseDao.logger.save("ProductCircleRate", "psr_id", store.get("psr_id"));
	
	}

	@Override
	public void deleteProductStandard(int id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PRODUCTSTANDARDRATE", "psr_statuscode", "psr_id=" + id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除Application
		baseDao.deleteById("PRODUCTSTANDARDRATE", "psr_id", id);
		// 删除ApplicationDetail
		baseDao.deleteById("PRODUCTSTANDARDRATEDETAIL", "pd_psrid", id);
		// 记录操作
		baseDao.logger.delete(caller, "psr_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	
	}

	@Override
	public void updateProductStandardById(String formStore, String param,
			String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PRODUCTSTANDARDRATE", "psr_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(param, "PRODUCTSTANDARDRATEDETAIL", "pd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pd_id") == null || s.get("pd_id").equals("") || s.get("pd_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODUCTSTANDARDRATEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PRODUCTSTANDARDRATEDETAIL", new String[]{"pd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "psr_id", store.get("psr_id"));
	
	}

	@Override
	public void submitProductStandard(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("PRODUCTSTANDARDRATE", "psr_statuscode", "psr_id=" + id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.submit("PRODUCTSTANDARDRATE", "psr_id=" + id, "psr_status", "psr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "psr_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	
	}

	@Override
	public void auditProductStandard(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("PRODUCTSTANDARDRATE", "psr_statuscode", "psr_id=" + id);
		StateAssert.auditOnlyCommited(status);
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		baseDao.audit("PRODUCTSTANDARDRATE", "psr_id=" + id, "psr_status", "psr_statuscode", "psr_auditdate", "psr_auditman");
		baseDao.execute("update product set PR_BZKCZZL = (select nvl(pd_bzkczzl,0) from PRODUCTSTANDARDRATEDETAIL where nvl(pd_brand,' ')=pr_brand and pd_psrid="+id+" and nvl(pd_bzkczzl,0)<>0) where nvl(pr_brand,' ') in (select pd_brand from PRODUCTSTANDARDRATEDETAIL where pd_psrid="+id+" and nvl(pd_bzkczzl,0)<>0)");
		// 记录操作
		baseDao.logger.audit(caller, "psr_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void resAuditProductStandard(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("PRODUCTSTANDARDRATE", "psr_statuscode", "psr_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.handler(caller, "resAudit", "before", new Object[] { id });
		baseDao.resOperate("PRODUCTSTANDARDRATE", "psr_id=" + id, "psr_status", "psr_statuscode");
		baseDao.logger.resAudit(caller, "psr_id", id);
		handlerService.handler(caller, "resAudit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitProductStandard(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("PRODUCTSTANDARDRATE", "psr_statuscode", "psr_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "resCommit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.resOperate("PRODUCTSTANDARDRATE", "psr_id=" + id, "psr_status", "psr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "psr_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "resCommit", "after", new Object[] { id });
	
	}

}
