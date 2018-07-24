package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.NGReasonService;

@Service("ngReasonService")
public class NGReasonServiceImpl implements NGReasonService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveNGReason(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("QUA_NGReason", "nr_code='" + store.get("nr_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave("NGReason", new Object[]{store});
		//保存QUA_NGReason
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "QUA_NGReason", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save("NGReason", "nr_id", store.get("nr_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("NGReason", new Object[]{store});	
	}

	@Override
	public void updateNGReasonById(String formStore, String gridStore) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		//执行修改前的其它逻辑
		handlerService.beforeSave("NGReason", new Object[]{store});
		//修改QUA_NGReason
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "QUA_NGReason", "nr_id");
		baseDao.execute(formSql);	
		baseDao.logger.update("NGReason", "nr_id", store.get("nr_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("NGReason", new Object[]{store});	
	}

	@Override
	public void deleteNGReason(int nr_id) {
		//执行删除前的其它逻辑
		handlerService.beforeDel("NGReason", nr_id);
		//删除QUA_NGReason
		baseDao.deleteById("QUA_NGReason", "nr_id", nr_id);
		//记录操作
		baseDao.logger.delete("NGReason", "nr_id", nr_id);
		//执行保存后的其它逻辑
		handlerService.afterDel("NGReason", nr_id);	
	}

	@Override
	public void printNGReason(int nr_id) {
		//执行打印前的其它逻辑
		handlerService.beforePrint("NGReason", nr_id);
		//执行打印操作
		//TODO
		//记录操作
		baseDao.logger.print("NGReason", "nr_id", nr_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint("NGReason", nr_id);
	}
}
