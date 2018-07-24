package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.WarehouseManService;

@Service("warehouseManService")
public class WarehouseManServiceImpl implements WarehouseManService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateWarehouseManById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.handler("WarehouseMan", "save", "before", new Object[] { store, gstore });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Warehouse", "wh_id");
		baseDao.execute(formSql);
		// 修改WarehouseMan
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "WarehouseMan", "wm_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("wm_id") == null || s.get("wm_id").equals("") || s.get("wm_id").equals("0")
					|| Integer.parseInt(s.get("wm_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("WAREHOUSEMAN_SEQ");
				s.put("wm_whid", store.get("wh_id"));
				String sql = SqlUtil.getInsertSqlByMap(s, "WarehouseMan", new String[] { "wm_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save("WarehouseMan", "wh_id", store.get("wh_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("WarehouseMan", "save", "after", new Object[] { store, gstore });
	}
	
	@Override
	public void clearWareMan(String caller, String condition){
		Object table = baseDao.getFieldDataByCondition("DetailGrid", "dg_table", "dg_caller='" + caller + "' and "
				+ "trim(dg_table) is not null order by dg_sequence ");
		if(table != null){
			Integer id = Integer.parseInt(condition.split("=")[1].toString());
			String tableName = table.toString().split(" ")[0];
			baseDao.execute("delete "+tableName+" where "+condition+"");
			baseDao.logger.others("删除明细操作", "删除成功", caller, condition.split("=")[0].toString(), id);
		}
	}
}
