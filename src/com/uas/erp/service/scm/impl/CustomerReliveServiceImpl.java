package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.CustomerReliveService;

@Service
public class CustomerReliveServiceImpl implements CustomerReliveService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomerRelive(String formStore, String gridstore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridstore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "customerrelive", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存Purchasetypedetail
		for (Map<Object, Object> map : gstore) {
			map.put("crd_id", baseDao.getSeqId("customerrelivedetail_seq"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "customerrelivedetail");
		baseDao.execute(gridSql);
		updateDetailAmount(store.get("cr_id"));
		// 记录操作
		baseDao.logger.save(caller, "cr_id", store.get("cr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updateCustomerReliveById(String formStore, String gridstore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridstore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "customerrelive", "cr_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridstore, "customerrelivedetail", "crd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("crd_id") == null || s.get("crd_id").equals("") || s.get("crd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("customerrelivedetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "customerrelivedetail", new String[] { "crd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		updateDetailAmount(store.get("cr_id"));
		// 记录操作
		baseDao.logger.update(caller, "cr_id", store.get("cr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteCustomerRelive(int cr_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CustomerRelive", "cr_statuscode", "cr_id=" + cr_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, cr_id);
		// 删除purchase
		baseDao.deleteById("CustomerRelive", "cr_id", cr_id);
		// 删除purchaseDetail
		baseDao.deleteById("CustomerRelivedetail", "crd_crid", cr_id);
		// 记录操作
		baseDao.logger.delete(caller, "cr_id", cr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, cr_id);
	}

	@Override
	public void auditCustomerRelive(int cr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("CustomerRelive", "cr_statuscode", "cr_id=" + cr_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, cr_id);
		// 执行审核操作
		baseDao.audit("CustomerRelive", "cr_id=" + cr_id, "cr_status", "cr_statuscode", "CR_AUDITDATE", "CR_AUDITORNAME");
		SqlRowList rs = baseDao.queryForRowSet("select crd_custid,crd_commitdate from customerrelivedetail where crd_crid=?",
				new Object[] { cr_id });
		while (rs.next()) {
			baseDao.execute("UPDATE CUSTOMER SET cu_status='长期',cu_statuscode=null,cu_commitdate=?,cu_salegroup='申请解挂' WHERE cu_id=?",
					new Object[] { rs.getObject("CRD_COMMITDATE"), rs.getObject("CRD_CUSTID") });
		}
		// 记录操作
		baseDao.logger.audit(caller, "cr_id", cr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, cr_id);
	}

	@Override
	public void submitCustomerRelive(int cr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CustomerRelive", "cr_statuscode", "cr_id=" + cr_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, cr_id);
		// 执行提交操作
		baseDao.submit("CustomerRelive", "cr_id=" + cr_id, "cr_status", "cr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "cr_id", cr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, cr_id);
	}

	@Override
	public void resSubmitCustomerRelive(int cr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CustomerRelive", "cr_statuscode", "cr_id=" + cr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, cr_id);
		// 执行反提交操作
		baseDao.resOperate("CustomerRelive", "cr_id=" + cr_id, "cr_status", "cr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "cr_id", cr_id);
		handlerService.afterResSubmit(caller, cr_id);
	}

	private void updateDetailAmount(Object cr_id) {
		List<Object[]> list = baseDao.getFieldsDatasByCondition("CustomerReliveDetail", new String[] { "crd_custcode", "crd_id" },
				"crd_crid='" + cr_id + "'");
		baseDao.execute("update arbill set ab_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(ab_date,'yyyymm') and ab_currency=cm_crname),1) where nvl(ab_rate,0)=0");
		String sql = "SELECT round(SUM(NVL(ca_amount,0)*NVL(cr_rate,1)),2) endamount FROM Custar,Currencys "
				+ "WHERE ca_custcode=? AND cr_name=ca_currency";
		String sql1 = "SELECT round(SUM((nvl(ab_aramount,0)-nvl(ab_payamount,0))*NVL(cr_rate,1)) ,2) overamount FROM ARBill,Currencys WHERE ab_currency=cr_name and ab_custcode=? AND ab_statuscode='POSTED' AND trunc(ab_paydate)<trunc(sysdate) AND abs(nvl(ab_aramount,0))>abs(nvl(ab_payamount,0))";
		SqlRowList rs = null;
		SqlRowList rs1 = null;
		for (Object[] o : list) {
			rs = baseDao.queryForRowSet(sql, new Object[] { o[0] });
			if (rs.next()) {
				baseDao.execute("UPDATE CustomerReliveDetail SET crd_endamount=? where crd_id='" + o[1] + "'",
						new Object[] { rs.getObject("ENDAMOUNT") });
			}
			rs1 = baseDao.queryForRowSet(sql1, new Object[] { o[0] });
			if (rs1.next()) {
				baseDao.execute("UPDATE CustomerReliveDetail SET crd_over=? where crd_id='" + o[1] + "'",
						new Object[] { rs1.getObject("overamount") });
			}
		}
	}

	@Override
	public void countCustReturn() {
		baseDao.procedure("SP_CUSTRETURN", new Object[] {});
	}
}
