package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.ReportFilesService;

@Service("ReportFilesService")
public class ReportFilesServiceImpl implements
		ReportFilesService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveReportFiles(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String cal= store.get("caller").toString();
		String rptName = store.get("file_name").toString();
		Object attachId = store.get("attach");
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ReportFiles",
				"caller='" + cal + "' and file_name=' "+ rptName+"'");
		if (!bool) {
			BaseUtil.showError("该报表名已经存在，请重命名报表名");
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil
				.getInsertSqlByMap(store, "ReportFiles");
		baseDao.execute(formSql);
		if (StringUtil.hasText(attachId)){
			attachId = attachId.toString().substring(0,
					attachId.toString().indexOf(";"));
			baseDao.execute("update reportfiles set file_path=nvl((select fp_path from filepath where fp_id="
					+ attachId
					+ "),file_path),last_modify='"
					+ SystemSession.getUser().getEm_name()
					+ "',modify_time=sysdate where caller='"
					+ cal + "' and file_name='" +rptName+ "'");
		}
		// 记录操作
		baseDao.logger.save(caller, "id", store.get("id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateReportFiles(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String cal= store.get("caller").toString();
		String rptName = store.get("file_name").toString();
		String attachId = store.get("attach").toString();
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 执行修改操作
		String sql = SqlUtil.getUpdateSqlByFormStore(store,
				"ReportFiles", "id");
		baseDao.execute(sql);
		if (StringUtil.hasText(attachId)){
			attachId = attachId.toString().substring(0,
					attachId.toString().indexOf(";"));
			baseDao.execute("update reportfiles set file_path=nvl((select fp_path from filepath where fp_id="
					+ attachId
					+ "),file_path),last_modify='"
					+ SystemSession.getUser().getEm_name()
					+ "',modify_time=sysdate where caller='"
					+ cal + "' and file_name='" +rptName+ "'");
		}
		
		// 记录操作
		baseDao.logger.update(caller, "id", store.get("id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteReportFiles(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { id });
		// 执行删除操作
		baseDao.deleteById("ReportFiles", "id", id);
		// 记录操作
		baseDao.logger.delete(caller, "id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, id);
	}

	@Override
	public void saveReportFilesG(String caller, String param) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		// 执行保存前的其它逻辑
		for (Map<Object,Object> map: grid){
			String cal= map.get("caller").toString();
			String rptName = map.get("file_name").toString();
			String attachId = map.get("attach").toString();
			System.out.println(attachId);
			if (StringUtil.hasText(attachId)){
/*				attachId = attachId.toString().substring(0,
						attachId.toString().indexOf(";"));*/
				attachId = attachId.toString().substring(attachId.toString().indexOf(";")+1);
				
				System.out.println(attachId);
				baseDao.execute("update reportfiles set file_path=nvl((select fp_path from filepath where fp_id='"
						+ attachId
						+ "'),file_path),last_modify='"
						+ SystemSession.getUser().getEm_name()
						+ "',modify_time=sysdate where caller='"
						+ cal + "' and file_name='" +rptName+ "'");
			}
		}		
		List<String> sqls = SqlUtil.getInsertOrUpdateSql(grid, "ReportFiles", "id");
		baseDao.execute(sqls);
	
	}

	@Override
	public void deleteReportFilesG(String caller, int id) {
		// TODO Auto-generated method stub
		baseDao.deleteByCondition("ReportFiles", "id="+id);
	}

}
