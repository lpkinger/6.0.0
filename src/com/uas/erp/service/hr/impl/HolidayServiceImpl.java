package com.uas.erp.service.hr.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: USOFTPC30 Date: 13-6-17 Time: 上午9:26 To
 * change this template use File | Settings | File Templates.
 */
@Service
public class HolidayServiceImpl implements HolidayService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveHoliday(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "holiday", new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		Object[] hod_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			hod_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				hod_id[i] = baseDao.getSeqId("HOLIDAYDETAIL_SEQ");
			}
		} else {
			hod_id[0] = baseDao.getSeqId("HOLIDAYDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "holidaydetail", "hod_id", hod_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ho_id", store.get("ho_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store,gstore});
	}

	@Override
	public void updateHolidayById(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store,gstore});
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "holiday", "ho_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "holidaydetail", "hod_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("hod_id") == null || s.get("hod_id").equals("") || s.get("hod_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("HOLIDAYDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "holidaydetail", new String[] { "hod_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ho_id", store.get("ho_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store,gstore});
	}

	@Override
	public void deleteHoliday(int ho_id, String  caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ho_id});
		// 删除purchase
		baseDao.deleteById("holiday", "ho_id", ho_id);
		// 删除purchaseDetail
		baseDao.deleteById("holidaydetail", "hod_hoid", ho_id);
		// 记录操作
		baseDao.logger.delete(caller, "ho_id", ho_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { ho_id});
	}
}
