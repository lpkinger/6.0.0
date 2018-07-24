package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.EmployeeFeeService;

@Service
public class EmployeeFeeServiceImpl implements EmployeeFeeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveEmployeeFee(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count = baseDao
				.getCount("select count(1) from EmployeeFee where ef_emcode='"
						+ store.get("ef_emcode") + "'");
		if (count != 0) {
			BaseUtil.showError("此员工已存在薪资记录!");
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[] {store });
		store.put("ef_detno", baseDao.getFieldDataByCondition("EmployeeFee",
				"max(ef_detno)+1", "1=1"));
		store.put("ef_emfid", 1);
		//store.put("ef_id", baseDao.getSeqId("EmployeeFee_seq"));
		// 保存EmployeeFee
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "EmployeeFee",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ef_id", store.get("ef_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[] {store });
	}

	@Override
	public void deleteEmployeeFee(int ef_id, String  caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ef_id });
		// 删除EmployeeFee
		baseDao.deleteById("EmployeeFee", "ef_id", ef_id);
		// 记录操作
		baseDao.logger.delete(caller, "ef_id", ef_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { ef_id });
	}

	@Override
	public void updateEmployeeFee(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] {store });
		// 修改EmployeeFee
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "EmployeeFee",
				"ef_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "ef_id", store.get("ef_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] {	store });
	}

	@Override
	public void updateBatchAssistRequire(String formStore, String gridStore,
			String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] {store,gstore});
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
			"EmployeeFee", "ef_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ef_id") == null || s.get("ef_id").equals("") || s.get("ef_id").toString().equals("0")){//新添加的数据，id不存在
				Object ef_id=baseDao.getFieldDataByCondition("EmployeeFee", "ef_id", "ef_emcode='"+s.get("ef_emcode")+"'");
				if (ef_id != null) {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "EmployeeFee", "ef_id"));//存在则更新
				}else{
					int id = baseDao.getSeqId("EmployeeFee_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "EmployeeFee", new String[]{"ef_id"}, new Object[]{id});
					gridSql.add(sql);
				}
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ef_id", store.get("ef_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store,gstore});
	}

}
