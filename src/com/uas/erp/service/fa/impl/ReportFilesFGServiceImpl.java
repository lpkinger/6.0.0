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
import com.uas.erp.service.fa.ReportFilesFGService;

@Service
public class ReportFilesFGServiceImpl implements ReportFilesFGService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveReportFilesFG(String formStore, String gridStore,
			String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		for (Map<Object,Object> map: grid){
			String cal= map.get("caller").toString();
			String rptName = map.get("file_name").toString();
			String attachId = map.get("attach").toString();
			if (StringUtil.hasText(attachId)){
				attachId = attachId.toString().substring(attachId.toString().indexOf(";")+1);
				String sql="update reportfiles set file_path=nvl((select fp_path from filepath where fp_id='"
						+ attachId
						+ "'),file_path),last_modify='"
						+ SystemSession.getUser().getEm_name()
						+ "',modify_time=sysdate where caller='"
						+ cal + "' and file_name='" +rptName+ "'";
				baseDao.execute(sql);
			}
		}		
		List<String> sqls = SqlUtil.getInsertOrUpdateSql(grid, "ReportFiles", "id");
		baseDao.execute(sqls);
	}

	@Override
	public void updateReportFilesFG(String formStore, String gridStore,
			String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"reportfiles", "id");
		// 执行保存前的其它逻辑
		for (Map<Object,Object> map: grid){
			if (map.get("id") == null || map.get("id").equals("")
					|| map.get("id").equals("0")
					|| Integer.parseInt(map.get("id").toString()) == 0) {
				int id = baseDao.getSeqId("reportfiles_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(map,
						"reportfiles",
						new String[] { "id" }, new Object[] { id });
				gridSql.add(sql);
			}
			String cal= map.get("caller").toString();
			String rptName = map.get("file_name").toString();
			String attachId = map.get("attach").toString();
			List<String> sqls = SqlUtil.getInsertOrUpdateSql(grid, "ReportFiles", "id");
			baseDao.execute(sqls);
			if (StringUtil.hasText(attachId)){
				String name= map.get("attach").toString().split(";")[0];
				attachId = attachId.toString().substring(attachId.toString().indexOf(";")+1);
				String sql1="update reportfiles set file_path=nvl((select fp_path from filepath where fp_id='"
						+ attachId
						+ "'),file_path),last_modify='"
						+ SystemSession.getUser().getEm_name()
						+ "',modify_time=sysdate where caller='"
						+ cal + "' and file_name='" +rptName+ "'";
				baseDao.execute(sql1);
				if(!"0".equals(attachId)){//attch字段实际为空，更新条件字段时无法更新，所以将【attach赋值为报表名;0】，此时不强制更新路径
					String sql2="update reportfiles set file_path='/usr/rpts/"+name+"' where caller='"
							+ cal + "' and file_name='" +rptName+ "'";
					baseDao.execute(sql2);
				}
			}
		}
	}

	@Override
	public void deleteReportFilesFG(int fo_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, fo_id);
		// 删除purchase
		baseDao.deleteById("ReportFiles", "fo_id",fo_id);
		// 记录操作
		baseDao.logger.delete(caller, "fo_id", fo_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, fo_id);
	}

}
