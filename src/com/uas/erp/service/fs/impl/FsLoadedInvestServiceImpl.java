package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.fs.FsLoadedInvestService;

@Service
public class FsLoadedInvestServiceImpl implements FsLoadedInvestService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	public void saveInvestReport(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "FSLOADEDINVEST"));
		baseDao.logger.save(caller, "li_id", store.get("li_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateInvestReport(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object status = baseDao.getFieldDataByCondition("FSLOADEDINVEST", "li_statuscode", "li_id=" + store.get("li_id"));
		StateAssert.updateOnlyEntering(status);
		Object value = null;
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FSLOADEDINVEST", "li_id"));
		baseDao.saveClob("FSLOADEDINVEST", clobFields, clobStrs, "li_id=" + store.get("bs_caid"));
		// 记录操作
		baseDao.logger.update(caller, "li_id", store.get("li_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteInvestReport(int li_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { li_id });
		
		// 删除主表内容
		baseDao.deleteById("FSLOADEDINVEST", "li_id", li_id);
		//删除授信业务状况
		baseDao.deleteById("FSCREDITCONDITION", "cd_liid", li_id);
		//删除财务状况
		baseDao.deleteById("FSLOADEDINVESTFAITEMS", "lfi_liid", li_id);
		//删除买卖双方交易
		baseDao.deleteById("fsloadedinvesttrans", "lft_liid", li_id);
		//删除主要账户结算检查
		baseDao.deleteById("FSSETTLEACCOUNT", "sta_liid", li_id);
		//删除担保条件
		baseDao.deleteById("FSLOADEDINVESTMORTGAGE", "lfm_liid", li_id);
		
		baseDao.logger.delete(caller, "li_id", li_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { li_id });
	}

	@Override
	public void submitInvestReport(int li_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FSLOADEDINVEST", "li_statuscode", "li_id=" + li_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { li_id });
		// 执行提交操作
		baseDao.submit("FSLOADEDINVEST", "li_id=" + li_id, "li_status", "li_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "li_id", li_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { li_id });
	}

	@Override
	public void resSubmitInvestReport(int li_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FSLOADEDINVEST", "li_statuscode", "li_id=" + li_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { li_id });
		// 执行反提交操作
		baseDao.resOperate("FSLOADEDINVEST", "li_id=" + li_id, "li_status", "li_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "li_id", li_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { li_id });
	}

	@Override
	public void auditInvestReport(int li_id, String caller) {
		// 只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("FSLOADEDINVEST", "li_statuscode", "li_id=" + li_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { li_id });

		baseDao.audit("FSLOADEDINVEST", "li_id=" + li_id, "li_status", "li_statuscode", "li_auditdate", "li_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "li_id", li_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { li_id });
	}

	@Override
	public void resAuditInvestReport(int li_id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FSLOADEDINVEST", "li_statuscode", "li_id=" + li_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("FSLOADEDINVEST", li_id);
		handlerService.beforeResAudit(caller, new Object[] { li_id });
		// 执行反审核操作
		baseDao.resAudit("FSLOADEDINVEST", "li_id=" + li_id, "li_status", "li_statuscode", "li_auditman", "li_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "li_id", li_id);
		handlerService.afterResAudit(caller, new Object[] { li_id });
	}

	@Override
	public void getDefault(int id) {
		Object custcode = baseDao.getFieldDataByCondition("FSLOADEDINVEST", "li_custcode", "li_id = " + id);
		baseDao.deleteById("FSCREDITCONDITION", "cd_liid", id);
		if (custcode != null) {
			SqlRowList rs = baseDao.queryForRowSet("select AA_CODE,CQ_QUOTATYPE,AA_MFCUSTNAME,AA_LEFTAMOUNT,"
					+ "to_char(AA_ACTPAYDATE,'yyyy-MM-dd')||'~'||to_char(AA_MATURITYDATE,'yyyy-MM-dd') aa_sedate,"
					+ "AA_ISOVERDUE,nvl(od_odinterest,0) od_odinterest,aa_assuremeans from ACCOUNTAPPLY left join CUSTOMERQUOTA "
					+ "on AA_CACODE=CQ_CODE left join FsOverdue on od_aacode=aa_code where AA_CUSTCODE= ? "
					+ "and NVL(AA_ISCLOSEOFF,'否')='否'", custcode);
			List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
			while (rs.next()) {
				Map<Object, Object> map = new HashMap<Object, Object>();
				map.put("cd_id", baseDao.getSeqId("FSCREDITCONDITION_SEQ"));
				map.put("cd_liid", id);
				map.put("cd_quotatype", rs.getGeneralString("cq_quotatype"));
				map.put("cd_mfcustname", rs.getGeneralString("aa_mfcustname"));
				map.put("cd_leftamount", rs.getGeneralDouble("aa_leftamount"));
				map.put("cd_sedate", rs.getGeneralString("aa_sedate"));
				map.put("cd_odinterest", rs.getGeneralDouble("od_odinterest"));
				map.put("cd_isoverdue", rs.getGeneralString("aa_isoverdue"));
				map.put("cd_assuremeans", rs.getGeneralString("aa_assuremeans"));
				map.put("cd_aacode", rs.getGeneralString("aa_code"));
				list.add(map);
			}
			baseDao.execute(SqlUtil.getInsertSqlbyGridStore(list, "FSCREDITCONDITION"));
		}
		
		Master master = SystemSession.getUser().getCurrentMaster();
		Master parentMaster = null;
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			parentMaster = master;
		} else if (null != master.getMa_pid() && master.getMa_pid() > 0) {
			parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
		}
		
		if (null != parentMaster) {
			baseDao.procedure("FS_LOADEDINVESTFAITEMS", new Object[] { parentMaster.getMa_name(), id });
		} else {
			baseDao.procedure("FS_LOADEDINVESTFAITEMS", new Object[] { "", id });
		}
	}

	@Override
	public void updateTransactionCheck(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object status = baseDao.getFieldDataByCondition("FSLOADEDINVEST", "li_statuscode", "li_id=" + store.get("li_id"));
		StateAssert.updateOnlyEntering(status);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FSLOADEDINVEST", "li_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "FSLOADEDINVESTTRANS", "lft_id"));
		// 记录操作
		baseDao.logger.update(caller, "li_id", store.get("li_id"));
	}

	@Override
	public void updateGuaranteeCheck(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object status = baseDao.getFieldDataByCondition("FSLOADEDINVEST", "li_statuscode", "li_id=" + store.get("li_id"));
		StateAssert.updateOnlyEntering(status);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FSLOADEDINVEST", "li_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "FSLOADEDINVESTMORTGAGE", "lfm_id"));
		// 记录操作
		baseDao.logger.update(caller, "li_id", store.get("li_id"));
	}

	private String getSql(Map<Object, Object> map,String keyField, String type){
		if (map.get(keyField) == null || "".equals(map.get(keyField)) || "null".equals(map.get(keyField))
				|| Integer.parseInt(String.valueOf(map.get(keyField))) <= 0) {
			map.put(keyField, baseDao.getSeqId("FSSETTLEACCOUNT_SEQ"));
			map.put("sta_type", type);
			return SqlUtil.getInsertSqlByMap(map, "FSSETTLEACCOUNT");
		}else{
			return SqlUtil.getUpdateSqlByFormStore(map, "FSSETTLEACCOUNT", "sta_id");
		}
	}
	
	@Override
	public void saveSettleAccountCheck(String formStore, String param1,
			String param2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object status = baseDao.getFieldDataByCondition("FSLOADEDINVEST", "li_statuscode", "li_id=" + store.get("li_id"));
		StateAssert.updateOnlyEntering(status);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(param2);
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FSLOADEDINVEST", "li_id"));
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : gstore1) {
			sqls.add(getSql(map,"sta_id","转入资金"));
		}
		for (Map<Object, Object> map : gstore2) {
			sqls.add(getSql(map,"sta_id","支付资金"));
		}
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.update(caller, "li_id", store.get("li_id"));
	}

}
