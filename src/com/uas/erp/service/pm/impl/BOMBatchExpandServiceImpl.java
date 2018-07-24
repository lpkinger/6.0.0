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
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.BOMBatchExpandService;



@Service("BOMBatchExpandService")
public class BOMBatchExpandServiceImpl implements BOMBatchExpandService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	
	@Override
	public void cleanBOMBathExpand(int em_id,String caller) {
		baseDao.deleteByCondition("BOMBATCH", "bb_emid='"+em_id+"'");
		//删除展开结果
		Employee employee = SystemSession.getUser();
		baseDao.execute("delete from bombatchstruct where bbs_emid="+employee.getEm_id());
	}
	@Override
	public void bomExpand(int em_id, String gridStore,String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);		
		String rep = CollectionUtil.getRepeats(gstore, "bb_prodcode");
		if(rep != null){//重复行
			BaseUtil.showError("物料："+rep+"出现多行");
		}
		List<String> gridSql = new ArrayList<String>();
		gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "BOMBATCH","bb_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("bb_id") == null || s.get("bb_id").equals("") || s.get("bb_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("BOMBATCH_SEQ");
				s.put("bb_emid", em_id);
				String sql = SqlUtil.getInsertSqlByMap(s, "BOMBATCH", new String[] { "bb_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);		
		String res = baseDao.callProcedure("MM_BATCHBOMSTRUCT", new Object[]{em_id});
		if(res != null && !res.trim().equals("")){
			BaseUtil.showError(res);
		}		
	}
	
	@Override
	@Transactional
	public void updateBOMBatchExpandById(String formStore, String param,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		handlerService.beforeSave("BOMBatchExpand", new Object[] { store,gstore});
		//判断是否有重复
		String rep = CollectionUtil.getRepeats(gstore, "bb_prodcode");
		if(rep != null){//重复行
			BaseUtil.showError("物料："+rep+"出现多行");
		}
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(param, "BOMBATCH", "bb_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("bb_id") == null || s.get("bb_id").equals("") || s.get("bb_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("BOMBATCH_SEQ");
				s.put("bb_emid", store.get("em_id"));
				String sql = SqlUtil.getInsertSqlByMap(s, "BOMBATCH", new String[] { "bb_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql); 
		//删除展开结果
		Employee employee = SystemSession.getUser();
		baseDao.execute("delete from bombatchstruct where bbs_emid="+employee.getEm_id());
		// 记录操作  
		baseDao.logger.update(caller, "em_id", store.get("em_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("BOMBatchExpand", new Object[] {store, gstore});
	}
	@Override
	public void bomStructAll(int em_id, String caller) { 
		String res = baseDao.callProcedure("MM_SETPRODBOMSTRUCT_ALL", new Object[]{em_id});
		if(res != null && !res.trim().equals("")){
			BaseUtil.showError(res);
		}
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "批量展开所有BOM","成功",caller+"|em_id=" + em_id));
		
	}
	@Override
	public String[] printBOMSet(int id,String reportName,String condition, String caller) { 
		String res = baseDao.callProcedure("MM_BATCHBOMSTRUCT", new Object[]{id});
		if(res != null && !res.trim().equals("")){
			BaseUtil.showError(res);
		}
		//执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		return keys;
	}
}
