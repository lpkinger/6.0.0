package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.OareceiveService;

@Service
public class OareceiveServiceImpl implements OareceiveService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveOareceive(String formStore, String gridStore,
			String  caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller,new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Oareceive", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		Object[] od_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			od_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				od_id[i] = baseDao.getSeqId("OareceiveDETAIL_SEQ");
			}
		} else {
			od_id[0] = baseDao.getSeqId("OareceiveDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "OareceiveDetail", "od_id", od_id);
		baseDao.execute(gridSql);
		String totalSql="update OareceiveDetail set od_total=od_price*od_num where od_osid="+store.get("or_id");
		baseDao.execute(totalSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "or_id", store.get("or_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store,gstore});
		
	}

	@Override
	public void updateOareceiveById(String formStore, String gridStore,
			String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Oareceive", "or_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "OareceiveDetail", "od_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("od_id") == null || s.get("od_id").equals("") || s.get("od_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("OareceiveDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "OareceiveDetail", new String[]{"od_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		String totalSql="update OareceiveDetail set od_total=od_price*od_num where od_osid="+store.get("or_id");
		baseDao.execute(totalSql);
		//记录操作
		baseDao.logger.update(caller, "or_id", store.get("or_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
		
	}

	@Override
	public void deleteOareceive(int or_id, String caller) {
		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{or_id});
		//删除purchase
		baseDao.deleteById("Oareceive", "or_id", or_id);
		//删除purchaseDetail
		baseDao.deleteById("Oareceivedetail", "od_osid", or_id);
		//记录操作
		baseDao.logger.delete(caller, "or_id", or_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{or_id});

	}

	@Override
	public void auditOareceive(int or_id, String  caller) {
		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Oareceive", "or_statuscode", "or_id=" + or_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{or_id});
		//执行审核操作
		baseDao.audit("Oareceive", "or_id=" + or_id, "or_status", "or_statuscode", "or_auditdate", "or_auditor");
		//记录操作
		baseDao.logger.audit(caller, "or_id", or_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{or_id});
		
	}

	@Override
	public void resAuditOareceive(int or_id, String caller) {
		
		Object status = baseDao.getFieldDataByCondition("Oareceive", "or_statuscode", "or_id=" + or_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Oareceive", "or_id=" + or_id, "or_status", "or_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "or_id", or_id);
		
	}

	@Override
	public void submitOareceive(int or_id, String caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Oareceive", "or_statuscode", "or_id=" + or_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{or_id});
		//执行提交操作
		baseDao.submit("Oareceive", "or_id=" + or_id, "or_status", "or_statuscode");
		//getOaapplication(or_id, griddata);
		//记录操作
		baseDao.logger.submit(caller, "or_id", or_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[]{or_id});
		
	}

	@Override
	public void resSubmitOareceive(int or_id, String caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Oareceive", "or_statuscode", "or_id=" + or_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Oareceive", "or_id=" + or_id, "or_status", "or_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "or_id", or_id);
		
	}

	@Override
	public void getOaapplication(int or_id, String griddata,String  caller){
		
		/*Object status = baseDao.getFieldDataByCondition("Oareceive", "or_getapp", "or_id=" + or_id);
		if(!status.equals("0")){
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.Oareceive.getapplication"));
		}*/
		Object status = baseDao.getFieldDataByCondition("Oareceive", "or_inoutstatuscode", "or_id=" + or_id);
		if (status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		List<String> sqls = new ArrayList<String>();
		String sql = "update Oareceive set or_getapp='1',or_inoutstatus='已过账',or_inoutstatuscode='POSTED' where or_id='"+or_id+"'";
		sqls.add(sql);		
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = new JSONObject();
		String gridSql = null;
		String getnum;
		StringBuffer sb = new StringBuffer();
		int i,j = 0;
		for (i = 0; i<grid.size();i++){
			gridjson = grid.getJSONObject(i);
			getnum = "select os_totalnum from oainstorage where os_procode='"+gridjson.getString("od_procode")+"'";
			j = baseDao.getCount(getnum);
 			if ( j < gridjson.getInt("od_num")) {
 				sb.append(BaseUtil.getLocalMessage("oa.appliance.getoaapplication"));
 				sb.append(gridjson.get("od_procode"));
				j = -1;
				break;
			}
			gridSql = "update oainstorage set os_totalnum=os_totalnum-"+gridjson.getString("od_num")+" where os_procode='"+gridjson.getString("od_procode")+"'";
		sqls.add(gridSql);
		}	
		if (j == -1) {
			BaseUtil.showError(sb.toString());
		} else {
			baseDao.execute(sqls);
		}
	}

	@Override
	@Transactional
	public void returnOaapplication(int or_id, String griddata,
			String  caller) {
		Object status = baseDao.getFieldDataByCondition("Oareceive", "or_inoutstatuscode", "or_id=" + or_id);
		if (status.equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyPost"));
		}
		List<String> sqls = new ArrayList<String>();
		String sql = "update Oareceive set or_getapp='0',or_inoutstatus='未过账',or_inoutstatuscode='UNPOST' where or_id='"+or_id+"'";
		sqls.add(sql);		
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = null;
		String gridSql = null;
		for (int i = 0; i<grid.size();i++){
			gridjson = grid.getJSONObject(i);
			gridSql = "update oainstorage set os_totalnum=os_totalnum+"+gridjson.getString("od_num")+" where os_procode='"+gridjson.getString("od_procode")+"'";
			sqls.add(gridSql);
		}	
		baseDao.execute(sqls);
	}

	@Override
	public String[] printOareceive(int or_id, String  caller, String reportName, String condition) {
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		/*
		baseDao.updateByCondition("Oapurchase",
				"pu_printstatuscode='PRINTED',pu_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"op_id=" + op_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.print"),
				BaseUtil.getLocalMessage("msg.printSuccess"), "Oapurchase|pu_id=" + op_id));
		// 执行打印后的其它逻辑
		handlerService.handler("Oapurchase", "print", "after", new Object[] { op_id });
		*/
		return keys;
	}
	
}
