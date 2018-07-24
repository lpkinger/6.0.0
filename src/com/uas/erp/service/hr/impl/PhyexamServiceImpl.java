package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.PhyexamService;

@Service
public class PhyexamServiceImpl implements PhyexamService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePhyexam(String formStore, String gridStore,
			String caller) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller,new Object[]{store, gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Phyexam", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	////保存phyexamDetail
		Object[] pd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			pd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				pd_id[i] = baseDao.getSeqId("PHYEXAMDETAIL_SEQ");
			}
		} else {
			pd_id[0] = baseDao.getSeqId("PHYEXAMDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "PhyexamDetail", "pd_id", pd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ph_id", store.get("ph_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store, gstore});
	}

	@Override
	public void updatePhyexamById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store, gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Phyexam", "ph_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PhyexamDetail", "pd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pd_id") == null || s.get("pd_id").equals("") || s.get("pd_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PHYEXAMDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PhyexamDetail", new String[]{"pd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ph_id", store.get("ph_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store, gstore});
	}

	@Override
	public void deletePhyexam(int ph_id, String caller) {
		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{ph_id});
		//删除purchase
		baseDao.deleteById("Phyexam", "ph_id", ph_id);
		//删除purchaseDetail
		baseDao.deleteById("Phyexamdetail", "pd_phid", ph_id);
		//记录操作
		baseDao.logger.delete(caller, "ph_id", ph_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{ph_id});
	}
}
