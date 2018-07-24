package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.OADocumentCatalogService;
import com.uas.erp.service.oa.OADocumentPowerService;

@Service
public class OADocumentCatalogServiceImpl implements OADocumentCatalogService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private OADocumentPowerService oaDocumentPowerService;
	@Override
	@CacheEvict(value="document",allEntries=true)
	public void save(String save, String  caller) {
		//保存
		Object[] id = new Object[1];
		if(save.contains("},")){//明细行有多行数据哦
			String[] datas = save.split("},");
			id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				id[i] = baseDao.getSeqId("DOCUMENTCATALOG_SEQ");
			}
		} else {
			id[0] = baseDao.getSeqId("DOCUMENTCATALOG_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(save, "DocumentCatalog", "dc_id", id);
		oaDocumentPowerService.save(save, "DocumentPower", "dcp_id", id, caller);
		baseDao.execute(gridSql);
		try{
			for(Object o:id){
				//记录操作
				baseDao.logger.save(caller, "dc_id", o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@CacheEvict(value="document",allEntries=true)
	public void update(String update, String  caller) {
		//修改
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(update, "DocumentCatalog", "dc_id");
		oaDocumentPowerService.update(update, "DocumentPower", "dcp_id",caller);
		baseDao.execute(gridSql);
		//记录操作
		try{
			List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(update);
			for(Map<Object, Object> map:store){
				//记录操作
				baseDao.logger.update(caller, "dc_id", map.get("dc_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@CacheEvict(value="document",allEntries=true)
	public void delete(int id, String  caller) {
		//删除
		deleteChilds(id);
		oaDocumentPowerService.delete(id, caller);
		//记录操作
		try{
			baseDao.logger.delete(caller, "dc_id", id);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void deleteChilds(int id){
		baseDao.deleteByCondition("DocumentCatalog", "dc_deleteable='T' AND dc_id=" + id);
		//判断是否有子元素
		boolean bool = baseDao.checkByCondition("DocumentCatalog", "dc_parentid=" + id);
		if(!bool){
			List<Object> objs = baseDao.getFieldDatasByCondition("DocumentCatalog", "dc_id", "dc_parentid=" + id);
			for(Object obj:objs){
				deleteChilds(Integer.parseInt("" + obj));
			}
		}
	}
}
