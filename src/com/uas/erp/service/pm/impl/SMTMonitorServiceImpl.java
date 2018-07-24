package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.SMTMonitorService;
import com.uas.pda.dao.PdaCommonDao;

@Service("SMTMonitorService")
public class SMTMonitorServiceImpl implements SMTMonitorService{
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private PdaCommonDao pdaCommonDao;
    
	@Override
	public Map<String, Object> getSMTMonitorStore(String de_code) {
		// TODO Auto-generated method stub
		//1、判断输入的机台编号是否存在，状态是否为审核 2、判断该机台是否在线，如果不在在线提示
		Object ob = baseDao.getFieldDataByCondition("device", "de_statuscode", "de_code='"+de_code+"'");
		if(ob != null){
			if(!(ob.toString().equals("AUDITED"))){//机台未审核
				BaseUtil.showError("机台："+de_code+"未审核!");
			}
		}else{
			BaseUtil.showError("机台："+de_code+"不存在!");
		}
		SqlRowList rs = baseDao.queryForRowSet("select msl_devcode,mc_makecode,mc_qty,mc_madeqty from makeCraft left join makesmtlocation on msl_mcid=mc_id left join device on msl_devcode=de_code where msl_devcode='"+de_code+"' and msl_status=0");
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
		}else{
			BaseUtil.showError("机台："+de_code+"未使用!");
		}
		return null;
	}

}
