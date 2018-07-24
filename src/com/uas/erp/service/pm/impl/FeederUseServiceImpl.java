package com.uas.erp.service.pm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.FeederUseService;

@Service
public class FeederUseServiceImpl implements FeederUseService {

	@Autowired
	private BaseDao baseDao;

	@Override
	public void getFeeder(String feedercode, String makecode, String linecode) {
		Object []obj1 = baseDao.getFieldsDataByCondition("feeder", "fe_usestatus,fe_spec", "fe_code='"+feedercode+"'");
		if (obj1==null){
			BaseUtil.showError("飞达：" + feedercode + "，不存在！");
		}else if(!"未领用".equals(obj1[0])){
			BaseUtil.showError("飞达：" + feedercode + "，状态["+obj1[0]+"]！");
		} 
		SqlRowList rs = baseDao.queryForRowSet("select ma_code from make left join productSMT on ma_prodcode=ps_prodcode left join productSMTLocation on ps_id=psl_psid where ma_code='"+makecode+"' and psl_feeder='"+obj1[1]+"'");
		if(!rs.next()){
			BaseUtil.showError("飞达：" + feedercode + "，规格["+obj1[1]+"]不是此工单适用规格！");
		}
		int id = baseDao.getSeqId("FEEDERUSE_SEQ");
		baseDao.execute("INSERT INTO FeederUse(fu_id,fu_makecode, fu_linecode,fu_fecode,fu_fespec,fu_status,fu_statuscode,"
				+ "fu_usedate,fu_useman)"
				+ " select "+ id + ",'"+makecode+"','"+linecode+"','"+feedercode+"',fe_spec,'待上料','UNFEEDING',"
				+ "sysdate,'" + SystemSession.getUser().getEm_name() + "' from Feeder where fe_code='" + feedercode + "'");
		baseDao.execute("update FeederUse set fu_maid=(select ma_id from make where ma_code=fu_makecode) where fu_id=" + id + " and nvl(fu_makecode,' ')<>' '");
		baseDao.execute("update Feeder set fe_usestatus='已领用',fe_makecode='" + makecode + "', fe_linecode='" + linecode + "' where fe_code='" + feedercode + "'");
	}

	@Override
	public void returnFeeder(String feedercode, String reason, int isuse) {
		//从飞达表中查找当前领用工单记录
		SqlRowList rs0 = baseDao.queryForRowSet("select fe_makecode from Feeder where fe_code='"+feedercode+"'");
		if(rs0.next()){
			if(rs0.getObject("fe_makecode") == null)
			   BaseUtil.showError("飞达：" + feedercode + "，未领用");
		}else{
			BaseUtil.showError("飞达：" + feedercode + "，不存在");		
		}
		SqlRowList rs  = baseDao.queryForRowSet("select fu_devcode,NVL(fu_status,0) fu_status from FeederUse where fu_fecode=? and fu_makecode=?",feedercode,rs0.getString("fe_makecode"));
		if (rs.next()) {			
			if(rs.getString("fu_status").equals("待上料")){//待上料状态才可以归还
				baseDao.execute("update FeederUse set fu_status='已归还',fu_statuscode='RETURNED' where fu_fecode='" + feedercode + "' and fu_makecode='"+rs0.getString("fe_makecode")+"'");
				baseDao.execute("update Feeder set fe_usestatus='未领用',fe_makecode=null, fe_linecode=null where fe_code='" + feedercode + "'");
				if(isuse != 0){
					baseDao.execute("update Feeder set fe_usestatus='停用',fe_reason='"+reason+"' where fe_code='" + feedercode + "'");
				} 
			}else if(rs.getString("fu_status").equals("已归还")){
				BaseUtil.showError("飞达：" + feedercode + "，已归还!");
			}else{
				BaseUtil.showError("飞达：" + feedercode + "，已被机台使用，使用机台：" + rs.getString("fu_devcode") + "！");
			}
		}	
	}

	@Override
	public void returnAllFeeder(String makecode) {
		int cn = baseDao.getCount("select count(1) cn  from feederUse where fu_makecode='"+makecode+"'");
		if(cn == 0){
			BaseUtil.showError("制造单:"+makecode+"未领用飞达，不需要归还!");
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(fu_fecode) from FeederUse where fu_makecode=?  and nvl(fu_status,' ')='已归还'",
						String.class, makecode);
		if (dets != null) {
			BaseUtil.showError("飞达：" + dets + "，已归还！");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(fu_fecode) from FeederUse where fu_makecode=?  and nvl(fu_status,' ')<>'待上料'",
						String.class, makecode);
		if(dets != null){
			BaseUtil.showError("存在飞达：" + dets + "，被机台使用，不允许归还！");
		}
		baseDao.execute("update FeederUse set fu_status='已归还',fu_statuscode='RETURNED' where fu_makecode='" + makecode + "'");
		baseDao.execute("update Feeder set fe_usestatus='未领用',fe_makecode=null, fe_linecode=null where fe_code in (select fu_fecode from FeederUse where fu_makecode='" + makecode + "')");
	}

}
