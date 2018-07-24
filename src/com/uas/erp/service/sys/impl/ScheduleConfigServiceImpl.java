package com.uas.erp.service.sys.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.sys.ScheduleConfigService;

@Service
public class ScheduleConfigServiceImpl implements ScheduleConfigService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	/**
	 * 新增定时任务
	 */
	@Override
	public void save(String caller, String formStore){
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(baseDao.checkIf("SYS_SCHEDULETASK", "code_ = '" + store.get("code_") + "'")){
			BaseUtil.showError("当前编号已存在");
		}
		if(baseDao.checkIf("SYS_SCHEDULETASK", "bean_ = '" + store.get("bean_") + "' and function_ = '" + store.get("function_") + "'")){
			BaseUtil.showError("定时任务已存在(类、方法相同)");
		}
		check(store);
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		//保存//账套检测
		String sql = SqlUtil.getInsertSqlByFormStore(store, "SYS_SCHEDULETASK", new String[]{}, new Object[]{});
		baseDao.execute(sql);
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}
	
	/**
	 * 修改定时任务
	 */
	@Override
	public void update(String caller, String formStore){
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		check(store);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SYS_SCHEDULETASK", "id_");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "id_", store.get("id_"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	/**
	 * 删除定时任务
	 */
	@Override
	public void deleteDocSetting(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		baseDao.delCheck("SYS_SCHEDULETASK", id);
		// 删除
		baseDao.deleteById("SYS_SCHEDULETASK", "id_", id);
		// 记录操作
		baseDao.logger.delete(caller, "id_", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}
	//检测数据
	private void check(Map<Object, Object> store){
		Object master = store.get("master_");
		if(!StringUtils.isEmpty(master)){ //检测账套
			String defualtSob = BaseUtil.getXmlSetting("defaultSob");
			String[] masterArray = master.toString().toUpperCase().split(",");
			for (int i = 0; i < masterArray.length; i++) {
				if(!baseDao.checkIf(defualtSob+".master", " ma_user = '"+masterArray[i]+"' ")){
					BaseUtil.showError("当前填写账套信息有误！");
				}
			}
		}else{
			BaseUtil.showError("请补充适用账套");
		}
	}
	
}
