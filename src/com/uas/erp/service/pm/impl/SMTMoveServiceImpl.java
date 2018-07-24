package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.SMTMoveService;
import com.uas.pda.dao.PdaCommonDao;

@Service("SMTMoveService")
public class SMTMoveServiceImpl implements SMTMoveService{
     @Autowired 
     private BaseDao baseDao;
     @Autowired 
     private PdaCommonDao pdaCommonDao;
     
	@Override
	public List<Map<String, Object>> loadSMTMoveStore(String de_oldCode,
			String mc_code) {
		// TODO Auto-generated method stub
		//判断dev_oldcode是否存在于device  ，并且de_runstatus=停止
		SqlRowList rs = baseDao.queryForRowSet("select de_statuscode,de_runstatus from device where de_code='"+de_oldCode+"'");
		if(rs.next()){
			if(!rs.getString("de_statuscode").equals("AUDITED")){
				BaseUtil.showError("原机台号："+de_oldCode+"未审核!");
			}
			if(!rs.getString("de_runstatus").equals("停止")){
				BaseUtil.showError("原机台号："+de_oldCode+"必须是停止状态!");
			}
		}else{
			BaseUtil.showError("原机台号："+de_oldCode+"不存在!");
		}
		rs = baseDao.queryForRowSet("select mc_statuscode,msl_devcode from makeCraft left join makesmtlocation on msl_mccode=mc_code where msl_mccode='"+mc_code+"'");
		if(rs.next()){
			if(!rs.getString("mc_statuscode").equals("AUDITED")){
				BaseUtil.showError("作业单号:"+mc_code+"未审核!");
			}
			if(!rs.getString("msl_devcode").equals(de_oldCode)){
				BaseUtil.showError("机台号："+de_oldCode+"不属于作业单号:"+mc_code+"!");
			}
		}else {
			BaseUtil.showError("作业单号:"+mc_code+"不存在!");			
		}		
		rs = baseDao.queryForRowSet("select msl_location,msl_table,msl_baseqty,msl_needqty,msl_getqty,msl_fecode,msl_fespec,msl_prodcode,msl_barcode," +
				"msl_id from MakeCraft left join MakeSMTLocation on mc_maid=msl_maid where mc_code=? And msl_devcode=? And NVL(msl_status,0)=0 and msl_remainqty>0",mc_code,de_oldCode);
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}
		return null;
	}

	@Override
	public void comfirmSMTMove(String de_oldCode, String mc_code,
			String de_newCode) {
		// TODO Auto-generated method stub
		List<Map<String,Object>> list = loadSMTMoveStore(de_oldCode,mc_code);
		if(list.isEmpty()){
		   BaseUtil.showError("没有需要转移的料卷!");
		}
		SqlRowList rs = baseDao.queryForRowSet("select de_statuscode,de_runstatus from device where de_code='"+de_newCode+"'");
		if(rs.next()){
			if(!rs.getString("de_statuscode").equals("AUDITED")){
				BaseUtil.showError("转至机台号："+de_oldCode+"未审核!");
			}
			if(!rs.getString("de_runstatus").equals("停止")){
				BaseUtil.showError("转至机台号："+de_oldCode+"必须是停止状态!");
			}
		}else{
			BaseUtil.showError("转至机台号："+de_oldCode+"不存在!");
		}
		//转 复制所有的记录插入到新机台对应的明细，当成是新机台的上料的业务处理。新机台的Remainqty，getqty都等于原机台的Remainqty
		//新机台msl_status=0
		baseDao.execute("insert into MakeSMTLocation(msl_id,msl_maid,msl_makecode,msl_mcid,"+
					"msl_mccode,msl_mmdetno,msl_location,msl_prodcode,msl_repcode,msl_fespec,msl_baseqty,"+
					"msl_table,msl_needqty,msl_getqty,msl_remainqty,msl_fecode,msl_barcode,msl_linecode,msl_devcode,msl_status)"+
					"select MAKESMTLOCATION_SEQ.nextval,msl_maid,msl_makecode,msl_mcid,"+
		            "msl_mccode,msl_mmdetno,msl_location,msl_prodcode,msl_repcode,msl_fespec,msl_baseqty,"+
		            "msl_table,msl_needqty,msl_remainqty,msl_remainqty,msl_fecode,msl_barcode,msl_linecode,'"+de_newCode+"',0 from MakeCraft left join MakeSMTLocation on mc_maid=msl_maid where mc_code='"+mc_code+"' And msl_devcode='"+de_oldCode+"' And NVL(msl_status,0)=0 and msl_remainqty>0");
	  //原机台相当于下料msl_status=-1,
		baseDao.updateByCondition("makeSMTlocation", "msl_status=-1", "msl_mccode='"+mc_code+"' And msl_devcode='"+de_oldCode+"'");
	}
}
