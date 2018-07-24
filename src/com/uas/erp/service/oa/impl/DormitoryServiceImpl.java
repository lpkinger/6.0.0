package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.DormitoryService;
@Service
public class DormitoryServiceImpl implements DormitoryService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void updateBednull(String condition, int bednull, String caller) {
		baseDao.updateByCondition("Dormitory", "do_bednull="+bednull, condition);
		baseDao.logger.update(caller, "do_id", condition);
	}

	@Override
	public void update(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.handler("Dormitory", "save", "before", new Object[] { gstore});
		// 修改MProjectPlanDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "DormitoryDetail", "dd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("dd_id") == null || s.get("dd_id").equals("") || s.get("dd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("DormitoryDetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "DormitoryDetail", new String[] { "dd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//已住人数
		int number=baseDao.getCountByCondition("DormitoryDetail", "dd_doid="+store.get("do_id"));
		//修改空床位
		store.put("do_bednull", Integer.parseInt(store.get("do_bed")+"")-number);
		String formSql=SqlUtil.getUpdateSqlByFormStore(store, "Dormitory", "do_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "do_id", store.get("do_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store, gstore});
	}

	@Override
	public void VastDeal(String gridStore, String  caller) {
		String sql="DELETE DormitoryDetail WHERE dd_id=?";
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(gridStore);
		for(Map<Object, Object> map:maps){
			baseDao.execute(sql,new Object[]{map.get("dd_id")});
			baseDao.logger.delete(caller, "dd_id", map.get("dd_id"));
		}
	}

}
