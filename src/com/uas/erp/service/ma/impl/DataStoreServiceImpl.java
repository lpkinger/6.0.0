package com.uas.erp.service.ma.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.ma.DataStoreService;

@Service
public class DataStoreServiceImpl implements DataStoreService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public void save(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DataStore", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Object[] dsd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			dsd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				dsd_id[i] = baseDao.getSeqId("DATASTOREDETAIL_SEQ");
			}
		} else {
			dsd_id[0] = baseDao.getSeqId("DATASTOREDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "DataStoreDetail", "dsd_id", dsd_id);
		baseDao.execute(gridSql);
		baseDao.logger.save("DataStore", "ds_id", store.get("ds_id"));
	}

	@Override
	public void delete(int id) {
		// 执行删除前的其它逻辑
		baseDao.deleteById("DataStore", "ds_id", id);
		baseDao.deleteById("DataStoreDetail", "dsd_mainid", id);
		// 记录操作
		baseDao.logger.delete("DataStore", "ds_id", id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DataStore", "ds_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "DataStoreDetail", "dsd_id");
		baseDao.execute(gridSql);
		baseDao.logger.update("DataStore", "ds_id", store.get("ds_id"));
	}

	@Override
	public String getFieldsByTable(int id) {
		/** 3.4 data 和4.0不同 只有value 没有key */
		String FindSql = "Select dsd_id,dsd_field,dsd_caption from DataStoreDetail where dsd_mainid=" + id + " order by dsd_detno ";
		SqlRowList sl = baseDao.queryForRowSet(FindSql);
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		while (sl.next()) {
			sb.append("[");
			sb.append("\"" + sl.getInt(1) + "\",");
			sb.append("\"" + sl.getString(2) + "\",");
			sb.append("\"" + sl.getString(3) + "\"");
			sb.append("],");
		}

		return sb.toString().substring(0, sb.toString().lastIndexOf(",")) + "]";
	}

	@Override
	public String getExcelFxsByTable(int id) {
		String FindSql = "Select ef_id,ef_fullname,ef_description from ExcelFx where ef_datastoreid=" + id
				+ " order by nlssort(ef_fullname,'NLS_SORT=SCHINESE_PINYIN_M')";
		SqlRowList sl = baseDao.queryForRowSet(FindSql);
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		while (sl.next()) {
			sb.append("[");
			sb.append("\"" + sl.getInt(1) + "\",");
			sb.append("\"" + sl.getString(2) + "\",");
			sb.append("\"" + sl.getString(3) + "\"");
			sb.append("],");
		}
		return sb.toString().substring(0, sb.toString().lastIndexOf(",")) + "]";
	}

}
