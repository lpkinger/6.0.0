package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.hr.TurnfullmembService;

@Service
public class TurnfullmembServiceImpl implements TurnfullmembService {
	
	@Autowired
	private HandlerService handlerService;

	@Autowired
	private BaseDao baseDao;
	
	@Override
	public void saveTurnfullmemb(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Turnfullmemb", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	////保存TurnfullmembDetail
		Object[] td_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			td_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				td_id[i] = baseDao.getSeqId("TurnfullmembDETAIL_SEQ");
			}
		} else {
			td_id[0] = baseDao.getSeqId("TurnfullmembDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "TurnfullmembDetail", "td_id", td_id);
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.save(caller, "tf_id", store.get("tf_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateTurnfullmembById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller,new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Turnfullmemb", "tf_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "TurnfullmembDetail", "td_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("td_id") == null || s.get("td_id").equals("") || s.get("td_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("TurnfullmembDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "TurnfullmembDetail", new String[]{"td_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "tf_id", store.get("tf_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store,gstore});
	}

	@Override
	public void deleteTurnfullmemb(int tf_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, tf_id);
		//删除purchase
		baseDao.deleteById("Turnfullmemb", "tf_id", tf_id);
		//删除purchaseDetail
		baseDao.deleteById("Turnfullmembdetail", "td_tfid", tf_id);
		//记录操作
		baseDao.logger.delete(caller, "tf_id", tf_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, tf_id);
	}

	@Override
	public void auditTurnfullmemb(int tf_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Turnfullmemb", "tf_statuscode", "tf_id=" + tf_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,tf_id);
		//执行审核操作
		baseDao.audit("Turnfullmemb", "tf_id=" + tf_id, "tf_status", "tf_statuscode", "tf_auditdate", "tf_auditor");
		baseDao.updateByCondition("employee", "em_class='正式',em_zzdate=sysdate", "em_code in (select td_code from Turnfullmembdetail where td_tfid=" + 
				tf_id + ")");
		Object masters = baseDao.getFieldDataByCondition("Master", "wm_concat(ma_user)", " ma_name<>'" + SystemSession.getUser().getEm_master()+"'");
		if(masters!=null){
			Object ids = baseDao.getFieldDataByCondition("employee left join Turnfullmembdetail on em_code=td_code", "wm_concat(em_id)", "  td_tfid=" +tf_id);
			baseDao.callProcedure("SYS_POST", new Object[] { "Turnfullmemb!Post", SpObserver.getSp(), masters, ids.toString(), SystemSession.getUser().getEm_name(),
					SystemSession.getUser().getEm_id()  });
		}
		//记录操作
		baseDao.logger.audit(caller, "tf_id", tf_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,tf_id);
	}

	@Override
	public void resAuditTurnfullmemb(int tf_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Turnfullmemb", "tf_statuscode", "tf_id=" + tf_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Turnfullmemb", "tf_id=" + tf_id, "tf_status", "tf_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "tf_id", tf_id);
	}

	@Override
	public void submitTurnfullmemb(int tf_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Turnfullmemb", "tf_statuscode", "tf_id=" + tf_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, tf_id);
		//执行提交操作
		baseDao.submit("Turnfullmemb", "tf_id=" + tf_id, "tf_status", "tf_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "tf_id", tf_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, tf_id);
	}

	@Override
	public void resSubmitTurnfullmemb(int tf_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Turnfullmemb", "tf_statuscode", "tf_id=" + tf_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, tf_id);
		//执行反提交操作
		baseDao.resOperate("Turnfullmemb", "tf_id=" + tf_id, "tf_status", "tf_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "tf_id", tf_id);
		handlerService.afterResSubmit(caller, tf_id);
	}

	@Override
	public void vastZhuanz(String gridStore, String caller) {
		int tf_id=baseDao.getSeqId("Turnfullmemb_SEQ");
		String code=baseDao.sGetMaxNumber("Turnfullmemb", 2);
		String formSql="insert into Turnfullmemb(tf_id,tf_code,tf_recordor,tf_date,tf_status,tf_statuscode,tf_recordorid) values("+tf_id
				+",'"+code+"','"+SystemSession.getUser().getEm_name()+"',"+DateUtil.parseDateToOracleString(null, new Date())+",'在录入','ENTERING',"+SystemSession.getUser().getEm_id()+")";
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<String> gridSql=new ArrayList<String>();
		int detno=1;
		for(Map<Object, Object> store:gstore){
			StringBuffer sb=new StringBuffer();
			sb.append("insert into Turnfullmembdetail(td_detno,td_code,td_name,td_sex,td_depart,td_position,td_date,td_id,td_tfid) VALUES(");
			sb.append(detno+",'"+store.get("em_code")+"','"+store.get("em_name")+"','"+store.get("em_sex")+"','"+store.get("em_depart")+"','"
			+store.get("em_defaulthsname")+"',"+DateUtil.parseDateToOracleString(null, store.get("em_indate")+"")+","+baseDao.getSeqId("TurnfullmembDETAIL_SEQ")
			+","+tf_id+")");
			gridSql.add(sb.toString());
			detno++;
		}
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.save(caller, "tf_id", tf_id);
	}
}
