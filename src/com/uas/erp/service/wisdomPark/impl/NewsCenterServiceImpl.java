package com.uas.erp.service.wisdomPark.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.wisdomPark.NewsCenterService;

@Service("newsCenterService")
public class NewsCenterServiceImpl implements NewsCenterService{
	
	@Autowired BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	
	@Override
	public void deleteNewsType(String caller, int id) {
		// 只能删除没有发布的新闻的新闻类型
		boolean bool = baseDao.checkIf("NewsType", "nt_id = " + id +" and nvl(nt_count,0) > 0 ");
		if (bool) {
			BaseUtil.showError("该新闻类型存在已发布新闻，不能删除！");
		}
		
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		
		// 删除NewsCenter
		baseDao.deleteById("NewsType", "nt_id", id);
		
		// 记录操作
		baseDao.logger.delete(caller, "nt_id", id);
		
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, id);
		
	}

	private void setImage(Map<Object, Object> store){
		if(!StringUtil.hasText(store.get("nc_image"))){
			store.put("nc_image", baseDao.getFieldDataByCondition("NewsType", "nt_image", "nt_id = " + store.get("nc_ntid")));
		}
		
		if(!StringUtil.hasText(store.get("nc_status"))||"垃圾箱".equals(store.get("nc_status"))){
			store.put("nc_status", "草稿箱");
		}
		
		store.put("nc_update", DateUtil.format(new Date(), Constant.YMD_HMS));
	}
	
	//保存新闻
	public void saveNews(String caller, String formStore){	
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		
		if(!StringUtil.hasText(store.get("nc_id"))){
			store.put("nc_id", baseDao.getSeqId("NEWSCENTER_SEQ"));
		}
		
		setImage(store);
		
		//处理超长字符
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		Object value = null;
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		// 保存nc_id
		String formSql = SqlUtil.getInsertSqlByMap(store, "NewsCenter");
		baseDao.execute(formSql);
		baseDao.saveClob("NewsCenter", clobFields, clobStrs, "nc_id=" + store.get("nc_id"));
		// 记录操作
		baseDao.logger.save(caller, "nc_id", store.get("nc_id"));
		
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}
	
	//更新新闻
	public void updateNews(String caller, String formStore){	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		// 只能修改未发布的新闻!
		/*boolean bool = baseDao.checkIf("NewsCenter", "nc_id = " + store.get("nc_id")+" and nvl(nc_status,'草稿箱') = '已发布'");
		if (bool) {
			BaseUtil.showError("新闻已发布，不能修改！");
		}*/
		
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		
		setImage(store);
		
		//处理超长字符
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		Object value = null;
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		
		// 修改NewsCenter
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "NewsCenter", "nc_id");
		baseDao.execute(formSql);
		baseDao.saveClob("NewsCenter", clobFields, clobStrs, "nc_id=" + store.get("nc_id"));
		// 记录操作
		baseDao.logger.update(caller, "nc_id", store.get("nc_id"));
		
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
	
	//删除新闻
	public void deleteNews(String caller, int id){
		
		// 只能删除未发布的新闻!
		boolean bool = baseDao.checkIf("NewsCenter", "nc_id = " + id +" and nvl(nc_status,'草稿箱') = '已发布'");
		if (bool) {
			BaseUtil.showError("新闻已发布，不能删除！");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		
		// 删除NewsCenter
		baseDao.deleteById("NewsCenter", "nc_id", id);
		
		// 记录操作
		baseDao.logger.delete(caller, "nc_id", id);
		
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, id);
	}
	
	//发布新闻
	public void publishNews(String caller, int id){	
		
		boolean bool = baseDao.checkIf("NewsCenter", "nc_id = " + id +" and nvl(nc_status,'草稿箱') = '已发布'");
		if (bool) {
			BaseUtil.showError("新闻已发布，不用重复发布！");
		}
		
		Object ntid = baseDao.getFieldDataByCondition("NewsCenter", "nc_ntid", "nc_id = "+id);
		bool = baseDao.checkByCondition("NewsType", "nt_id ="+ntid);
		if (bool) {
			BaseUtil.showError("该新闻类型不存在！");
		}
		
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition("NewsCenter", "nc_status = '已发布',nc_readnum = 0,nc_publisher = '"+employee.getEm_name()+"',nc_publishdate = "+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "nc_id = " + id);
		
		//更新新闻数量
		baseDao.updateByCondition("NewsType", "nt_count = nvl(nt_count,0)+1", "nt_id = "+ntid);
		
		//记录日志
		baseDao.logger.others("发布新闻", "发布成功", caller, "nc_id", id);
	}
	
	@Override
	public void cancelNews(String caller, int id) {
		boolean bool = baseDao.checkIf("NewsCenter", "nc_id = " + id +" and nvl(nc_status,'草稿箱') <> '已发布'");
		if (bool) {
			BaseUtil.showError("新闻未发布，不用重复撤销！");
		}
		
		baseDao.updateByCondition("NewsCenter", "nc_status = '垃圾箱',nc_publisher = '',nc_publishdate = ''", "nc_id = " + id);
		
		//更新新闻数量
		Object ntid = baseDao.getFieldDataByCondition("NewsCenter", "nc_ntid", "nc_id = "+id);
		baseDao.updateByCondition("NewsType", "nt_count = nvl(nt_count,0)-1", "nt_id = "+ntid);
		
		//记录日志
		baseDao.logger.others("取消新闻", "取消成功", caller, "nc_id", id);
		
	}

	@Override
	public String getNewsHtml(int id) {
		return baseDao.getFieldValue("NewsCenter", "nc_content", "nc_id = " + id +" and nvl(nc_status,'草稿箱') = '已发布'", String.class);
	}

}
