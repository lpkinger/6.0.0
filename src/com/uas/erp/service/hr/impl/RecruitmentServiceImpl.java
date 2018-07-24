package com.uas.erp.service.hr.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.hr.RecruitmentService;

@Service
public class RecruitmentServiceImpl implements RecruitmentService {
	
	static final String formSql = "insert into recruitplan(rp_code,rp_recordorid," +
			"rp_recordor,rp_date,rp_id,rp_statuscode,rp_status,rp_sourcecode) values(?,?,?,?,?,?,?,?)";
    static final String gridSql = "insert into recruitplandetail(rd_detno,rd_hrorg," +
    		"rd_depart,rd_position,rd_num,rd_rpid,rd_id) values(?,?,?,?,?,?,?)"; 
    
    static final String update ="update Recruitment set re_isturn='1' where re_id=?";
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveRecruitment(String formStore, String gridStore,String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		if(!store.get("re_isturn").toString().equals("0")){
			store.put("re_isturn", "0");
			formStore = BaseUtil.parseMap2Str(store);
		}
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Recruitment", 
				new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	////保存RecruitmentDetail
		Object[] rd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			rd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				rd_id[i] = baseDao.getSeqId("RecruitmentDETAIL_SEQ");
			}
		} else {
			rd_id[0] = baseDao.getSeqId("RecruitmentDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "RecruitmentDetail",
				"rd_id", rd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "re_id", store.get("re_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}

	@Override
	public void updateRecruitmentById(String formStore, String gridStore,String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Recruitment", "re_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "RecruitmentDetail", "rd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("rd_id") == null || s.get("rd_id").equals("") || s.get("rd_id").toString().equals("0")
					){//新添加的数据，id不存在
				int id = baseDao.getSeqId("RecruitmentDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "RecruitmentDetail", new String[]{"rd_id"}, 
						new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "re_id", store.get("re_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}

	@Override
	public void deleteRecruitment(int re_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{re_id});
		//还原来源转单状态
		baseDao.execute("update demandplandetail set dd_isturn=0 where dd_id in (select rd_sourceid from Recruitmentdetail where rd_reid=?)", re_id);
		//删除Recruitment
		baseDao.deleteById("Recruitment", "re_id", re_id);
		//删除RecruitmentDetail
		baseDao.deleteById("Recruitmentdetail", "rd_reid", re_id);
		//记录操作
		baseDao.logger.delete(caller, "re_id", re_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{re_id});
	}

	@Override
	public void auditRecruitment(int re_id, String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Recruitment", "re_statuscode", "re_id=" + re_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{re_id});
		//执行审核操作
		baseDao.audit("Recruitment", "re_id=" + re_id, "re_status", "re_statuscode", "re_auditdate", "re_auditor");
		//记录操作
		baseDao.logger.audit(caller, "re_id", re_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{re_id});
	}

	@Override
	public void resAuditRecruitment(int re_id, String caller) {
		
		Object[] t = baseDao.getFieldsDataByCondition("Recruitment", new String[]{"re_statuscode","re_isturn"}, "re_id=" + re_id);
		StateAssert.resAuditOnlyAudit(t[0]);
		if(t[1].equals("1")){
			BaseUtil.showError("该用人申请单已转招聘计划，不允许反审核！");
		}
		System.out.println(t);
		//执行反审核操作
		baseDao.resAudit("Recruitment", "re_id=" + re_id, "re_status", "re_statuscode", "re_auditdate", "re_auditor");
		//记录操作
		baseDao.logger.resAudit(caller, "re_id", re_id);
	}

	@Override
	public void submitRecruitment(int re_id, String  caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Recruitment", "re_statuscode", "re_id=" + re_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{re_id});
		//执行提交操作
		baseDao.submit("Recruitment", "re_id=" + re_id, "re_status", "re_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "re_id", re_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{re_id});
	}

	@Override
	public void resSubmitRecruitment(int re_id, String caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Recruitment", "re_statuscode", "re_id=" + re_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Recruitment", "re_id=" + re_id, "re_status", "re_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "re_id", re_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { re_id });
	}

	@Override
	public String turnRecruitplan(String formdata,
			String griddata,String caller) {
		
		JSONObject formJson = JSONObject.fromObject(formdata);
		JSONArray gridJsonArray = JSONArray.fromObject(griddata);
		JSONObject gridJson = new JSONObject();
		String code = baseDao.sGetMaxNumber("Careerapply", 2);
		int reId = formJson.getInt("re_id");
		int id = baseDao.getSeqId("recruitplan_SEQ");
		try {
			boolean bool = baseDao.execute(formSql, new Object[]{code,SystemSession.getUser().getEm_id(),
					SystemSession.getUser().getEm_name(),Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),id,"ENTERING","在录入",formJson.getString("re_code")});
			if (bool) {
				for(int i=0;i<gridJsonArray.size();i++){
					gridJson = gridJsonArray.getJSONObject(i);
					baseDao.execute(gridSql, new Object[]{i+1,gridJson.get("rd_hrorg"),gridJson.get("rd_depart"),
							gridJson.get("rd_position"),gridJson.get("rd_demandper"),id,baseDao.getSeqId("recruitplandetail_SEQ")});
				}
			}
			baseDao.execute(update, new Object[]{reId});
			baseDao.logger.turn(BaseUtil.getLocalMessage("msg.turnRecruitment"), caller, "re_id", reId);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
		}
		
		return
		"转入成功,招聘计划单号:" + "<a href=\"javascript:openUrl('jsps/hr/emplmana/recruitment/recruitplan.jsp?formCondition=rp_idIS" + id
				+ "&gridCondition=rd_rpidIS" + id + "&whoami=Recruitplan')\">" + code + "</a>";
		
	
	}
}
