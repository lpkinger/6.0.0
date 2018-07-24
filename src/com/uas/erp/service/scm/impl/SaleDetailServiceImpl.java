package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.SaleDetailService;

@Service
public class SaleDetailServiceImpl implements  SaleDetailService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void updateSaleDetailDetById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("SaleDetail", "sd_statuscode", "sd_id=" + store.get("sd_id"));
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改SaleDetail
		store.remove("pr_detail");
		store.remove("pr_spec");
		store.remove("pr_unit");
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SaleDetail", "sd_id");
		baseDao.execute(formSql);
		//修改SaleDetailDet
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "SaleDetailDet", "sdd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("sdd_id") == null || s.get("sdd_id").equals("") || s.get("sdd_id").equals("0") ||
					Integer.parseInt(s.get("sdd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("SALEDETAILDET_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "SaleDetailDet", new String[]{"sdd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "sd_id", store.get("sd_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});		
	}
}
