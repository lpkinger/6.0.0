package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.ProductSetService;

@Service("ProductSetService")
public class ProductSetServiceImpl implements ProductSetService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductSet(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProductSet", "ps_code='" + store.get("ps_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存ProductSet
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProductSet", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存ProductSetDetail
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		for (Map<Object, Object> s : grid) {
			s.put("psd_id", baseDao.getSeqId("ProductSetDETAIL_SEQ"));
			s.put("psd_code", store.get("ps_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ProductSetDetail");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "ps_code", store.get("ps_code"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteProductSet(int ps_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ProductSet", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ps_id });
		// 删除ProductSet
		baseDao.deleteById("ProductSet", "ps_id", ps_id);
		// 删除ProductSetDetail
		baseDao.deleteById("ProductSetdetail", "psd_psid", ps_id);
		// 记录操作
		baseDao.logger.delete(caller, "ps_id", ps_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { ps_id });
	}

	@Override
	public void updateProductSetById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ProductSet", "ps_statuscode", "ps_id=" + store.get("ps_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改ProductSet
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductSet", "ps_id");
		baseDao.execute(formSql);
		// 修改ProductSetDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "ProductSetDetail", "psd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("psd_id") == null || s.get("psd_id").equals("") || s.get("psd_id").equals("0")
					|| Integer.parseInt(s.get("psd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ProductSetDETAIL_SEQ");
				s.put("psd_code", store.get("ps_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "ProductSetDetail", new String[] { "psd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ps_id", store.get("ps_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void printProductSet(int ps_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("ProductSet", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.printOnlyAudited(status);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { ps_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "ps_id", ps_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { ps_id });
	}

	@Override
	public void auditProductSet(int ps_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductSet", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.auditOnlyCommited(status);
		String dets = baseDao.queryForObject(
				"select wm_concat(distinct psd_prodcode) from ProductSetDetail where psd_psid=? group by psd_prodcode having count(*)>1",
				String.class, ps_id);
		if (dets != null) {
			BaseUtil.showError("明细物料编号重复！" + dets);
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { ps_id });
		baseDao.audit("ProductSet", "ps_id=" + ps_id, "ps_status", "ps_statuscode", "ps_auditdate", "ps_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "ps_id", ps_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { ps_id });
	}

	@Override
	public void resAuditProductSet(int ps_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProductSet", new String[] { "ps_statuscode", "ps_code" }, "ps_id=" + ps_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		int count = baseDao.getCountByCondition("AppMouldDetail", "ad_pscode='" + status[1] + "'");
		if (count > 0) {
			BaseUtil.showError("模具资料已经被其他单据使用时，不能反审核!");
		}
		// 执行反审核操作
		baseDao.resOperate("ProductSet", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ps_id", ps_id);
	}

	@Override
	public void submitProductSet(int ps_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductSet", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao.queryForObject(
				"select wm_concat(distinct psd_prodcode) from ProductSetDetail where psd_psid=? group by psd_prodcode having count(*)>1",
				String.class, ps_id);
		if (dets != null) {
			BaseUtil.showError("明细物料编号重复！" + dets);
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { ps_id });
		// 执行提交操作
		baseDao.submit("ProductSet", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ps_id", ps_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { ps_id });
	}

	@Override
	public void resSubmitProductSet(int ps_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductSet", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { ps_id });
		// 执行反提交操作
		baseDao.resOperate("ProductSet", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ps_id", ps_id);
		handlerService.afterResSubmit(caller, new Object[] { ps_id });
	}

	@Override
	public void updateReturnqty(String data) {
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(data);
		Object returnqty = formdata.get("returnqty");
		Object prodcode = formdata.get("prodcode");
		Object psd_id = formdata.get("psd_id");
		SqlRowList rs = baseDao.queryForRowSet(
				"select psd_detno,psd_returnqty,psd_psid,psd_prodcode,psd_id from ProductSetDetail where psd_id=?", formdata.get("psd_id"));
		if (rs.next()) {
			int count = baseDao.getCount("select count(*) from ProductSetDetail where psd_psid=" + rs.getObject("psd_psid")
					+ " and psd_prodcode='" + prodcode + "' and psd_id<>" + rs.getObject("psd_id"));
			if (count > 0) {
				BaseUtil.showError("更改的料号不能与明细行的其他物料编号相同!");
			}
			// 记录操作
			if (prodcode != null && !"".equals(prodcode.toString()) && returnqty != rs.getObject(2)) {
				baseDao.execute("update ProductSetDetail set psd_prodcode=? where psd_id=?", prodcode, psd_id);
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新返还数量", "行" + rs.getInt(1) + ":"
						+ rs.getObject(2) + "=>" + returnqty, "ProductSet|ps_id=" + rs.getInt(3)));
			}
			if (returnqty != null && !"".equals(returnqty.toString()) && !prodcode.equals(rs.getString(4))) {
				baseDao.execute("update ProductSetDetail set psd_returnqty=? where psd_id=?", returnqty, psd_id);
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新物料编号", "行" + rs.getInt(1) + ":"
						+ rs.getString(2) + "=>" + prodcode, "ProductSet|ps_id=" + rs.getInt(3)));
			}
		}
	}

	@Override
	public void updateVendReturn(Integer id, String vendstatus, String vendremark) {
		baseDao.updateByCondition("ProductSet", "ps_vendreturnstatus='" + vendstatus + "', ps_vendreturnremark='" + vendremark + "'",
				"ps_id =" + id);
		// 记录操作
		baseDao.logger.others("更新供应商返还状态", "msg.updateSuccess", "ProductSet", "ps_id", id);
	}

	@Override
	public void updateCustReturn(Integer id, String custstatus, String custremark) {
		baseDao.updateByCondition("ProductSet", "ps_custreturnstatus='" + custstatus + "', ps_custreturnremark='" + custremark + "'",
				"ps_id =" + id);
		// 记录操作
		baseDao.logger.others("更新客户返还状态", "msg.updateSuccess", "ProductSet", "ps_id", id);
	}
}
