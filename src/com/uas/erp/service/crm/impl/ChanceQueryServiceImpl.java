package com.uas.erp.service.crm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.crm.ChanceQueryService;

@Service
public class ChanceQueryServiceImpl implements ChanceQueryService {
	@Autowired
	private SingleGridPanelService singleGridPanelService;

	@Autowired
	private BaseDao baseDao;

	@Override
	public GridPanel getQuery(String caller, String condition) {
		JSONObject d = JSONObject.fromObject(condition);// condition:{"ch_tasker":"\u9648\u864e2","chq_cucode":{"ch_cucode":"7.009"}}
		GridPanel gridPanel = null;
		String con = " 1=1";
		if (d.containsKey("ch_tasker") && d.getString("ch_tasker") != null) {
			con += " AND ch_tasker='" + d.getString("ch_tasker") + "'";
		}
		if (d.containsKey("chq_cucode")) {// 蛋疼的API，没这个KEY的时候，居然是报错，不是返回null
			JSONObject chq_custcode = d.get("chq_cucode") == null ? null
					: (JSONObject) d.get("chq_cucode");
			if (chq_custcode != null && chq_custcode.containsKey("ch_cucode")
					&& chq_custcode.getString("ch_cucode") != null) {
				con += " AND ch_cucode='" + chq_custcode.getString("ch_cucode")
						+ "' ";
			}
		}
		if (d.containsKey("ch_stage") && d.getString("ch_stage") != null) {
			con += " AND ch_stage='" + d.getString("ch_stage") + "' ";
		}
		gridPanel = singleGridPanelService.getGridPanelByCaller(caller, con, 1,
				200, null,false,"");
		String sql = "SELECT ch_code,ch_from,ch_title,ch_cucode,ch_cuname,ch_contact,ch_tasker,ch_stage,ch_status,ch_id "
				+ "FROM Chance where " + con;
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		while (sqlRowList.next()) {
			map = new HashMap<String, Object>();
			map.put("ch_code", sqlRowList.getObject("ch_code"));
			map.put("ch_from", sqlRowList.getObject("ch_from"));
			map.put("ch_title", sqlRowList.getObject("ch_title"));
			map.put("ch_cucode", sqlRowList.getObject("ch_cucode"));
			map.put("ch_cuname", sqlRowList.getObject("ch_cuname"));
			map.put("ch_contact", sqlRowList.getObject("ch_contact"));
			map.put("ch_tasker", sqlRowList.getObject("ch_tasker"));
			map.put("ch_stage", sqlRowList.getObject("ch_stage"));
			map.put("ch_status", sqlRowList.getObject("ch_status"));
			map.put("ch_id", sqlRowList.getObject("ch_id"));
			statis.add(map);
		}
		gridPanel.setDataString(BaseUtil.parseGridStore2Str(statis));
		return gridPanel;
	}

}
