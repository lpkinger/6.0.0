package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DocumentListPowerDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.model.DocumentListPower;
import com.uas.erp.service.hr.JobDocumentListPowerService;

@Service
public class JobDocumentListPowerServiceImpl implements JobDocumentListPowerService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DocumentListPowerDao documentListPowerDao;
	@Autowired
	private HrJobDao hrJobDao;
	/**
	 * 修改岗位权限
	 */
	@Override
	@CacheEvict(value="documentlistpower",allEntries=true)
	public void update(String update, String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(update);
		//修改
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(update, "DocumentListPower", "dlp_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("dlp_id") == null || s.get("dlp_id").equals("") || s.get("dlp_id").equals("null")
					|| (Integer)s.get("dlp_id") == 0 ||s.get("dlp_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("DOCUMENTLISTPOWER_SEQ");
				s.put("dlp_id", id);
				String sql = SqlUtil.getInsertSqlByMap(s, "DocumentListPower", new String[]{"dlp_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		try{
			for(Map<Object, Object> map:gstore){
				//记录操作
				baseDao.logger.update(caller, "dlp_id", map.get("dlp_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<DocumentListPower> getDocumentListPowersByDCLID(int dcl_id) {
		return documentListPowerDao.getDocumentListPowersByDCLID(dcl_id);
	}

	@Override
	public DocumentListPower getDLPByDclIdAndJoID(int dcl_id, int em_id) {
		return documentListPowerDao.getDLPByJoId_DclId(hrJobDao.getJoIdByEmId(em_id), dcl_id);
	}
	
}
