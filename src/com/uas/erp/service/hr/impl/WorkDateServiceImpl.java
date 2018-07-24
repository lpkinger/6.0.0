package com.uas.erp.service.hr.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.WorkDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WorkDateServiceImpl implements WorkDateService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveWorkDate(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WorkDate", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "wd_id", store.get("wd_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});

	}

	@Override
	public void updateWorkDateById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		String[] f = {"wd_ondutyone","wd_ondutytwo","wd_ondutythree", "wd_offdutyone", "wd_offdutytwo","wd_offdutythree","wd_offend1","wd_offbeg1","wd_onend1",
				"wd_onbeg1","wd_offend2","wd_offbeg2", "wd_onend2", "wd_onbeg2","wd_offend3","wd_offbeg3","wd_onend3","wd_onbeg3"}; 
		for(int i=0;i<f.length;i++){;
			if(store.get(f[i])==null){
				store.put(f[i], "");
			}
		}
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WorkDate", "wd_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "wd_id", store.get("wd_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});

	}

	@Override
	public void deleteWorkDate(int wd_id, String  caller) {

		handlerService.beforeDel(caller,new Object[]{wd_id});
		//删除
		baseDao.deleteById("WorkDate", "wd_id", wd_id);
		//记录操作
		baseDao.logger.delete(caller, "wd_id", wd_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{wd_id});
		
	}

    @Override
    public void setEmpWorkDate(int wdid, String condition, String caller) {
        baseDao.updateByCondition("employee", "em_wddefaultid=" + wdid, condition);
    }

}
