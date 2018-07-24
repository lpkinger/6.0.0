package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.FundDataService;
@Service
public class FundDataServiceImpl implements FundDataService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;	

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public List<Map<String, Object>> autogetItems(Integer id,String kind) {
		//删除原来的数据
		if (id!=null) {
			baseDao.deleteByCondition("FundDataDetail", "FDD_FDID="+id);
		}
		String sql ="select  rd_detno,rd_item from ReportItemDetail  left join ReportItem on RI_ID= RD_RIID where  rd_accesstype='artificial' and  ri_kind='"+kind+"' order by rd_detno";
		baseDao.logger.others("获取明细", "获取明细成功", "FundData", "fd_id", id);
		return baseDao.queryForList(sql);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void save(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FundData", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "FundDataDetail","fdd_id");
		baseDao.execute(gridSql);
		//求本月合计
		monthTotal(store);
		baseDao.logger.save(caller, "fd_id", store.get("fd_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void update(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FundData", "fd_id");
		baseDao.execute(formSql);
		if (grid!=null) {
			List<String> gridsql = SqlUtil.getUpdateSqlbyGridStore(grid, "FundDataDetail", "fdd_id");
			for (Map<Object, Object> map : grid) {
				Object fdd_id = map.get("fdd_id");
				if (fdd_id == null || fdd_id.equals("") || fdd_id.equals("0") || Integer.parseInt(fdd_id.toString()) == 0) {
					baseDao.execute(SqlUtil.getInsertSql(map, "FundDataDetail", "fdd_id"));
				}
			}
			baseDao.execute(gridsql);
		}
		//求本月合计
		monthTotal(store);
		// 执行修改后的其它逻辑
		baseDao.logger.update(caller, "fd_id", store.get("fd_id"));
		handlerService.afterUpdate(caller, new Object[] { store, grid });		
	}

	@Override
	public void delete(int fd_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { fd_id });
		// 删除
		baseDao.deleteById("FundData", "fd_id", fd_id);
		//删除明细
		baseDao.deleteById("FundDataDetail", "fdd_fdid", fd_id);
		// 记录操作
		baseDao.logger.delete(caller, "fd_id", fd_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { fd_id });		
	}
	
	
	public void monthTotal(Map<Object, Object> store) {
		Integer keyValue = Integer.parseInt(store.get("fd_id").toString());
		baseDao.updateByCondition("FundDataDetail", "FDD_MONTHTOTAL=nvl(FDD_WEEK1,0)+nvl(FDD_WEEK2,0)+nvl(FDD_WEEK3,0)+nvl(FDD_WEEK4,0)", "fdd_fdid="+keyValue);
	}

}
