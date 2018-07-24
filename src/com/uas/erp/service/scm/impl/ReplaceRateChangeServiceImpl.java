package com.uas.erp.service.scm.impl;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ReplaceRateChangeService;

@Service("replaceRateChangeService")
public class ReplaceRateChangeServiceImpl implements ReplaceRateChangeService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveReplaceRateChange(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ReplaceRateChange", "rc_code='" + store.get("rc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ReplaceRateChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存Detail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "ReplaceRateChangeDetail", "rd_id");
		baseDao.execute(gridSql);
		Object rc_id = store.get("rc_id");
		baseDao.logger.save(caller, "rc_id", rc_id);
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void deleteReplaceRateChange(int rc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ReplaceRateChange", "rc_statuscode", "rc_id=" + rc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { rc_id });
		// 删除ReplaceRateChange
		baseDao.deleteById("ReplaceRateChange", "rc_id", rc_id);
		// 删除ReplaceRateChangeDetail
		baseDao.deleteById("ReplaceRateChangedetail", "rd_rcid", rc_id);
		// 记录操作
		baseDao.logger.delete(caller, "rc_id", rc_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { rc_id });
	}

	@Override
	public void updateReplaceRateChangeById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ReplaceRateChange", "rc_statuscode", "rc_id=" + store.get("rc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ReplaceRateChange", "rc_id");
		baseDao.execute(formSql);
		// 修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "ReplaceRateChangeDetail", "rd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("rd_id") == null || s.get("rd_id").equals("") || s.get("rd_id").equals("0")
					|| Integer.parseInt(s.get("rd_id").toString()) == 0) {// 新添加的数据，id不存在
				String sql = SqlUtil.getInsertSql(s, "ReplaceRateChangeDetail", "rd_id");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		Object rc_id = store.get("rc_id");
		// 记录操作
		baseDao.logger.update(caller, "rc_id", rc_id);
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void printReplaceRateChange(int rc_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { rc_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "rc_id", rc_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { rc_id });
	}

	@Override
	public void auditReplaceRateChange(int rc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ReplaceRateChange", "rc_statuscode", "rc_id=" + rc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { rc_id });
		//审核时更新prr_rate为rd_newrate
		List<Object[]> prr = baseDao.getFieldsDatasByCondition("ReplaceRateChangeDetail", new String[]{"rd_groupcode","rd_prodcode","rd_id","rd_newrate"}, "rd_rcid="+rc_id);
		for(Object[] a : prr){
			if(StringUtil.hasText(a[0])&&StringUtil.hasText(a[1])){
				baseDao.execute("update ProdReplaceRate set prr_rate='"+a[3]+"' where prr_groupcode='"+a[0]+"' and prr_prodcode='"+a[1]+"'");
			}
		}
		// 执行审核操作
		baseDao.audit("ReplaceRateChange", "rc_id=" + rc_id, "rc_status", "rc_statuscode", "rc_auditdate", "rc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "rc_id", rc_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { rc_id });
	}

	@Override
	public void resAuditReplaceRateChange(int rc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ReplaceRateChange", "rc_statuscode", "rc_id=" + rc_id);
		StateAssert.resAuditOnlyAudit(status);
		//反审核时更新prr_rate为rd_oldrate
		List<Object[]> prr = baseDao.getFieldsDatasByCondition("ReplaceRateChangeDetail", new String[]{"rd_groupcode","rd_prodcode","rd_id","rd_oldrate"}, "rd_rcid="+rc_id);
		for(Object[] a : prr){
			if(StringUtil.hasText(a[0])&&StringUtil.hasText(a[1])){
				baseDao.execute("update ProdReplaceRate set prr_rate='"+a[3]+"' where prr_groupcode='"+a[0]+"' and prr_prodcode='"+a[1]+"'");
			}
		}
		// 执行反审核操作
		baseDao.resAudit("ReplaceRateChange", "rc_id=" + rc_id, "rc_status", "rc_statuscode", "rc_auditdate", "rc_auditman");
		baseDao.updateByCondition("ReplaceRateChangeDetail", "rd_statuscode='UNVALID',rd_status='" + BaseUtil.getLocalMessage("UNVALID")
				+ "'", "rd_rcid=" + rc_id);
		baseDao.logger.resAudit(caller, "rc_id", rc_id);
	}

	@Override
	public void submitReplaceRateChange(int rc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ReplaceRateChange", "rc_statuscode", "rc_id=" + rc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { rc_id });
		// 提交时更新rd_oldrate
		List<Object[]> prr = baseDao.getFieldsDatasByCondition("ReplaceRateChangeDetail", new String[]{"rd_groupcode","rd_prodcode","rd_id"}, "rd_rcid="+rc_id);
		for(Object[] a : prr){
			Object pr = baseDao.getFieldDataByCondition("ProdReplaceRate", "prr_rate", "prr_groupcode='"+a[0]+"' and prr_prodcode='"+a[1]+"'");
			if(StringUtil.hasText(pr)){
				baseDao.execute("update ReplaceRateChangeDetail set rd_oldrate='"+pr+"' where rd_id="+a[2]+"");
			}
		}
		// 执行提交操作
		baseDao.submit("ReplaceRateChange", "rc_id=" + rc_id, "rc_status", "rc_statuscode");
		baseDao.logger.submit(caller, "rc_id", rc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { rc_id });
	}

	@Override
	public void resSubmitReplaceRateChange(int rc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ReplaceRateChange", "rc_statuscode", "rc_id=" + rc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { rc_id });
		// 执行反提交操作
		baseDao.resOperate("ReplaceRateChange", "rc_id=" + rc_id, "rc_status", "rc_statuscode");
		baseDao.logger.resSubmit(caller, "rc_id", rc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { rc_id });
	}
	
}
