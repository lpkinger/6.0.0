package com.uas.erp.service.oa.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.OATaskService;
@Service
public class OATaskServiceImpl implements OATaskService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveOATask(String formStore, String gridStore,
			String gridStore2, String gridStore3, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(gridStore3);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[] { store});
		//处理OATaskDetail
		for(Map<Object, Object> s:grid){
			s.put("id", baseDao.getSeqId("ProjectTask_SEQ"));
			s.put("class", "OATask");
			if("".equals(s.get("taskcode")+"")){
				s.put("taskcode", baseDao.sGetMaxNumber("ProjectTask", 2));
			}
		}
		//处理OATaskRecord
		for(Map<Object, Object> s:grid2){
			s.put("wr_id", baseDao.getSeqId("workrecord_SEQ"));
			s.put("wr_taskname", store.get("name"));
			s.put("wr_recorddate", DateUtil.parseDateToString(new Date(), null));
			s.put("wr_recorder", SystemSession.getUser().getEm_name());
			s.put("wr_status", "ENTERING");
			s.put("wr_statuscode", "在录入");
			s.put("wr_type", "OATaskRecord");
			if("".equals(s.get("wr_code")+"")){
				s.put("wr_code", baseDao.sGetMaxNumber("workrecord", 2));
			}
		}
		//处理OATaskChange
		for(Map<Object, Object> s:grid3){
			s.put("ptc_id", baseDao.getSeqId("ProjectTaskChange_SEQ"));
			s.put("ptc_status", "ENTERING");
			s.put("ptc_statuscode", "在录入");
			s.put("ptc_recorddate", DateUtil.parseDateToString(new Date(), null));
			s.put("ptc_proposer",  SystemSession.getUser().getEm_name());
			s.put("ptc_type", "OATaskChange");
			if("".equals(s.get("ptc_code")+"")){
				s.put("ptc_code", baseDao.sGetMaxNumber("ProjectTaskChange", 2));
			}
		}
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"ProjectTask", new String[] {}, new Object[] {});
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ProjectTask");
		List<String> gridSql2 = SqlUtil.getInsertSqlbyGridStore(grid2, "workrecord");
		List<String> gridSql3 = SqlUtil.getInsertSqlbyGridStore(grid3, "ProjectTaskChange");
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		baseDao.execute(gridSql2);
		baseDao.execute(gridSql3);
		// 记录操作
		baseDao.logger.save(caller, "id", store.get("id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[] { store});
	}

	@Override
	public void deleteOATask(int id, String  caller) {
		// 执行删除前的其它逻辑
		        handlerService.beforeDel(caller, new Object[] { id});
				// 删除purchase
				baseDao.deleteById("ProjectTask", "id", id);
				baseDao.deleteById("ProjectTask", "parentid", id);
				baseDao.deleteById("workrecord", "wr_taskid", id);
				baseDao.deleteById("ProjectTaskChange", "ptc_oldtaskid", id);
				// 记录操作
				baseDao.logger.delete(caller, "id", id);
				// 执行删除后的其它逻辑
				handlerService.afterDel(caller, new Object[] { id});
	}

	@Override
	public void updateOATask(String formStore, String gridStore,
			String gridStore2, String gridStore3, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(gridStore3);
		List<String> gridSql1 = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"ProjectTask", "id");
		List<String> gridSql2 = SqlUtil.getUpdateSqlbyGridStore(gridStore2,
				"workrecord", "wr_id");
		List<String> gridSql3 = SqlUtil.getUpdateSqlbyGridStore(gridStore3,
				"ProjectTaskChange", "ptc_id");
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store});
		//处理OATaskDetail
		for(Map<Object, Object> s:grid){
			if (s.get("id") == null || s.get("id").equals("")
					|| s.get("id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ProjectTask_SEQ");
				if("".equals(s.get("taskcode")+"")){
					s.put("taskcode", baseDao.sGetMaxNumber("ProjectTask", 2));
				}
				s.put("class","OATask");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProjectTask",
						new String[] { "id" }, new Object[] { id });
				gridSql1.add(sql);
			}
		}
		//处理OATaskRecord
		for(Map<Object, Object> s:grid2){
			if (s.get("wr_id") == null || s.get("wr_id").equals("")
					|| s.get("wr_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("workrecord_SEQ");
				if("".equals(s.get("wr_code")+"")){
					s.put("wr_code", baseDao.sGetMaxNumber("workrecord", 2));
				}
				s.put("wr_type", "OATaskRecord");
				s.put("wr_taskname", store.get("name"));
				s.put("wr_recorddate", DateUtil.parseDateToString(new Date(), null));
				s.put("wr_recorder", SystemSession.getUser().getEm_name());
				s.put("wr_status", "ENTERING");
				s.put("wr_statuscode", "在录入");
				String sql = SqlUtil.getInsertSqlByMap(s, "workrecord",
						new String[] { "wr_id" }, new Object[] { id });
				gridSql2.add(sql);
			}
		}
		//处理OATaskChange
		for(Map<Object, Object> s:grid3){
			if (s.get("ptc_id") == null || s.get("ptc_id").equals("")
					|| s.get("ptc_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ProjectTaskChange_SEQ");
				if("".equals(s.get("ptc_code")+"")){
					s.put("ptc_code", baseDao.sGetMaxNumber("ProjectTaskChange", 2));
				}
				s.put("ptc_type", "OATaskChange");
				s.put("ptc_status", "ENTERING");
				s.put("ptc_statuscode", "在录入");
				s.put("ptc_recorddate", DateUtil.parseDateToString(new Date(), null));
				s.put("ptc_proposer", SystemSession.getUser().getEm_name());
				String sql = SqlUtil.getInsertSqlByMap(s, "ProjectTaskChange",
						new String[] { "ptc_id" }, new Object[] { id });
				gridSql3.add(sql);
			}
		}
		// 执行保存操作
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProjectTask", "id");
		baseDao.execute(formSql);
		baseDao.execute(gridSql1);
		baseDao.execute(gridSql2);
		baseDao.execute(gridSql3);
		// 记录操作
		baseDao.logger.update(caller, "id", store.get("id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store});
	}

}
