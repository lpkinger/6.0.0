package com.uas.erp.service.cost.impl;

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
import com.uas.erp.service.cost.ProductWHMonthAdjustService;

@Service("productWHMonthAdjustService")
public class ProductWHMonthAdjustServiceImpl implements ProductWHMonthAdjustService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductWHMonthAdjust(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("pwa_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProductWHMonthAdjust", "pwa_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		baseDao.checkCloseMonth("MONTH-P", store.get("pwa_date"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave("ProductWHMonthAdjust", new Object[] { store, grid });
		// 保存ProductWHMonthAdjust
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProductWHMonthAdjust", new String[] {}, new Object[] {});
		Object yearmonth = store.get("pwa_yearmonth");
		baseDao.execute(formSql);
		// 保存ProductWHMonthAdjustDetail
		for (Map<Object, Object> m : grid) {
			m.put("pwd_id", baseDao.getSeqId("PRODUCTWHMONTHADJUSTDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ProductWHMonthAdjustDetail");
		baseDao.execute(gridSql);
		baseDao.execute("update ProductWHMonthAdjustDetail set (pwd_oldqty,pwd_oldamount)=(select nvl(pwm_endqty,0),nvl(pwm_endamount,0) from ProductWhMonth where pwm_prodcode=pwd_prodcode and pwm_whcode=pwd_whcode and pwm_yearmonth="
				+ yearmonth + ") where pwd_pwaid=" + store.get("pwa_id"));
		baseDao.logger.save("ProductWHMonthAdjust", "pwa_id", store.get("pwa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave("ProductWHMonthAdjust", new Object[] { store, grid });
	}

	@Override
	public void deleteProductWHMonthAdjust(int pwa_id) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("ProductWHMonthAdjust", new String[] { "pwa_statuscode", "pwa_date" }, "pwa_id="
				+ pwa_id);
		StateAssert.delOnlyEntering(status[0]);
		baseDao.checkCloseMonth("MONTH-P", status[1]);
		checkVoucher(pwa_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel("ProductWHMonthAdjust", pwa_id);
		// 删除ProductWHMonthAdjust
		baseDao.deleteById("ProductWHMonthAdjust", "pwa_id", pwa_id);
		// 删除ProductWHMonthAdjustDetail
		baseDao.deleteById("ProductWHMonthAdjustdetail", "pwd_pwaid", pwa_id);
		// 记录操作
		baseDao.logger.delete("ProductWHMonthAdjust", "pwa_id", pwa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("ProductWHMonthAdjust", pwa_id);
	}

	@Override
	public void updateProductWHMonthAdjustById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ProductWHMonthAdjust", "pwa_statuscode", "pwa_id=" + store.get("pwa_id"));
		StateAssert.updateOnlyEntering(status);
		baseDao.checkCloseMonth("MONTH-P", store.get("pwa_date"));
		checkVoucher(store.get("pwa_id"));
		// 执行修改前的其它逻辑
		handlerService.beforeSave("ProductWHMonthAdjust", new Object[] { store, gstore });
		// 修改Application
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductWHMonthAdjust", "pwa_id");
		Object yearmonth = store.get("pwa_yearmonth");
		baseDao.execute(formSql);
		// 修改ApplicationDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProductWHMonthAdjustDetail", "pwd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pwd_id") == null || s.get("pwd_id").equals("") || s.get("pwd_id").equals("0")
					|| Integer.parseInt(s.get("pwd_id").toString()) == 0) {// 新添加的数据，id不存在
				s.put("pwd_id", baseDao.getSeqId("ProductWHMonthAdjustDETAIL_SEQ"));
				String sql = SqlUtil.getInsertSqlByMap(s, "ProductWHMonthAdjustDetail");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update ProductWHMonthAdjustDetail set (pwd_oldqty,pwd_oldamount)=(select nvl(pwm_endqty,0),nvl(pwm_endamount,0) from ProductWhMonth where pwm_prodcode=pwd_prodcode and pwm_whcode=pwd_whcode and pwm_yearmonth="
				+ yearmonth + ") where pwd_pwaid=" + store.get("pwa_id"));
		// 记录操作
		baseDao.logger.update("ProductWHMonthAdjust", "pwa_id", store.get("pwa_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("ProductWHMonthAdjust", new Object[] { store, gstore });
	}

	@Override
	public String[] printProductWHMonthAdjust(int pwa_id, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint("ProductWHMonthAdjust", pwa_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("ProductWHMonthAdjust", "pwa_id=" + pwa_id, "pwa_status", "pwa_statuscode");
		// 记录操作
		baseDao.logger.print("ProductWHMonthAdjust", "pwa_id", pwa_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint("ProductWHMonthAdjust", pwa_id);
		return keys;
	}

	@Override
	public void auditProductWHMonthAdjust(int pwa_id) {
		// 只能对状态为[已提交]的单据进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProductWHMonthAdjust", new String[] { "pwa_statuscode", "pwa_date" }, "pwa_id="
				+ pwa_id);
		StateAssert.auditOnlyCommited(status[0]);
		baseDao.checkCloseMonth("MONTH-P", status[1]);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("ProductWHMonthAdjust", pwa_id);
		// 执行审核操作
		baseDao.audit("ProductWHMonthAdjust", "pwa_id=" + pwa_id, "pwa_status", "pwa_statuscode", "pwa_auditdate", "pwa_auditman");
		// 记录操作
		baseDao.logger.audit("ProductWHMonthAdjust", "pwa_id", pwa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("ProductWHMonthAdjust", pwa_id);
	}

	@Override
	public void resAuditProductWHMonthAdjust(int pwa_id) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProductWHMonthAdjust", new String[] { "pwa_statuscode", "pwa_date" }, "pwa_id="
				+ pwa_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		baseDao.checkCloseMonth("MONTH-P", status[1]);
		// 执行反审核操作
		baseDao.resAudit("ProductWHMonthAdjust", "pwa_id=" + pwa_id, "pwa_status", "pwa_statuscode", "pwa_auditdate", "pwa_auditman");
		// 记录操作
		baseDao.logger.resAudit("ProductWHMonthAdjust", "pwa_id", pwa_id);
	}

	@Override
	public void submitProductWHMonthAdjust(int pwa_id) {
		// 只能对状态为[在录入]的单据进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProductWHMonthAdjust", new String[] { "pwa_statuscode", "pwa_date" }, "pwa_id="
				+ pwa_id);
		StateAssert.submitOnlyEntering(status[0]);
		baseDao.checkCloseMonth("MONTH-P", status[1]);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("ProductWHMonthAdjust", pwa_id);
		// 执行提交操作
		baseDao.submit("ProductWHMonthAdjust", "pwa_id=" + pwa_id, "pwa_status", "pwa_statuscode");
		// 记录操作
		baseDao.logger.submit("ProductWHMonthAdjust", "pwa_id", pwa_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("ProductWHMonthAdjust", pwa_id);
	}

	@Override
	public void resSubmitProductWHMonthAdjust(int pwa_id) {
		// 只能对状态为[已提交]的单据进行反提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProductWHMonthAdjust", new String[] { "pwa_statuscode", "pwa_date" }, "pwa_id="
				+ pwa_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		baseDao.checkCloseMonth("MONTH-P", status[1]);
		// 执行反提交操作
		baseDao.resOperate("ProductWHMonthAdjust", "pwa_id=" + pwa_id, "pwa_status", "pwa_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("ProductWHMonthAdjust", "pwa_id", pwa_id);
	}

	static final String AMOUNT_ADD = "update productwhmonth set pwm_endamount=nvl(pwm_endamount,0)+nvl((select sum(pwd_amount) from ProductWHMonthAdjust,ProductWHMonthAdjustDetail where pwa_id=pwd_pwaid and pwd_whcode=pwm_whcode and pwa_yearmonth=pwm_yearmonth and pwd_prodcode=pwm_prodcode and pwa_statuscode='POSTED' and pwa_id=?),0) where pwm_yearmonth=(select pwa_yearmonth from ProductWHMonthAdjust where pwa_id=?) and (pwm_whcode,pwm_prodcode) in (select pwd_whcode,pwd_prodcode from ProductWHMonthAdjust,ProductWHMonthAdjustDetail where pwa_id=pwd_pwaid and pwa_id=?)";

	@Override
	public void postProductWHMonthAdjust(int pwa_id) {
		// 只能对状态为[已审核]的单据进行过账操作!
		boolean isPosted = baseDao.checkIf("ProductWHMonthAdjust", "pwa_id=" + pwa_id + " and nvl(pwa_statuscode,' ')='AUDITED'");
		if (!isPosted) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyAudit"));
		} else {
			Object[] pwadate = baseDao.getFieldsDataByCondition("ProductWHMonthAdjust", new String[] { "pwa_date" }, "pwa_id=" + pwa_id);
			baseDao.checkCloseMonth("MONTH-P", pwadate[0]);
			handlerService.beforePost("ProductWHMonthAdjust", pwa_id);
			baseDao.execute("update ProductWHMonthAdjust set pwa_statuscode='POSTED',pwa_status='" + BaseUtil.getLocalMessage("POSTED")
					+ "',pwa_postman='" + SystemSession.getUser().getEm_name() + "',pwa_postdate=sysdate where pwa_id=?", pwa_id);
			// 库存+
			baseDao.execute(AMOUNT_ADD, pwa_id, pwa_id, pwa_id);
			// 记录操作
			baseDao.logger.post("ProductWHMonthAdjust", "pwa_id", pwa_id);
			handlerService.afterPost("ProductWHMonthAdjust", pwa_id);
		}
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pwa_vouchercode) from ProductWHMonthAdjust where pwa_id=? and nvl(pwa_vouchercode,' ') <>' ' and pwa_vouchercode<>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	static final String AMOUNT_RED = "update productwhmonth set pwm_endamount=nvl(pwm_endamount,0)-nvl((select sum(pwd_amount) from ProductWHMonthAdjust,ProductWHMonthAdjustDetail where pwa_id=pwd_pwaid and pwd_whcode=pwm_whcode and pwa_yearmonth=pwm_yearmonth and pwd_prodcode=pwm_prodcode and pwa_statuscode='POSTED' and pwa_id=?),0) where pwm_yearmonth=(select pwa_yearmonth from ProductWHMonthAdjust where pwa_id=?) and (pwm_whcode,pwm_prodcode) in (select pwd_whcode,pwd_prodcode from ProductWHMonthAdjust,ProductWHMonthAdjustDetail where pwa_id=pwd_pwaid and pwa_id=?)";

	@Override
	public void resPostProductWHMonthAdjust(int pwa_id) {
		checkVoucher(pwa_id);
		// 只能对状态为[已过账]的单据进行反过账操作!
		SqlRowList rs = baseDao.queryForRowSet("select pwa_vouchercode,pwa_statuscode from ProductWHMonthAdjust where pwa_id=?", pwa_id);
		if (rs.next()) {
			if (!"POSTED".equals(rs.getString(2))) {
				BaseUtil.showError(BaseUtil.getLocalMessage("common.resPost_onlyPost"));
			} else {
				Object[] pwadate = baseDao
						.getFieldsDataByCondition("ProductWHMonthAdjust", new String[] { "pwa_date" }, "pwa_id=" + pwa_id);
				baseDao.checkCloseMonth("MONTH-P", pwadate[0]);
				handlerService.beforeResPost("ProductWHMonthAdjust", pwa_id);
				// 库存-
				baseDao.execute(AMOUNT_RED, pwa_id, pwa_id, pwa_id);
				baseDao.execute(
						"update ProductWHMonthAdjust set pwa_statuscode='ENTERING',pwa_status='" + BaseUtil.getLocalMessage("ENTERING")
								+ "',pwa_postman=null,pwa_postdate=null where pwa_id=?", pwa_id);
				// 记录操作
				baseDao.logger.resPost("ProductWHMonthAdjust", "pwa_id", pwa_id);
				handlerService.afterResPost("ProductWHMonthAdjust", pwa_id);
			}
		}
	}

}
