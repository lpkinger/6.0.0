package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.BOMCodeCalculateService;


@Service("BOMCodeCalculateService")
public class BOMCodeCalculateServiceImpl implements BOMCodeCalculateService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveBOMCodeCalculate(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BOM", "bo_code='" + store.get("bo_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_bocodeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave("BOMCodeCalculate", new Object[]{store,grid});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOM", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存Detail
		Object[] bd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			bd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				bd_id[i] = baseDao.getSeqId("BOMDETAIL_SEQ");
			}
		} else {
			bd_id[0] = baseDao.getSeqId("BOMDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "BOMDetail", "bd_id", bd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bo_id", store.get("bo_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave("BOMCodeCalculate", new Object[]{store,grid});
	}
	@Override
	public void deleteBOMCodeCalculate(int bo_id, String caller) {
		//只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel("BOMCodeCalculate", new Object[]{bo_id});
		//删除
		baseDao.deleteById("BOM", "bo_id", bo_id);
		//删除Detail
		baseDao.deleteById("BOMdetail", "bd_bomid", bo_id);
		//记录操作
		baseDao.logger.delete(caller, "bo_id",bo_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("BOMCodeCalculate", new Object[]{bo_id});
	}
	
	@Override
	public void updateBOMCodeCalculateById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + store.get("bo_id"));
		StateAssert.updateOnlyEntering(status);
		//更新采购计划下达数\本次下达数\状态
		//purchaseDao.updatePurchasePlan(Integer.parseInt((String)store.get("pu_id")));
		//执行修改前的其它逻辑
		handlerService.beforeUpdate("BOMCodeCalculate",new Object[]{store,gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOM", "bo_id");
		baseDao.execute(formSql);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "BOMDetail", "bd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("bd_id") == null || s.get("bd_id").equals("") || s.get("bd_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("BOMDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BOMDetail", new String[]{"bd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "bo_id",store.get("bo_id"));
		//更新上次采购价格、供应商
		//purchaseDao.updatePrePurchase((String)store.get("pu_code"), (String)store.get("pu_date"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate("BOMCodeCalculate",new Object[]{store});
	}
	
	@Override
	public void auditBOMCodeCalculate(int bo_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit("BOMCodeCalculate",new Object[]{bo_id});
		//执行审核操作
		baseDao.audit("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode", "bo_auditdate", "bo_auditman");
		//记录操作
		baseDao.logger.audit(caller, "bo_id", bo_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit("BOMCodeCalculate",new Object[]{bo_id});
	}
	@Override
	public void resAuditBOMCodeCalculate(int bo_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "bo_id", bo_id);
	}
	@Override
	public void submitBOMCodeCalculate(int bo_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit("BOMCodeCalculate", new Object[]{bo_id});
		//执行提交操作
		baseDao.submit("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "bo_id", bo_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit("BOMCodeCalculate", new Object[]{bo_id});
	}
	@Override
	public void resSubmitBOMCodeCalculate(int bo_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("BOMCodeCalculate",new Object[]{bo_id});
		//执行反提交操作
		baseDao.resOperate("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "bo_id", bo_id);
		handlerService.afterResSubmit("BOMCodeCalculate", new Object[]{bo_id});
	}
}
