package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.service.ma.GroupService;

@Service
public class GroupServiceImpl implements GroupService {

	@Autowired
	private BaseDao baseDao;

	@Override
	@CacheEvict(value = "basedataset", allEntries = true)
	public void updateBaseDataSet(String data) {
		baseDao.updateByCondition("BaseDataSet", "bds_editable=0", "1=1");
		if (data != null && data.length() > 0) {
			baseDao.updateByCondition("BaseDataSet", "bds_editable=1", "bds_caller in (" + data + ")");
		}
	}

	@Override
	public void updatePostStyleSet(String data) {
		List<Map<Object, Object>> datas = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		String currentMaster = SpObserver.getSp();
		for (Map<Object, Object> m : datas) {
			sqls.add("update poststyle set ps_autosync='" + m.get("ps_autosync") + "' where ps_caller='"
					+ m.get("ps_caller") + "'");
			String triggerName = m.get("ps_caller").toString().replaceAll("!", "_") + "_SYNC_TRG";
			if (m.get("ps_autosync") == null || "".equals(m.get("ps_autosync"))) {
				sqls.add("drop trigger " + triggerName);
			} else {
				if (m.get("ps_table") != null && m.get("ps_keyfield") != null) {
					sqls.add("create or replace TRIGGER " + triggerName + " AFTER INSERT ON " + m.get("ps_table")
							+ " FOR EACH ROW declare v_res varchar2(30);BEGIN BEGIN IF :NEW." + m.get("ps_keyfield")
							+ " IS NOT NULL THEN SYS_POST('" + m.get("ps_caller") + "','" + currentMaster + "','"
							+ m.get("ps_autosync") + "', :NEW." + m.get("ps_keyfield")
							+ ", 'admin', 1, v_res);END IF; END;END;");
				}
			}
		}
		baseDao.execute(sqls);
	}

}
