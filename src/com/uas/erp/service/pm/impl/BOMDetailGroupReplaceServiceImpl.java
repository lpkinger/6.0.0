package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.BOMDetailGroupReplaceService;


@Service("BOMDetailGroupReplaceService")
public class BOMDetailGroupReplaceServiceImpl implements BOMDetailGroupReplaceService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveBOMDetailGroupReplace(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave("BOMDetailGroupReplace", new Object[]{formStore,gstore});
		/*//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(formStore, "BOMDetailGroup", new String[]{}, new Object[]{});
		baseDao.execute(formSql);*/
		//保存Detail
		Object[] bgr_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			bgr_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				bgr_id[i] = baseDao.getSeqId("BOMDETAILGROUPREPLACE_SEQ");
			}
		} else {
			bgr_id[0] = baseDao.getSeqId("BOMDETAILGROUPREPLACE_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "BOMDetailGroupReplace", "bgr_id", bgr_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bdg_id", store.get("bdg_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave("BOMDetailGroupReplace",new Object[]{formStore});
	}
	
	@Override
	public void deleteBOMDetailGroupReplace(int bdg_id, String caller) {	
		//执行删除前的其它逻辑
		handlerService.beforeDel("BOMDetailGroupReplace",new Object[]{bdg_id});
		/*//删除
		baseDao.deleteById("BOMDetailGroup", "bdg_id", bdg_id);*/
		//删除Detail
		baseDao.deleteById("BOMDetailGroupReplace", "bgr_groupid", bdg_id);
		/*//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.delete", language), 
				BaseUtil.getLocalMessage("msg.deleteSuccess", language), "BOMDetail|bdg_id=" + bdg_id));*/
		//执行删除后的其它逻辑
		handlerService.afterDel("BOMDetailGroupReplace",new Object[]{bdg_id});
	}
	
	@Override
	public void updateBOMDetailGroupReplaceById(String formStore, String gridStore, String caller) {
		//Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		//执行修改前的其它逻辑
		handlerService.beforeUpdate("BOMDetailGroupReplace",new Object[]{formStore, gstore});
		/*//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(formStore, "BOMDetailGroup", "bdg_id");
		baseDao.execute(formSql);*/
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "BOMDetailGroupReplace", "bgr_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("bgr_id") == null || s.get("bgr_id").equals("") ||s.get("bgr_id").equals("null")
					|| s.get("bgr_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("BOMDETAILGROUPREPLACE_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BOMDetailGroupReplace", new String[]{"bgr_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		/*//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.update", language), 
				BaseUtil.getLocalMessage("msg.updateSuccess", language), "BOMDetailGroupReplace|bdg_id=" + store.get("bdg_id")));*/
		//执行修改后的其它逻辑
		handlerService.afterUpdate("BOMDetailGroupReplace", new Object[]{formStore,gstore});
	}
}
