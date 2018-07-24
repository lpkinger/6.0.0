package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeCraftDao;
import com.uas.erp.service.pm.OverStationGetService;
import com.uas.pda.dao.PdaCommonDao;

@Service("overStationGetService")
public class OverStationGetServiceImpl implements OverStationGetService{
    @Autowired
    private PdaCommonDao pdaCommonDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private MakeCraftDao  makeCraftDao;   
    
	@Override
	public Map<String, Object> getOverStationStore(String scCode, String mcCode) {
		// TODO Auto-generated method stub
		//判断资源编号是否正确。
	    Object ob = baseDao.getFieldDataByCondition("source","sc_statuscode","sc_code='"+scCode+"'");
	    if(ob == null){
	    	BaseUtil.showError("资源编号："+scCode+"不存在!");
	    }else if(!ob.toString().equals("AUDITED")){
	    	BaseUtil.showError("资源编号："+scCode+"未审核!");
	    }		
		//判断作业单当前状态是否已审核，且mc_qty>mc_madeqty
	    SqlRowList rs = baseDao.queryForRowSet("select mc_qty,mc_madeqty,mc_statuscode from makeCraft where mc_code='"+mcCode+"'");
	    if(rs.next()){
	    	if(!rs.getString("mc_statuscode").equals("AUDITED"))
	    	    BaseUtil.showError("作业单："+mcCode+"未审核!");
	    	if(rs.getDouble("mc_qty") == rs.getDouble("mc_madeqty")){
	    		BaseUtil.showError("作业单："+mcCode+"已经完成生产!");
	    	}
	    }else{
	    	BaseUtil.showError("作业单："+mcCode+"不存在!");
	    }
	   //判断资源所属工序是否在本作业单明细表的工序里面。
		rs = baseDao.queryForRowSet("select sc_code,sc_name,mc_code,mcd_stepcode st_code,mcd_stepname st_name,mcd_inqty,(mc_qty-mcd_inqty) mcd_restqty,mc_qty from makeCraft left join makeCraftDetail" +
				" on mc_id=mcd_mcid left join source on sc_stepcode=mcd_stepcode where sc_code='"+scCode+"' and mc_code='"+mcCode+"'");
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
		}else{
			BaseUtil.showError("资源所属的工序不在作业单明细表的工序里面!");
		}
		return null;
	}

	@Override
	public Map<String, Object> confirmSnCodeGet(String sc_code, String mc_code,
			String sn_code, String st_code,boolean combineChecked) {
		// TODO Auto-generated method stub
		SqlRowList rs,rs1;      
		//判断序列号是否当前作业单的序列号
		int cn = baseDao.getCount("select count(1) cn  from makeSerial left join makeCraft on mc_makecode=ms_makecode where ms_sncode='"+sn_code+"' and mc_code='"+mc_code+"'");	  
		if( cn == 0){
			BaseUtil.showError("序列号:"+sn_code+",不属于当前制造单序列号!");
		}
		//序列号回车：判断当前序列号的下一工序是否为当前工序。
		if(!makeCraftDao.ifNextStepcode(st_code, sn_code,mc_code)){//判断序列号的ms_nextstepcode是否等于当前工序
			BaseUtil.showError("序列号:"+sn_code+",当前工序不是"+st_code);
		}
		//判断资源所属工序是否在本作业单明细表的工序里面。
		rs = baseDao.queryForRowSet("select mcd_stepcode from makeCraft left join makeCraftDetail" +
				" on mc_id=mcd_mcid left join source on sc_stepcode=mcd_stepcode where sc_code='"+sc_code+"' and mc_code='"+mc_code+"'");
		if(rs.next()){
			//判断该工序是否为过站采集工序，craftdetail 表中其他属性都为否[0],即为过站采集工序
			Object ob = baseDao.getFieldDataByCondition("Craft left join CraftDetail on cr_id=cd_crid left join makeSerial on ms_craftcode=cr_code", "cd_stepcode", "cd_stepcode='"+st_code+"' and ms_sncode='"+sn_code+"'and NVL(cd_ifinput,0)=0 and NVL(cd_iftest,0)=0 " +
					" and NVL(cd_ifsnchange,0)=0 and NVL(cd_ifpack,0)=0 and NVL(cd_ifrepair,0)=0 and NVL(cd_ifsmtinout,0)=0 and NVL(cd_ifreduce,0)=0 " +
					" and NVL(cd_ifmidfinish,0)=0 and NVL(cd_ifmidinput,0)=0 and NVL(cd_ifoffline,0)=0 and NVL(cd_ifoutput,0)=0 and NVL(cd_ifoqc,0)=0 " +
					" and NVL(cd_ifoutline,0)=0 and NVL(cd_ifspc,0)=0 and NVL(cd_ifburnin,0)=0 and NVL(cd_ifburnout,0)=0");
			if(ob == null){
				BaseUtil.showError("资源所属的工序:"+st_code+"，不是过站工序!");
			}			
			//如果勾选“拼板采集”，则根据所采集的序列号取出ms_combinecode，再关联e拼板内的所有序列号。相当于连续采集了n个序列号
			if(combineChecked){
				rs1 = baseDao.queryForRowSet("select ms_sncode from makeSerial where ms_combinecode=(select ms_combinecode from makeSerial where ms_sncode='"+sn_code+"')");
				if(rs1.next()){//是拼板
					for(Map<String,Object> map:rs1.getResultList()){
						if(makeCraftDao.ifNextStepcode(st_code, map.get("ms_sncode").toString(),mc_code)){//判断序列号的ms_nextstepcode等于当前工序
							makeCraftDao.updateMakeMessage("过站采集", "采集成功", map.get("ms_sncode").toString(), mc_code, st_code);
						}
					}
				}else{//不是拼板
					BaseUtil.showError("序列号："+sn_code+",不是拼板序列号!");
				}
			}else{
				//更新makesertial的下一工序
				//更新makecraftdetail的mcd_inqty +1，mcd_outqty+1，mcd_okqty+1
				//插入记录到makeprocess
				makeCraftDao.updateMakeMessage("过站采集", "采集成功", sn_code, mc_code, st_code);
			}
			rs = baseDao.queryForRowSet("select sc_code,sc_name,mc_code,mcd_stepcode st_code,mcd_stepname st_name,mcd_inqty,(mc_qty-mcd_inqty) mcd_restqty,mc_qty from makeCraft left join makeCraftDetail" +
					" on mc_id=mcd_mcid left join source on sc_stepcode=mcd_stepcode where sc_code='"+sc_code+"' and mc_code='"+mc_code+"'");
			if(rs.next()){
			   return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
			}
		}else{
			BaseUtil.showError("资源所属的工序不在作业单明细表的工序里面!");
		}
		return null;
	}

}
