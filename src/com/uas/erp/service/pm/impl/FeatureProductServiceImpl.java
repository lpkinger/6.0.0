package com.uas.erp.service.pm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.FeatureProductService;


@Service
public class FeatureProductServiceImpl implements FeatureProductService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveFeatureProduct(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("FeatureProduct", "fp_code='" + store.get("fp_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave("FeatureProduct",new Object[]{store,grid});		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FeatureProduct", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存Detail
		
		for(Map<Object, Object> map:grid){
			map.put("fpd_id", baseDao.getSeqId("FEATUREPRODUCTDETAIL_SEQ"));
			map.put("fpd_fpid", store.get("fp_id"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "FeatureProductDetail");
		baseDao.execute(gridSql); 
		baseDao.execute("update FeatureProduct set fp_description=(select wm_concat(fpd_fecode||'|'||fpd_fevaluecode) from (select fpd_fecode,fpd_fevaluecode from FeatureProductDetail where fpd_fpid=?)) where fp_id=?", store.get("fp_id"), store.get("fp_id"));
		baseDao.execute("update FeatureProduct set fp_description2=(select wm_concat(fpd_fename||'|'||fpd_fevalue) from (select fpd_fename,fpd_fevalue from FeatureProductDetail where fpd_fpid=?)) where fp_id=?", store.get("fp_id"), store.get("fp_id"));
		try{
			//记录操作
			baseDao.logger.save(caller, "fp_id", store.get("fp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave("FeatureProduct",new Object[]{store,grid});
	}
	
	@Override
	public void deleteFeatureProduct(int fp_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("FeatureProduct", "fp_statuscode", "fp_id=" + fp_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel("FeatureProduct",new Object[]{fp_id});		//删除
		baseDao.deleteById("FeatureProduct", "fp_id", fp_id);
		//删除Detail
		baseDao.deleteById("FeatureProductdetail", "fpd_fpid", fp_id);
		//记录操作
		baseDao.logger.delete(caller, "fp_id", fp_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("FeatureProduct",new Object[]{fp_id});
	}
	
	@Override
	public void deleteDetail(int fpd_id, String caller) {
		baseDao.deleteById("FeatureProductdetail", "fpd_id", fpd_id);
	}
	
	@Override
	public void updateFeatureProductById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("FeatureProduct", "fp_statuscode", "fp_id=" + store.get("fp_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave("FeatureProduct", new Object[]{store});		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FeatureProduct", "fp_id");
		baseDao.execute(formSql);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "FeatureProductDetail", "fpd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("fpd_id") == null || s.get("fpd_id").equals("") || s.get("fpd_id").equals("0") || 
					Integer.parseInt(s.get("fpd_id").toString()) == 0){//新添加的数据，id不存在
				s.put("fpd_id", baseDao.getSeqId("FEATUREPRODUCTDETAIL_SEQ"));
				s.put("fpd_fpid", store.get("fp_id"));
				s.put("fpd_code", store.get("fp_code"));
				s.put("fpd_prodcode", store.get("fp_prodcode"));
				String sql = SqlUtil.getInsertSqlByMap(s, "FeatureProductDetail", new String[]{}, new Object[]{});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql); 
		baseDao.execute("update FeatureProduct set fp_description=(select wm_concat(fpd_fecode||'|'||fpd_fevaluecode) from (select fpd_fecode,fpd_fevaluecode from FeatureProductDetail where fpd_fpid=?)) where fp_id=?", store.get("fp_id"), store.get("fp_id"));
		baseDao.execute("update FeatureProduct set fp_description2=(select wm_concat(fpd_fename||'|'||fpd_fevalue) from (select fpd_fename,fpd_fevalue from FeatureProductDetail where fpd_fpid=?)) where fp_id=?", store.get("fp_id"), store.get("fp_id"));
		//记录操作
		baseDao.logger.update(caller, "fp_id", store.get("fp_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave("FeatureProduct", new Object[]{store});
	}
	
	@Override
	public void auditFeatureProduct(int fp_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FeatureProduct", "fp_statuscode", "fp_id=" + fp_id);
		StateAssert.auditOnlyCommited(status);
		baseDao.execute("update FeatureProduct set fp_description=(select wm_concat(fpd_fecode||'|'||fpd_fevaluecode) from (select fpd_fecode,fpd_fevaluecode from FeatureProductDetail where fpd_fpid=?)) where fp_id=?", fp_id, fp_id);
		baseDao.execute("update FeatureProduct set fp_description2=(select wm_concat(fpd_fename||'|'||fpd_fevalue) from (select fpd_fename,fpd_fevalue from FeatureProductDetail where fpd_fpid=?)) where fp_id=?", fp_id, fp_id);
		//执行审核前的其它逻辑
		handlerService.beforeAudit("FeatureProduct",new Object[]{fp_id});		//执行审核操作
		baseDao.audit("FeatureProduct", "fp_id=" + fp_id, "fp_status", "fp_statuscode", "fp_auditdate", "fp_auditman");
		//记录操作
		baseDao.logger.audit(caller, "fp_id", fp_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit("FeatureProduct",new Object[]{fp_id});
	}
	
	@Override
	public void resAuditFeatureProduct(int fp_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FeatureProduct", "fp_statuscode", "fp_id=" + fp_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.updateByCondition("FeatureProduct", "fp_statuscode='ENTERING',fp_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "fp_id=" + fp_id);
		//记录操作
		baseDao.logger.resAudit(caller, "fp_id", fp_id);
	}
	
	@Override
	public void submitFeatureProduct(int fp_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FeatureProduct", "fp_statuscode", "fp_id=" + fp_id);
		StateAssert.submitOnlyEntering(status);
		baseDao.execute("update FeatureProduct set fp_description=(select wm_concat(fpd_fecode||'|'||fpd_fevaluecode) from (select fpd_fecode,fpd_fevaluecode from FeatureProductDetail where fpd_fpid=?)) where fp_id=?", fp_id, fp_id);
		baseDao.execute("update FeatureProduct set fp_description2=(select wm_concat(fpd_fename||'|'||fpd_fevalue) from (select fpd_fename,fpd_fevalue from FeatureProductDetail where fpd_fpid=?)) where fp_id=?", fp_id, fp_id);
		checkAll(fp_id);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit("FeatureProduct",new Object[]{fp_id});		//执行提交操作
		baseDao.submit("FeatureProduct", "fp_id=" + fp_id, "fp_status", "fp_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "fp_id", fp_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit("FeatureProduct",new Object[]{fp_id});
	}
	
	@Override
	public void resSubmitFeatureProduct(int fp_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FeatureProduct", "fp_statuscode", "fp_id=" + fp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("FeatureProduct",new Object[]{fp_id});		//执行反提交操作
		baseDao.updateByCondition("FeatureProduct", "fp_statuscode='ENTERING',fp_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "fp_id=" + fp_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "fp_id", fp_id);
		handlerService.afterResSubmit("FeatureProduct",new Object[]{fp_id});
	}
	
	/**
	 * FeatureProduct整批导入
	 */
	@Override
	@Transactional
	public void batchSaveFeatureProduct(String bom, String detail, String caller) {
		List<Map<Object, Object>> bomGrid = BaseUtil.parseGridStoreToMaps(bom);//FeatureProduct
		List<Map<Object, Object>> detailGrid = BaseUtil.parseGridStoreToMaps(detail);//FeatureProductDetail
		int id = 0;
		Map<Object, Integer> codes = new HashMap<Object, Integer>();//母件编号与母件ID的映射map
		for(Map<Object, Object> b:bomGrid){
			id = baseDao.getSeqId("FeatureProduct_SEQ");
			if(!codes.containsKey(b.get("fp_prodcode"))){
				codes.put(b.get("fp_prodcode"), id);
			}
			b.put("fp_id", id);
			b.put("fp_date", DateUtil.currentDateString(null));
			b.put("fp_status", BaseUtil.getLocalMessage("ENTERING"));
			b.put("fp_statuscode", "ENTERING");
			b.put("fp_recordman", SystemSession.getUser().getEm_name());
		}
		List<String> sqls = SqlUtil.getInsertSqlbyGridStore(bomGrid, "FeatureProduct");
		baseDao.execute(sqls);
		//子件按母件编号分组
		Map<Object, List<Map<Object, Object>>> details = BaseUtil.groupMap(detailGrid, "fpd_prodcode");
		int detno = 1;
		for(Object obj:details.keySet()){
			if(codes.containsKey(obj)){
				id = codes.get(obj);
				detno = 1;
				int fpdid = 0;
				for(Map<Object, Object> d:details.get(obj)){
					d.put("fpd_fpid", id);
					fpdid = baseDao.getSeqId("FEATUREPRODUCTDETAIL_SEQ");
					d.put("fpd_id", fpdid);
					d.put("bd_detno", detno++);
				}
				sqls = SqlUtil.getInsertSqlbyGridStore(details.get(obj), "FeatureProductDetail");
				baseDao.execute(sqls);
			}
		}
	}
	
	@Override
	public void deleteAllDetail(int id, String caller) {
		baseDao.deleteByCondition("FeatureProductDetail", "fpd_fpid=" + id);
		
	}
	 
	@Override
	public List<Object[]> getList(String tablename, String[] field,
			String condition, String caller) {
		return baseDao.getFieldsDatasByCondition(tablename, field, condition);
	}
	public void checkAll(int id){
		SqlRowList sl;
		baseDao.execute("update featureproductdetail set fpd_fevaluecode=(select max(fd_valuecode) from featuredetail where fd_code=fpd_fecode and fd_value=fpd_fevalue) where fpd_fpid=" + id );
		sl = baseDao.queryForRowSet("select fpd_detno from featureproductdetail where  fpd_fpid=" + id+" and NVL(fpd_fevaluecode,' ')=' ' " );
		if (sl.next()) {
			BaseUtil.showError("特征值编码不能为空，序号[" + sl.getString("fpd_detno")+ "]");
		} 
		baseDao.execute("update FeatureProduct set fp_description=(select wm_concat(fpd_fecode||'|'||fpd_fevaluecode) from (select fpd_fecode,fpd_fevaluecode from FeatureProductDetail where fpd_fpid=?)) where fp_id=?", id, id);
		sl = baseDao.queryForRowSet("select fpd_fecode,count(1)num from featureproductdetail where  fpd_fpid=" + id+" group by fpd_fecode order by num desc " );
		if (sl.next()) {
			if(sl.getInt("num")>1){
				BaseUtil.showError("特征编号不能重复录入，特征编号[" + sl.getString("fpd_fecode")+ "]");
			} 
		}
	} 
}
