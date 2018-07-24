package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DocumentPowerDao;
import com.uas.erp.model.DocumentPositionPower;
import com.uas.erp.service.hr.JobDocumentPowerService;

@Service
public class JobDocumentPowerServiceImpl implements JobDocumentPowerService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DocumentPowerDao documentPowerDao;
	/**
	 * 修改岗位权限
	 */
	@Override
	@CacheEvict(value="documentpositionpower",allEntries=true)
	public void update(String update, String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(update);
		//修改
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(update, "DocumentPositionPower", "dpp_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("dpp_id") == null || s.get("dpp_id").equals("") || s.get("dpp_id").equals("null")
					|| (Integer)s.get("dpp_id") == 0 ||s.get("dpp_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("DOCUMENTPOSITIONPOWER_SEQ");
				s.put("dpp_id", id);
				String sql = SqlUtil.getInsertSqlByMap(s, "DocumentPositionPower", new String[]{"dpp_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		try{
			for(Map<Object, Object> map:gstore){
				//记录操作
				baseDao.logger.update(caller, "dpp_id", map.get("dpp_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<DocumentPositionPower> getDocumentPositionPowersByDCPID(int dcp_id) {
		return documentPowerDao.getDocumentPositionPowersByDCPID(dcp_id);
	}

	@Override
	public DocumentPositionPower getDPPByDcpIdAndJoID(int dcp_id, int jo_id) {
		return documentPowerDao.getDPPByDcpIdAndJoId(dcp_id, jo_id);
	}
	
}
