package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.PurcRemarkService;

@Service("purcRemarkService")
public class PurcRemarkServiceImpl implements PurcRemarkService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void savePurcRemark(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PurcRemark", "pr_code='" + store.get("pr_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, grid});
		//保存PurcRemark
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PurcRemark", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存PurcRemarkDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "PurcRemarkDetail", "prd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "pr_id", store.get("pr_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, grid});
	}
	@Override
	public void deletePurcRemark(int pr_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{pr_id});
		int count = baseDao.getCountByCondition("Purchase", "pu_remarkcode=(select pr_code from purcremark where pr_id="+pr_id+")");
		if(count > 0){
			BaseUtil.showError("已被采购单使用过，不允许删除!");
		}
		//删除PurcRemark
		baseDao.deleteById("PurcRemark", "pr_id", pr_id);
		//删除PurcRemarkDetail
		baseDao.deleteById("PurcRemarkdetail", "prd_prid", pr_id);
		//记录操作
		baseDao.logger.delete(caller, "pr_id", pr_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{pr_id});
	}
	
	@Override
	public void updatePurcRemarkById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改PurcRemark
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PurcRemark", "pr_id");
		baseDao.execute(formSql);
		//修改PurcRemarkDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PurcRemarkDetail", "prd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("prd_id") == null || s.get("prd_id").equals("") || s.get("prd_id").equals("0") ||
					Integer.parseInt(s.get("prd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PURCREMARKDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PurcRemarkDetail", new String[]{"prd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}
	@Override
	public void bannedPurcRemark(int pr_id, String caller) {
		baseDao.banned("PurcRemark", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		baseDao.logger.banned(caller, "pr_id", pr_id);
	}
	@Override
	public void resBannedPurcRemark(int pr_id, String caller) {
		baseDao.updateByCondition("PurcRemark", "pr_status=null, pr_statuscode=null", "pr_id=" + pr_id);
		baseDao.logger.resBanned(caller, "pr_id", pr_id);
	}
}
