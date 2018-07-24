package com.uas.erp.service.hr.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.model.Key;
import com.uas.erp.service.hr.DemandplanService;

@Service
public class DemandplanServiceImpl implements DemandplanService {
	
	
	static final String Turnrecruit = "insert into Recruitment(re_code,re_recordorid,re_recordor,re_date," +
			"re_status,re_statuscode,re_id)values(?,?,?,?,?,?,?)";

	static final String turndetail = "insert into Recruitmentdetail(rd_detno,rd_depart,rd_hrorg,rd_position," +
			"rd_demandper,rd_reid,rd_id)values(?,?,?,?,?,?,?)";
	
	static final String updateDemand = "update demandplan set dp_isturn ='1' where dp_id=?";
	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void saveDemandplan(String formStore, String gridStore,
			String  caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.afterSave(caller, new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Demandplan", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	////保存DemandplanDetail
		Object[] dd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			dd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				dd_id[i] = baseDao.getSeqId("DemandplanDETAIL_SEQ");
			}
		} else {
			dd_id[0] = baseDao.getSeqId("DemandplanDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "DemandplanDetail", "dd_id", dd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "dp_id", store.get("dp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}

	@Override
	public void updateDemandplanById(String formStore, String gridStore,
			String  caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Demandplan", "dp_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "DemandplanDetail", "dd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("dd_id") == null || s.get("dd_id").equals("") || s.get("dd_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("DemandplanDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "DemandplanDetail", new String[]{"dd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "dp_id", store.get("dp_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});

	}

	@Override
	public void deleteDemandplan(int dp_id, String  caller) {
		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{dp_id});
		//删除purchase
		baseDao.deleteById("Demandplan", "dp_id", dp_id);
		//删除purchaseDetail
		baseDao.deleteById("Demandplandetail", "dd_rpid", dp_id);
		//记录操作
		baseDao.logger.delete(caller, "dp_id", dp_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{dp_id});

	}

	@Override
	public void auditDemandplan(int dp_id, String  caller) {
		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Demandplan", "dp_statuscode", "dp_id=" + dp_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{dp_id});
		//执行审核操作
		baseDao.audit("Demandplan", "dp_id=" + dp_id, "dp_status", "dp_statuscode", "dp_auditdate", "dp_auditor");
		//记录操作
		baseDao.logger.audit(caller, "dp_id", dp_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{dp_id});
	}

	@Override
	public void resAuditDemandplan(int dp_id, String  caller) {
		Object status = baseDao.getFieldDataByCondition("Demandplan", "dp_statuscode", "dp_id=" + dp_id);
		StateAssert.resAuditOnlyAudit(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('行：'||dd_detno) from (select dd_id,dd_detno from demandplandetail where dd_rpid = "
								+ dp_id + " and exists (select rd_sourceid from Recruitmentdetail where rd_sourceid=dd_id))" , String.class);
		if (dets != null) {
			BaseUtil.showError("明细行已转用人申请单，不允许进行反审核操作！" + dets);
		}
		//执行反审核操作
		baseDao.resAudit("Demandplan", "dp_id=" + dp_id, "dp_status", "dp_statuscode", "dp_auditdate", "dp_auditor");
		//记录操作
		baseDao.logger.resAudit(caller, "dp_id", dp_id);
	}

	@Override
	public void submitDemandplan(int dp_id, String  caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Demandplan", "dp_statuscode", "dp_id=" + dp_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[]{dp_id});
		//执行提交操作
		baseDao.submit("Demandplan", "dp_id=" + dp_id, "dp_status", "dp_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "dp_id", dp_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[]{dp_id});
		
	}

	@Override
	public void resSubmitDemandplan(int dp_id, String caller) {		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Demandplan", "dp_statuscode", "dp_id=" + dp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, dp_id);
		//执行反提交操作
		baseDao.resOperate("Demandplan", "dp_id=" + dp_id, "dp_status", "dp_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "dp_id", dp_id);
		handlerService.afterResSubmit(caller, dp_id);
	}

	@Override
	public void demandTurn(String  caller, int id,
			String param) {	
		String recruitCode =  baseDao.sGetMaxNumber("Recruitment", 2);
		int recruitId = baseDao.getSeqId("Recruitment_SEQ");
		JSONArray gridJsonArray = JSONArray.fromObject(param);
		JSONObject gridJson = new JSONObject();
		int i=0;
		try {
			boolean bool = baseDao.execute(Turnrecruit,new Object[]{recruitCode,SystemSession.getUser().getEm_id(),SystemSession.getUser().getEm_name(),
					Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),BaseUtil.getLocalMessage("ENTERING"),
					"ENTERING",recruitId});
			if(bool){
				for(i=0;i<gridJsonArray.size();i++){
					gridJson = gridJsonArray.getJSONObject(i);
					baseDao.execute(turndetail, new Object[]{i+1,gridJson.getString("dd_hrorgdepart"),gridJson.getString("dd_hrorgname"),
							gridJson.getString("dd_hrorgpos"),gridJson.getString("rd_demandper"),recruitId,
							baseDao.getSeqId("Recruitmentdetail_SEQ")});
				}
				baseDao.execute(updateDemand,new Object[]{id});
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转单失败");
		}
		baseDao.logger.getMessageLog( BaseUtil.getLocalMessage("hr.turnRecruitment"), BaseUtil.getLocalMessage("hr.turnRecruitmentSuccess"), caller, "dp_id", id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String vastTurnRecruitment(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String log = null;
		if (maps.size() > 0) {
			int id = baseDao.getSeqId("CONTRACT_SEQ");
			String code = baseDao.sGetMaxNumber("Contract", 2);
			boolean bool = baseDao.execute(Turnrecruit,new Object[]{code,SystemSession.getUser().getEm_id(),SystemSession.getUser().getEm_name(),
					Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),BaseUtil.getLocalMessage("ENTERING"),
					"ENTERING",id});
			if (bool) {
				transferRepository.transfer(caller, maps, new Key(id, code));
				log = "转入成功,用人申请单号:" + "<a href=\"javascript:openUrl('jsps/hr/emplmana/recruitment/recruitment.jsp?formCondition=re_idIS" + id
					+ "&gridCondition=rd_reidIS" + id + "')\">" + code + "</a>&nbsp;";
				sb.append(log).append("<hr>");
				for (Map<Object, Object> map : maps) {
					baseDao.execute("update demandplandetail set dd_isturn=1 where dd_id=?",new Object[]{map.get("dd_id").toString()});
				}
			}
		}
		return sb.toString();
	}

}
