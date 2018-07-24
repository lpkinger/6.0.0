package com.uas.erp.service.pm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.FormAttachDao;
import com.uas.erp.model.FormAttach;
import com.uas.erp.service.pm.ECRService;

@Service("ECRService")
public class ECRServiceImpl implements ECRService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormAttachDao formAttachDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveECR(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String filesId = null;
		if (store.containsKey("files")) {
			filesId = (String) store.get("files");
			store.remove("files");
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ECR", "ecr_code='" + store.get("ecr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}		
		// 执行保存前的其它逻辑
		handlerService.beforeSave("ECR",new Object[] { store,grid});		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ECR", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		//根据参数配置ifDCNChangePR， 是否启用BOM阶段等于DCN的，单据前缀设为DR
		bool = baseDao.isDBSetting(caller, "ifDCNChangePR");
		if(bool){	
			Object[] data = baseDao.getFieldsDataByCondition("ecr",
					"ecr_prodstage,ecr_code", "ecr_id=" +store.get("ecr_id"));			
			if (data[0] != null && "DCN".equals(data[0])) {	
				Object ob = baseDao.getFieldDataByCondition("MAXNUMBERS", "MN_LEADCODE", "mn_tablename='"+caller+"'");
				if(ob != null){
				     baseDao.updateByCondition("ECR", "ecr_code='" +data[1].toString().replaceAll(ob.toString(), "DR") + "'",
						      "ecr_code='" +data[1]+ "'");
				}else{
					 baseDao.updateByCondition("ecr", "ecr_code='DR'||'"+data[1].toString()+ "'",
							  "ecr_code='" + data[1]+ "'");
				}
			}
		}
		// 保存附件
		if (filesId != null) {
			String[] files = filesId.split(",");
			int id = Integer.parseInt(store.get("ecr_id").toString());
			String path = null;
			FormAttach attach = null;
			for (String file : files) {
				if (file != null && !file.equals("")) {
					Object res = baseDao.getFieldDataByCondition("filepath", "fp_path",
							"fp_id=" + Integer.parseInt(file));
					if (res != null) {
						path = (String) res;
						attach = new FormAttach();
						attach.setFa_caller("ECR");
						attach.setFa_keyvalue(id);
						attach.setFa_path(path);
						formAttachDao.saveAttach(attach);
					}
				}
			}
		}
		try {
			// 记录操作
			baseDao.logger.save(caller, "ecr_id", store.get("ecr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave("ECR",new Object[] { store,grid});
	}

	@Override
	public void deleteECR(int ecr_id, String caller) {
		// 只能删除在录入的ECR
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatuscode", "ecr_id=" + ecr_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel("ECR",  new Object[] { ecr_id});		
		// 删除ECR
		baseDao.deleteById("ECR", "ecr_id", ecr_id);
		/*
		 * //删除purchaseDetail baseDao.deleteById("ECRDetail", "ecrd_ecrid",
		 * ecr_id);
		 */
		// 记录操作
		baseDao.logger.delete(caller, "ecr_id", ecr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("ECR",  new Object[] { ecr_id});
	}

	@Override
	public void updateECRById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的ECR!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatuscode", "ecr_id=" + store.get("ecr_id"));
		StateAssert.updateOnlyEntering(status);
		// 更新ECR计划下达数\本次下达数\状态
		// purchaseDao.updatePurchasePlan(Integer.parseInt((String)store.get("pu_id")));
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate("ECR",new Object[] { store,grid});		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ECR", "ecr_id");
		baseDao.execute(formSql);		
		/*
		 * //修改ECRDetail List<String> gridSql =
		 * SqlUtil.getUpdateSqlbyGridStore(gridStore, "ECRDetail", "ecrd_id");
		 * for(Map<Object, Object> s:gstore){ if(s.get("ecrd_id") == null ||
		 * s.get("ecrd_id").equals("") ||
		 * s.get("ecrd_id").equals("0")){//新添加的数据，id不存在 int id =
		 * baseDao.getSeqId("ECRDETAIL_SEQ"); String sql =
		 * SqlUtil.getInsertSqlByMap(s, "ECRDetail", new String[]{"ecrd_id"},
		 * new Object[]{id}); gridSql.add(sql); } } baseDao.execute(gridSql);
		 */
		// 记录操作
		baseDao.logger.save(caller, "ecr_id", store.get("ecr_id"));
		// 更新上次采购价格、供应商
		// purchaseDao.updatePrePurchase((String)store.get("pu_code"),
		// (String)store.get("pu_date"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate("ECR",new Object[] { store,grid});
	}

	@Override
	public void auditECR(int ecr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatuscode", "ecr_id=" + ecr_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("ECR",new Object[] { ecr_id});		// 执行审核操作
		baseDao.updateByCondition(
				"ECR",
				"ecr_checkstatuscode='AUDITED',ecr_checkstatus='" + BaseUtil.getLocalMessage("AUDITED") + 
				"',ecr_auditman='" + SystemSession.getUser().getEm_name() + "'," + "ecr_auditdate="
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",ecr_recordman2=ecr_recordman," + "ecr_recorddate2=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()),
				"ecr_id=" + ecr_id);
		// 记录操作
		baseDao.logger.audit(caller, "ecr_id", ecr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("ECR",new Object[] { ecr_id});
	}

	@Override
	public void resAuditECR(int ecr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object []data = baseDao.getFieldsDataByCondition("ECR", "ECR_checkstatuscode,ECR_checkstatus2code", "ecr_id=" + ecr_id);
		StateAssert.resAuditOnlyAudit(data[0]);
		if(data[1]!=null && (data[1].equals("AUDITED") || data[1].equals("COMMITED"))){
			//评审单已提交或者已审核 申请不允许反审核
			BaseUtil.showError("对应评审单是已提交或者已审核状态 不允许反审核!");
		}
		// 执行反审核操作
		baseDao.updateByCondition("ECR",
				"ecr_checkstatuscode='ENTERING',ecr_checkstatus='" + BaseUtil.getLocalMessage("ENTERING")
						+ "'", "ecr_id=" + ecr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ecr_id", ecr_id);
	}

	@Override
	public void submitECR(int ecr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatuscode", "ecr_id=" + ecr_id);
		StateAssert.submitOnlyEntering(status);
		// 只能选择已审核的物料!
		Object code = baseDao.getFieldDataByCondition("ECR", "ecr_prodcode", "ecr_id=" + ecr_id);
		if (code!=null && !code.toString().equals("null") && code.toString().length()>2){
			int i=baseDao.getCount("select count(1) from product where pr_code='"+code+"' and pr_statuscode='AUDITED'");
			if (i==0){
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited"));
			}
		} 
		// 只能选择已审核的客户!
		Object code1 = baseDao.getFieldDataByCondition("ECR", "ecr_cucode", "ecr_id=" + ecr_id);
		if (code1!=null && !code1.toString().equals("null") && code1.toString().length()>2){
			int i=baseDao.getCount("select count(1) from Customer where cu_code='"+code1+"' and cu_auditstatuscode='AUDITED'");
			if (i==0){
				BaseUtil.showError(BaseUtil.getLocalMessage("customer_onlyAudited"));
			}
		} 
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("ECR",  new Object[] { ecr_id});		// 执行提交操作
		baseDao.submit("ECR", "ecr_id=" + ecr_id, "ecr_checkstatus", "ecr_checkstatuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ecr_id", ecr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("ECR",  new Object[] { ecr_id});
	}

	@Override
	public void resSubmitECR(int ecr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatuscode", "ecr_id=" + ecr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("ECR", new Object[] { ecr_id});		// 执行反提交操作
		baseDao.updateByCondition("ECR",
				"ecr_checkstatuscode='ENTERING',ecr_checkstatus='" + BaseUtil.getLocalMessage("ENTERING")
						+ "'", "ecr_id=" + ecr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ecr_id", ecr_id);
		handlerService.afterResSubmit("ECR", new Object[] { ecr_id});
	}
}
