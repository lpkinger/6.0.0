package com.uas.erp.service.scm.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.WarehouseingService;

@Service
public class WarehouseingServiceImpl implements WarehouseingService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public String createWarehouseing(String whi_clientcode,String whi_clientname,int whi_amount, String whi_freefix,String caller) {
		String scode="";
		for(int i=0;i<whi_amount;i++){
			String code=baseDao.sGetMaxNumber("Warehouseing", 2);   
			int whiid = baseDao.getSeqId("WAREHOUSEING_SEQ");
			if(whi_freefix!=""){
				code = whi_freefix + code.substring(code.indexOf("1"));
			}
			baseDao.execute("insert into warehouseing (whi_id,whi_code,whi_status,whi_updatedate,whi_updateman,whi_clientcode,whi_clientname) values (" + whiid + ", '"
					+ code + "', '取号', sysdate, '" + SystemSession.getUser().getEm_name() + "','"+whi_clientcode+"','"+whi_clientname+"')");
			// 记录操作
			baseDao.logger.save(caller, "whi_id", whiid);
			scode=scode+code+",";
		}
		return scode.substring(0,scode.length()-1);
	}

	@Override
	public List<?> getWarehouseingLog(String whi_code) {
		String sql = "SELECT WHL_ID, WHL_CODE, WHL_STATUS, WHL_UPDATEMAN, WHL_UPDATEDATE, WHL_TEXT FROM WarehouseingLog where whl_code=? order by whl_updatedate desc";
		SqlRowList list = baseDao.queryForRowSet(sql, whi_code);
		return list.getResultList();
	}

	@Override
	public void updateWarehouseing(String whi_code, String whi_status, String whi_text) {
		Employee employee = SystemSession.getUser();
		SqlRowList rs = baseDao.queryForRowSet("select * from warehouseing where whi_code=?", whi_code);
		if (rs.next()) {
			int whi_id = rs.getGeneralInt("whi_id");
			baseDao.execute("update warehouseing set whi_status='" + whi_status + "', whi_updateman='" + employee.getEm_name()
					+ "',whi_updatedate=sysdate where whi_id=" + whi_id);
			baseDao.execute("insert into warehouseinglog(whl_id, whl_code, whl_status, whl_text, whl_updateman, whl_updatedate) values (WAREHOUSEINGLOG_SEQ.NEXTVAL, '"
					+ whi_code + "', '" + whi_status + "', '" + whi_text + "', '" + employee.getEm_name() + "', sysdate)");
			baseDao.logger.update("Warehouseing", "whi_id", whi_id);
		}
	}
}
