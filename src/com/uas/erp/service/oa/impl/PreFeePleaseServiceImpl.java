package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.Assert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.PreFeePleaseService;

@Service("preFeePleaseService")
public class PreFeePleaseServiceImpl implements PreFeePleaseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void savePreFeePlease(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!		
		boolean bool = baseDao.checkByCondition("PreFeePlease", "fp_code='" + store.get("fp_code") + "'");
		Assert.isTrue(bool, "common.save_codeHasExist");
		//执行保存前的其它逻辑
		handlerService.handler("PreFeePlease", "save", "before", new Object[]{formStore});
		//保存PreFeePlease
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PreFeePlease", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存PreFeePleaseDetail
		Object[] fpd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			fpd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				fpd_id[i] = baseDao.getSeqId("PreFeePleaseDETAIL_SEQ");
			}
		} else {
			fpd_id[0] = baseDao.getSeqId("PreFeePleaseDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "PreFeePleaseDetail", "fpd_id", fpd_id);
		baseDao.execute(gridSql);
		baseDao.updateByCondition("PreFeePlease", "fp_pleaseamount="+baseDao.getFieldDataByCondition("PreFeePleasedetail", "sum(fpd_total)", "fpd_fpid="+store.get("fp_id")), "fp_id="+store.get("fp_id"));
		try{
			//记录操作
			baseDao.logger.save("PreFeePlease", "fp_id", store.get("fp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.handler("PreFeePlease", "save", "after", new Object[]{formStore});
	}

	@Override
	public void deletePreFeePlease(int fp_id) {
		Object status = baseDao.getFieldDataByCondition("PreFeePlease", "fp_statuscode", "fp_id=" + fp_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		//执行删除前的其它逻辑
		handlerService.handler("PreFeePlease", "delete", "before", new Object[]{fp_id});
		//删除PreFeePlease
		baseDao.deleteById("PreFeePlease", "fp_id", fp_id);
		//删除PreFeePleaseDetail
		baseDao.deleteById("PreFeePleasedetail", "fpd_fpid", fp_id);
		//记录操作
		baseDao.logger.delete("PreFeePlease", "fp_id", fp_id);
		//执行删除后的其它逻辑
		handlerService.handler("PreFeePlease", "delete", "after", new Object[]{fp_id});
	}

	@Override
	public void updatePreFeePleaseById(String formStore, String gridStore
			) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PreFeePlease", "fp_statuscode", "fp_id=" + store.get("fp_id"));
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		
		//执行修改前的其它逻辑
		handlerService.handler("PreFeePlease", "save", "before", new Object[]{formStore});
		//修改PreFeePlease
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PreFeePlease", "fp_id");
		baseDao.execute(formSql);
		//修改PreFeePleaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PreFeePleaseDetail", "fpd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("fpd_id") == null || s.get("fpd_id").equals("") || s.get("fpd_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PreFeePleaseDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PreFeePleaseDetail", new String[]{"fpd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.updateByCondition("PreFeePlease", "fp_pleaseamount="+baseDao.getFieldDataByCondition("PreFeePleasedetail", "sum(fpd_total)", "fpd_fpid="+store.get("fp_id")), "fp_id="+store.get("fp_id"));
		// 记录操作
		baseDao.logger.update("PreFeePlease", "fp_id", store.get("fp_id"));
		//执行修改后的其它逻辑
		handlerService.handler("PreFeePlease", "save", "after", new Object[]{formStore});
	}

	@Override
	public void submitPreFeePlease(int fp_id) {
		Object status = baseDao.getFieldDataByCondition("PreFeePlease", "fp_statuscode", "fp_id=" + fp_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		//执行提交前的其它逻辑
		handlerService.beforeSubmit("PreFeePlease", fp_id);
		// 执行提交操作
		baseDao.submit("PreFeePlease", "fp_id=" + fp_id, "fp_status", "fp_statuscode");
		// 记录操作
		baseDao.logger.submit("PreFeePlease", "fp_id", fp_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit("PreFeePlease", fp_id);
	}

	@Override
	public void resSubmitPreFeePlease(int fp_id) {
		Object status = baseDao.getFieldDataByCondition("PreFeePlease", "fp_statuscode", "fp_id=" + fp_id);
		if(!status.equals("COMMITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交操作
		baseDao.resOperate("PreFeePlease", "fp_id=" + fp_id, "fp_status", "fp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("PreFeePlease", "fp_id", fp_id);
		handlerService.afterResSubmit("PreFeePlease", fp_id);
	}

	@Override
	public void auditPreFeePlease(int fp_id) {
		Object status = baseDao.getFieldDataByCondition("PreFeePlease", "fp_statuscode", "fp_id=" + fp_id);
		if(!status.equals("COMMITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		//执行审核前的其它逻辑
		handlerService.handler("PreFeePlease", "audit", "before", new Object[]{fp_id});
		// 执行审核操作
		baseDao.audit("PreFeePlease", "fp_id=" + fp_id, "fp_status", "fp_statuscode", "fp_auditdate", "fp_auditman");
		// 记录操作
		baseDao.logger.audit("PreFeePlease", "fp_id", fp_id);
		//执行审核后的其它逻辑
		handlerService.handler("PreFeePlease", "audit", "after", new Object[]{fp_id});
	}

	@Override
	public void resAuditPreFeePlease(int fp_id) {
		Object status = baseDao.getFieldDataByCondition("PreFeePlease", "fp_statuscode", "fp_id=" + fp_id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		baseDao.resOperate("PreFeePlease", "fp_id=" + fp_id, "fp_status", "fp_statuscode");
		// 记录操作
		baseDao.logger.resAudit("PreFeePlease", "fp_id", fp_id);
	}

	@Override
	public int turnFYBX(int fp_id,
			String caller) {		
		Object[]  data=baseDao.getFieldsDataByCondition("prefeeplease left join employee on fp_pleaseman=em_name", new String[]{"em_name","em_depart","fp_code","fp_v13","fp_pleaseamount"}, "fp_id="+fp_id);//取原表单的录入人作为出差费用申请的申请人
		Object[] feedata=baseDao.getFieldsDataByCondition("FeePlease", new String[]{"fp_code","fp_id"}, "fp_sourcekind='费用申请' and fp_sourcecode='"+data[2]+"'");
		if(feedata!=null){//如果feeplease中存在记录，则报错
			BaseUtil.showError("转入失败,此费用申请已存在费用报销,单号为:" + "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease.jsp?whoami=FeePlease!FYBX&formCondition=fp_idIS"
					+ feedata[1] + "&gridCondition=fpd_fpidIS" + feedata[1] + "')\">" + feedata[0] + "</a>");
		}
		int id=baseDao.getSeqId("FeePlease_seq");
		String code=baseDao.sGetMaxNumber("FeePlease!FYBX", 2);
		String insertSql="insert into FeePlease(fp_code,fp_pleaseman,fp_department,fp_status,fp_recordman,fp_kind,fp_recorddate,fp_sourcecode,fp_sourcekind,fp_id,fp_statuscode,fp_v13,fp_pleaseamount,fp_v6,fp_v9,fp_v8,fp_v3,fp_remark,fp_v7)" +
				" select '"+code+"',fp_pleaseman,fp_department,'在录入',fp_recordman,'费用报销单',sysdate,fp_code,'费用申请',"+id+",'ENTERING',fp_v13,fp_pleaseamount,fp_v6,fp_v9,fp_v8,fp_v3,fp_remark,'未支付' from prefeeplease where fp_id="+fp_id+"";
		baseDao.execute(insertSql);
		String insertDetSql="insert into FeePleasedetail (fpd_detno,fpd_d1,fpd_n7,fpd_n8,fpd_d3,fpd_id,fpd_fpid,fpd_total) " +
				"select fpd_detno,fpd_d1,fpd_n7,fpd_n8,fpd_d3,FeePleasedetail_seq.nextval,"+id+",fpd_total from preFeepleasedetail where fpd_fpid="+fp_id;
		baseDao.execute(insertDetSql);
		baseDao.updateByCondition("prefeeplease", "fp_v7='已转'", "fp_id="+fp_id);			
		return id;
	}

}
