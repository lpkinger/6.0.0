package com.uas.erp.service.fa.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fa.SubsidiarySetService;

@Service("subsidiarySetService")
public class SubsidiarySetServiceImpl implements SubsidiarySetService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public List<Map<String, Object>> getSubsidiarySet(Boolean isCheck) {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select * from SubsidiarySet order by ss_detno");
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ss_id", rs.getString("ss_id"));
			map.put("ss_detno", rs.getString("ss_detno"));
			map.put("ss_mastercode", rs.getString("ss_mastercode"));
			map.put("ss_mastername", rs.getString("ss_mastername"));
			map.put("ss_name", rs.getString("ss_name"));
			map.put("ss_currency", rs.getString("ss_currency"));
			map.put("ss_man", rs.getString("ss_man"));
			map.put("ss_enable", rs.getInt("ss_enable"));
			Timestamp time = rs.getTimestamp("ss_date");
			String date = null;
			try {
				date = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD);
				map.put("ss_date", date);
			} catch (Exception e) {
				e.printStackTrace();
			}
			items.add(map);
		}
		if (isCheck != null && isCheck) {
			for (Map<String, Object> map : items) {
				if (!(Boolean) map.get("ss_enable")) {
					datas.add(map);
				}
			}
		}
		items.removeAll(datas);
		return items;
	}

	@Override
	public List<Map<String, Object>> getShareholdersRateSet(String checkcode) {
		SqlRowList list = baseDao.queryForRowSet("select * from SHAREHOLDERSRATESET where SHR_SSID=?", checkcode);
		return list.getResultList();
	}

	@Override
	public void saveSubsidiarySet(String CheckItems) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(CheckItems);
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : list) {
			if (map.get("ss_enable") == null || "".equals(map.get("ss_enable")) || "null".equals(map.get("ss_enable"))
					|| !Boolean.parseBoolean(map.get("ss_enable").toString())) {
				map.put("ss_enable", 0);
			} else {
				map.put("ss_enable", 1);
			}
			map.put("ss_man", SystemSession.getUser().getEm_name());
			map.put("ss_date", DateUtil.format(new Date(), Constant.YMD));
			Object ssid = map.get("ss_id");
			if (ssid == null || ssid.equals("") || ssid.equals("0") || Integer.parseInt(ssid.toString()) == 0) {// 新添加的数据，id不存在
				map.put("ss_id", baseDao.getSeqId("SUBSIDIARYSET_SEQ"));
				sqls.add(SqlUtil.getInsertSqlByMap(map, "SUBSIDIARYSET"));
			} else {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "SUBSIDIARYSET", "ss_id"));
			}
		}
		baseDao.execute(sqls);
		// 更新公司层级
		baseDao.execute("update SUBSIDIARYSET set SS_LEVEL=0 where not exists (select 1 from SHAREHOLDERSRATESET where shr_ssid=ss_id)");
		for (int i = 0; i < 5; i++) {
			baseDao.execute("update SUBSIDIARYSET b set SS_LEVEL="
					+ (i + 1)
					+ " where exists (select 1 from SHAREHOLDERSRATESET,SUBSIDIARYSET A WHERE SHR_PID=A.SS_ID AND SHR_SSID=B.SS_ID AND NVL(A.SS_LEVEL,0)="
					+ i + ")");
		}
	}

	@Override
	public void saveShareholdersRateSet(String checkcode, String ParamSets) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(ParamSets);
		List<String> sqls = new ArrayList<String>();
		// 先删除原有的设置数据
		baseDao.deleteByCondition("SHAREHOLDERSRATESET", "shr_ssid=?", checkcode);
		// 加上关联ID
		for (Map<Object, Object> map : list) {
			map.put("shr_ssid", checkcode);
			sqls.add(SqlUtil.getInsertSqlByMap(map, "SHAREHOLDERSRATESET"));
		}
		try {
			baseDao.execute(sqls);
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}
}
