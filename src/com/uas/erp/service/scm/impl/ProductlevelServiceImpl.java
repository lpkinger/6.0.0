package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.ProductlevelService;

@Service
public class ProductlevelServiceImpl implements ProductlevelService {
	
	static final String getPurchasetype = "select * from Purchasekind ";
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductlevel(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[]{store, grid});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Productlevel", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "Purchasetypedetail", "pd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "pl_id", store.get("pl_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, grid});
	}

	@Override
	public void updateProductlevelById(String formStore, String gridStore, String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Productlevel", "pl_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		if(gridStore!=null){
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "Purchasetypedetail", "pd_id");
			for(Map<Object, Object> s:gstore){
				if(s.get("pd_id") == null || s.get("pd_id").equals("") || s.get("pd_id").toString().equals("0")){//新添加的数据，id不存在
					int id = baseDao.getSeqId("Purchasetypedetail_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "Purchasetypedetail", new String[]{"pd_id"}, new Object[]{id});
					gridSql.add(sql);
				}
			}
			baseDao.execute(gridSql);
		}
		
		//记录操作
		baseDao.logger.save(caller, "pl_id", store.get("pl_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}

	@Override
	public void deleteProductlevel(int pl_id, String caller) {		
		//只能删除状态为[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Productlevel", "pl_statuscode", "pl_id=" + pl_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, pl_id);
		baseDao.delCheck("Productlevel", pl_id);
		baseDao.deleteById("Productlevel", "pl_id", pl_id); 
		baseDao.deleteById("Purchasetypedetail", "pd_plid", pl_id);
		//记录操作
		baseDao.logger.delete(caller, "pl_id", pl_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, pl_id);
	}

	@Override
	public void auditProductlevel(int pl_id, String caller) {		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Productlevel", "pl_statuscode", "pl_id=" + pl_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{pl_id});
		//执行审核操作
		baseDao.audit("Productlevel", "pl_id=" + pl_id, "pl_status", "pl_statuscode", "pl_auditdate", "pl_auditor");
		//记录操作
		baseDao.logger.audit(caller, "pl_id", pl_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{pl_id});
	}

	@Override
	public void resAuditProductlevel(int pl_id, String caller) {		
		Object status = baseDao.getFieldDataByCondition("Productlevel", "pl_statuscode", "pl_id=" + pl_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Productlevel", "pl_id=" + pl_id, "pl_status", "pl_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "pl_id", pl_id);
	}

	@Override
	public void submitProductlevel(int pl_id, String caller) {		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Productlevel", "pl_statuscode", "pl_id=" + pl_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{pl_id});
		//执行提交操作
		baseDao.submit("Productlevel", "pl_id=" + pl_id, "pl_status", "pl_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "pl_id", pl_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{pl_id});
	}

	@Override
	public void resSubmitProductlevel(int pl_id, String caller) {		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Productlevel", "pl_statuscode", "pl_id=" + pl_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[]{pl_id});
		//执行反提交操作
		baseDao.resOperate("Productlevel", "pl_id=" + pl_id, "pl_status", "pl_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "pl_id", pl_id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{pl_id});
	}

	@Override
	public void updatePurchasetypedetail(int id, String griddata, String caller) {
		JSONArray jsonArray = JSONArray.fromObject(griddata);
		JSONObject jsonObject = new JSONObject();
		int detno=0;
		int i,j=0;
		String purchaseType = null;
		int count = baseDao.getCountByTable("PurchaseKind");
		List<String> sqls = new ArrayList<String>();
		String insertSql = null;
		String[] type = new String [jsonArray.size()];
		if(count != jsonArray.size()){
			for(i=0;i<jsonArray.size();i++){
				jsonObject = jsonArray.getJSONObject(i);
				type[i] = jsonObject.getString("pd_billtype");
				if(jsonObject.getInt("pd_detno")>detno){
					detno = jsonObject.getInt("pd_detno");
				}
			}
			SqlRowList rs = baseDao.queryForRowSet(getPurchasetype);
			while(rs.next()){
				purchaseType = rs.getString("pk_name");
				for(j=0;j<type.length;j++){
					if(purchaseType.equals(type[j])){
						break;
					}
				}
				if(type.length==0){
					detno = detno+1;
					insertSql = "insert into Purchasetypedetail(PD_ID,PD_DETNO,PD_PLID,PD_BILLTYPE)values('"+
							baseDao.getSeqId("Purchasetypedetail_SEQ")+"','"+detno+"','"+id+"','"+purchaseType+"')";
					sqls.add(insertSql);
				}else if(j == type.length&&!purchaseType.equals(type[j-1])){
					detno = detno+1;
					insertSql = "insert into Purchasetypedetail(PD_ID,PD_DETNO,PD_PLID,PD_BILLTYPE)values('"+
							baseDao.getSeqId("Purchasetypedetail_SEQ")+"','"+detno+"','"+id+"','"+purchaseType+"')";
					sqls.add(insertSql);
				}
			}
			baseDao.execute(sqls);
		}
	}
}
