package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.pm.BOMBatchBackService;



@Service("BOMBatchBackService")
public class BOMBatchBackServiceImpl implements BOMBatchBackService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	
	@Override
	public void cleanBOMBatchBack(int em_id,String caller) {
		baseDao.deleteByCondition("BomMutiBack", "bm_emid='"+em_id+"'");
	}
	
	public void bomBack(String gridStore,String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Employee employee = SystemSession.getUser();		
		String rep = CollectionUtil.getRepeats(gstore, "bm_prcode");
		if(rep != null){//重复行
			BaseUtil.showError("物料："+rep+"出现多行");
		}
		List<String> gridSql = new ArrayList<String>();
		gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "BomMutiBack","bm_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("bm_id") == null || s.get("bm_id").equals("") || s.get("bm_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("BomMutiBack_SEQ");
				s.put("bm_emid", employee.getEm_id());
				String sql = SqlUtil.getInsertSqlByMap(s, "BomMutiBack", new String[] { "bm_id" },
						new Object[] { id });				
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);		
		for (Map<Object, Object> s : gstore) {
			String res = baseDao.callProcedure("MM_BOMMUTIBACK", new Object[]{s.get("bm_prcode"),employee.getEm_id()});
			if(res != null && !res.trim().equals("")){
				BaseUtil.showError(res);
			}	
		}		
	}
	
	@Override
	@Transactional
	public void updateBOMBatchBackById(String formStore, String param,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
	    handlerService.beforeSave("BomMutiBack", new Object[] { store,gstore});
		SqlRowList  rs0;
		
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(param, "BomMutiBack", "bm_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("bm_id") == null || s.get("bm_id").equals("") || s.get("bm_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("BomMutiBack_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BomMutiBack", new String[] { "bm_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql); 
		Employee employee = SystemSession.getUser();
		// 记录操作  
		baseDao.logger.update(caller, "em_id", store.get("em_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("BomMutiBack", new Object[] {store, gstore});
		
		rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bm_prcode) prcode from (select bm_prcode,count(1) nn from BomMutiBack where bm_emid='" + employee.getEm_id()+ "' group by bm_prcode) where nn>1 ");
		if (rs0.next()) {
			if (rs0.getInt("num")>0){
				BaseUtil.showErrorOnSuccess("物料："+rs0.getString("prcode")+"出现多行");
			} 
		}		
	}

}
