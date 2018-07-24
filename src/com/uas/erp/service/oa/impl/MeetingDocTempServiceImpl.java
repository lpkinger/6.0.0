package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.MeetingDocTempService;

@Service
public class MeetingDocTempServiceImpl implements MeetingDocTempService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMeetingDocTemp(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(baseDao.getCount("select count(1) from MeetingDocTemp where mt_name='"+store.get("mt_name")+"'")>0){
			BaseUtil.showError("此模板名称已存在，请修改后重试！");
		}
		int count=baseDao.getCount("select count(1) from MeetingDocTemp where mt_code='"+store.get("mt_code")+"'");		
		if(count!=0){
			BaseUtil.showError("此模板编号已存在！");
		}
		handlerService.beforeSave(caller, new Object[] { store });
		// html是clob字段，特殊处理
		String html = store.get("mt_html").toString();
		store.remove("mt_html");
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"MeetingDocTemp", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.saveClob("MeetingDocTemp", "mt_html", html,
				"mt_id=" + store.get("mt_id"));
		// 记录操作
		baseDao.logger.save(caller, "mt_id", store.get("mt_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteMeetingDocTemp(int mt_id, String caller) {
		int count1=baseDao.getCount("select count(1) from MeetingDoc where md_mtname=(select mt_name from MeetingDocTemp where mt_id='"+mt_id+"')");		
		if(count1!=0){
			BaseUtil.showError("此模板已被使用，不能删除！");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { mt_id });
		// 删除purchase
		baseDao.deleteById("MeetingDocTemp", "mt_id", mt_id);
		// 记录操作
		baseDao.logger.delete(caller, "mt_id", mt_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { mt_id });

	}

	@Override
	public void updateMeetingDocTemp(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(baseDao.getCount("select count(1) from MeetingDocTemp where mt_id<>"+store.get("mt_id")+" and mt_name='"+store.get("mt_name")+"'")>0){
			BaseUtil.showError("此模板名称已存在，请修改后重试！");
		}
		if(baseDao.getCount("select count(1) from MeetingDocTemp where mt_id<>"+store.get("mt_id")+" and mt_code='"+store.get("mt_code")+"'")>0){
			BaseUtil.showError("此模板编号已存在，请修改后重试！");
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// html是clob字段，特殊处理
		String html = store.get("mt_html").toString();
		store.remove("mt_html");
		// 修改Meetingroomapply
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"MeetingDocTemp", "mt_id");
		baseDao.execute(formSql);
		baseDao.saveClob("MeetingDocTemp", "mt_html", html,
				"mt_id=" + store.get("mt_id"));
		// 记录操作
		baseDao.logger.update(caller, "mt_id", store.get("mt_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}

}
