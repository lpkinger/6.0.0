package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
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
import com.uas.erp.service.fa.RelatedPartyService;

@Service("relatedPartyService")
public class RelatedPartyServiceImpl implements RelatedPartyService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateRelatedParty(String caller, String sets) {
		List<Map<Object, Object>> gridStore = BaseUtil.parseGridStoreToMaps(sets);
		List<String> sqls = new ArrayList<String>();
		
		for (Map<Object, Object> map : gridStore) {
			if (!StringUtil.hasText(map.get("rs_id"))|| Integer.parseInt(String.valueOf(map.get("rs_id"))) <= 0) {
				map.put("rs_id", baseDao.getSeqId("RELATEDPARTYSET_SEQ"));
				sqls.add(SqlUtil.getInsertSqlByMap(map, "RELATEDPARTYSET"));
			}else {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "RELATEDPARTYSET", "rs_id"));
			}
		}
		
		try {
			baseDao.execute(sqls);
		} catch (Exception e) {
			BaseUtil.showError("更新失败！"+e.getMessage());
		}
		
		baseDao.logger.update(caller, "", "");
	}

	@Override
	public void refreshRelatedParty(String caller) {
		String main = BaseUtil.getXmlSetting("defaultSob");
		String masterName = SystemSession.getUser().getCurrentMaster().getMa_name();
		int isMain = masterName.equals(main)?1:0;
		String res = baseDao.callProcedure("REFRESHRELATEDPARTY", new Object[]{isMain});
		if (StringUtil.hasText(res)&&!"OK".equals(res)) {
			BaseUtil.showError(res);
		}
		baseDao.logger.others("刷新关联方", "刷新成功", caller, "", "");
	}

}
