package com.uas.erp.service.scm.impl;

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
import com.uas.erp.service.scm.BarOrPackReportService;

@Service("barOrPackReportService")
public class BarOrPackReportServiceImpl implements BarOrPackReportService {
	@Autowired
	private BaseDao baseDao;


	@Override
	public void updateReportFile(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		for (Map<Object,Object> map: grid){
			map.put("caller", store.get("fo_caller"));
			map.put("foid", 0);
		}
		List<String> sqls = SqlUtil.getInsertOrUpdateSql(grid, "ReportFiles", "id");
		baseDao.execute(sqls);
		baseDao.logger.update(caller, "caller", store.get("fo_caller"));
	}

	@Override
	public void deleteReportFile(String callers, String caller) {
		baseDao.deleteByCondition("ReportFiles", "caller='"+callers+"'");
		// 记录操作
		baseDao.logger.others("删除操作", "删除明细成功", caller, "caller", callers);
	}

}
