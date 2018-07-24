package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.FormAttachDao;
import com.uas.erp.model.FormAttach;
import com.uas.erp.service.hr.TrainmaterailService;

@Service
public class TrainmaterailServiceImpl implements TrainmaterailService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private FormAttachDao formAttachDao;

	@Override
	public void saveTrainmaterail(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String filesId = null;
		if(store.containsKey("files")){
			filesId = (String)store.get("files");
			store.remove("files");
		}
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Trainmaterail", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存附件
		if(filesId != null){
			String[] files = filesId.split(",");
			int id = Integer.parseInt(store.get("tm_id").toString());
			String path = null;
			FormAttach attach = null;
			for(String file:files){
				if(file != null && !file.equals("")){
					Object res = baseDao.getFieldDataByCondition("filepath", "fp_path", "fp_id=" + Integer.parseInt(file));
					if(res != null){
						path = (String)res;
						attach = new FormAttach();
						attach.setFa_caller("Trainmaterail");
						attach.setFa_keyvalue(id);
						attach.setFa_path(path);
						formAttachDao.saveAttach(attach);
					}
				}
			}
		}
		try{
			//记录操作
			baseDao.logger.save(caller, "tm_id", store.get("tm_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateTrainmaterailById(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Trainmaterail", "tm_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "tm_id", store.get("tm_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
	}

	@Override
	public void deleteTrainmaterail(int tm_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{tm_id});
		//删除
		baseDao.deleteById("Trainmaterail", "tm_id", tm_id);
		//记录操作
		baseDao.logger.delete(caller, "tm_id", tm_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{tm_id});
	}

}
