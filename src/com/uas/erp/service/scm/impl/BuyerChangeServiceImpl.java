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
import com.uas.erp.service.scm.BuyerChangeService;
@Service
public class BuyerChangeServiceImpl implements BuyerChangeService {
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public void saveBuyerChange(String caller, String formStore, String gridStore) {
		Map<Object, Object> formstore = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.handler("BuyerChange", "save", "before", new Object[] { formStore, gridstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(formstore, "BuyerChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		Object[] rc_id = new Object[gridstore.size()];
		for (int i = 0; i < gridstore.size(); i++) {
			Map<Object, Object> map = gridstore.get(i);
			rc_id[i] = baseDao.getSeqId("BuyerChange_SEQ");
			map.put("bc_id", rc_id[i]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridstore, "BuyerChange");
		baseDao.execute(gridSql);
		baseDao.logger.save("BuyerChange", "bc_id", formstore.get("bc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("BuyerChange", "save", "after", new Object[] { formStore, gridstore });
	}
	
	@Override
	public void updateBuyerChangeById(String formStore, String param,
			String caller) {
		Map<Object, Object> formstore = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(param);
		handlerService.handler("BuyerChange", "save", "before", new Object[] { formStore, gridstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(formstore, "BuyerChange", "bc_id");
		baseDao.execute(formSql);
		List<String> girdSql = SqlUtil.getUpdateSqlbyGridStore(gridstore, "BuyerChange", "bc_id");
		baseDao.execute(girdSql);
		// 记录操作
		baseDao.logger.update("BuyerChange", "bc_id", formstore.get("bc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("BuyerChange", "save", "after", new Object[] { formStore, gridstore });
	}
	
	@Override
	public void submitBuyerChange(String caller, int id) {
		// 执行提交前的其它逻辑
		handlerService.handler("BuyerChange", "commit", "before", new Object[] { id });
		 Object[] buyer = baseDao.getFieldsDataByCondition("BuyerChange", "bc_oldbuyercode,bc_newbuyercode", "bc_id=" + id);
		if (buyer[0] == null || buyer[1] == null || "".equals(buyer[0]) || "".equals(buyer[1])) {
			BaseUtil.showError("采购员移交，原采购员、新采购员都必须填写!");
		}
		 if (buyer[0].equals(buyer[1])) {
			BaseUtil.showError("原采购员、新采购员不能相同!");
		 }
		// 执行提交操作
		baseDao.submit("BuyerChange", "bc_id=" + id, "bc_status", "bc_statuscode");
		// 记录操作
		baseDao.logger.submit("BuyerChange", "bc_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler("BuyerChange", "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitBuyerChange(String caller, int id) {
		// 执行反提交操作
		handlerService.handler("BuyerChange", "resCommit", "before", new Object[] { id });
		baseDao.resOperate("BuyerChange", "bc_id=" + id, "bc_status", "bc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("BuyerChange", "bc_id", id);
		handlerService.handler("BuyerChange", "resCommit", "after", new Object[] { id });
	}

	@Override
	public void auditBuyerChange(int id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("buyerchange", "bc_statuscode", "bc_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		List<String> updateSql = new ArrayList<String>();
		Object[] fields = baseDao.getFieldsDataByCondition("buyerchange left join employee on bc_newbuyercode=em_code",
				"bc_newbuyercode,bc_newbuyer,bc_oldbuyer,bc_oldbuyercode,em_uu,em_id,bc_buyer,bc_cgycode", "bc_id=" + id);
		if(fields[6] != null && "1".equals(fields[6].toString())){
			updateSql.add("update productkind set pk_buyercode='" + fields[0] + "',pk_buyername='" + fields[1] + "' where pk_buyercode='" + fields[3] + "'");
			updateSql.add("update product set pr_buyercode='" + fields[0] + "',pr_buyername='" + fields[1] + "' where pr_buyercode='" + fields[3] + "'");
			updateSql.add("update vendor set ve_buyercode='" + fields[0] + "',ve_buyername='" + fields[1] + "',ve_buyerid='" + fields[5] + "',"
					+ "ve_buyeruu='" + fields[4] + "' where ve_buyercode='" + fields[3] + "'");
		}
		if(fields[7] != null && "1".equals(fields[7].toString())){
			updateSql.add("update productkind set pk_cggdycode='" + fields[0] + "',pk_cggdy='" + fields[1] + "' where pk_cggdycode='" + fields[3] + "'");
			updateSql.add("update product set pr_cggdycode='" + fields[0] + "',pr_cggdy='" + fields[1] + "' where pr_cggdycode='" + fields[3] + "'");
		}
		baseDao.execute(updateSql);
		// 执行审核操作
		baseDao.audit("BuyerChange", "bc_id=" + id, "bc_status", "bc_statuscode", "bc_auditdate", "bc_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "bc_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void deleteBuyerChange(String caller, int id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BuyerChange", "bc_statuscode", "bc_id=" + id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler("BuyerChange", "delete", "before", new Object[] {id});
		// 删除BuyerChange
		baseDao.deleteById("BuyerChange", "bc_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "bc_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler("BuyerChange", "delete", "after", new Object[] { id });
	}

}
