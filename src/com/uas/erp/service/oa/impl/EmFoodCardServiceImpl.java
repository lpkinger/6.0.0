package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.EmFoodCardService;
@Service
public class EmFoodCardServiceImpl implements EmFoodCardService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveEmFoodCard(String formStore, String gridStore, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "EmFoodCard", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		for (Map<Object, Object> map : grid) {
			map.put("efd_id", baseDao.getSeqId("EmFoodCardDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"EmFoodCardDetail");
		// System.out.println(gridSql);
		baseDao.execute(gridSql);
		String sql="update EmFoodCardDetail set efd_total=nvl(efd_czmoney,0)+nvl(efd_deposit,0) where efd_efid="+store.get("ef_id");
		baseDao.execute(sql);
		//记录操作
		baseDao.logger.save(caller, "ef_id", store.get("ef_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void deleteEmFoodCard(int id, String  caller) {
		// TODO Auto-generated method stub
		//执行删除前的其它逻辑
		        handlerService.beforeDel(caller,new Object[]{id});
						//删除purchase
				baseDao.deleteById("EmFoodCard", "ef_id", id);
				baseDao.deleteById("EmFoodCardDetail", "efd_efid", id);
						//记录操作
				baseDao.logger.delete(caller, "ef_id", id);
						//执行删除后的其它逻辑
				handlerService.afterDel(caller,new Object[]{id});
	}

	@Override
	public void updateEmFoodCard(String formStore, String gridStore,
			String  caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改Schedule
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "EmFoodCard", "ef_id");
		baseDao.execute(formSql);		
		List<Map<Object, Object>> grid2 = BaseUtil
				.parseGridStoreToMaps(gridStore);
		List<String> gridSql2 = null;
		if (grid2.size() > 0) {
			gridSql2 = SqlUtil.getUpdateSqlbyGridStore(grid2, "EmFoodCardDetail",
					"efd_id");
			for (Map<Object, Object> s : grid2) {
				Object aid = s.get("efd_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("EmFoodCardDetail_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "EmFoodCardDetail",
							new String[] { "efd_id" }, new Object[] { id });
					gridSql2.add(sql);
				}
			}
			baseDao.execute(gridSql2);
			// System.out.println("2" + gridSql2);
		}
		/*List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "SellerSaleReportDet", "ssd_id");
		baseDao.execute(gridSql);*/
		String sql="update EmFoodCardDetail set efd_total=nvl(efd_czmoney,0)+nvl(efd_deposit,0) where efd_efid="+store.get("ef_id");
		baseDao.execute(sql);
		//记录操作
		baseDao.logger.delete(caller, "ef_id", store.get("ef_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

}
