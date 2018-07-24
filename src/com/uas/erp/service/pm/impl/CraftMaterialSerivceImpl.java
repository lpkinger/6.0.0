package com.uas.erp.service.pm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeCraftDao;
import com.uas.erp.service.pm.CraftMaterialService;
import com.uas.pda.dao.PdaCommonDao;

@Service("CraftMaterialSerivce")
public class CraftMaterialSerivceImpl implements CraftMaterialService{
    @Autowired
    private BaseDao baseDao;
    @Autowired 
    private PdaCommonDao pdaCommonDao;
    @Autowired
    private MakeCraftDao makeCraftDao;
	@Override
	public Map<String, Object>  checkCraftMaterialQuery(String mccode,
			String sccode, String stepcode,String mcprodcode) {
		SqlRowList rs ;
		String craftcode="";
		Map<String,Object> map = new HashMap<String, Object>();
		rs = baseDao.queryForRowSet("select * from  makecraft inner join makecraftdetail on mc_id=mcd_mcid  where mc_code='"+mccode+"' and mcd_stepcode='"+stepcode+"'");
		if(rs.next()){		
			craftcode=rs.getString("mc_craftcode");
			map.put("mcd_inqty", rs.getDouble("mcd_inqty"));
			rs = baseDao.queryForRowSet("select  NVL(cd_ifinput,0) cd_ifinput from Craft left join CraftDetail on cr_id=cd_crid where cr_code='"+craftcode+"' and cd_stepcode='"+stepcode+"'");
			if(rs.next()){
				if(rs.getInt("cd_ifinput") == 0){ 
					BaseUtil.showError("工序编号："+stepcode+"不属于上料工序!");
				}
			}
		}else{
			BaseUtil.showError("工序编号："+stepcode+"不属于此车间作业单工序!");
		} 				
		rs = baseDao.queryForRowSet("select sp_id,sp_soncode,pr_detail,pr_spec,sp_repcode,pr_id,'未采集' if_pick,sp_type from stepproduct left join product on pr_code=sp_soncode where sp_stepcode=?  And sp_mothercode=? order by SP_DETNO asc",stepcode,mcprodcode);
		if(rs.next()){
			map.put("datas", pdaCommonDao.changeKeyToLowerCase(rs.getResultList()));
		}
		return map;		
	}

	/**
	 * 
	 */
	@Override
	public List<Map<String,Object>> checkCraftMaterialGet(String mscode,String mccode, String licode, String sccode,
			String stepcode,boolean ifGet) {
		SqlRowList rs;
		//判断录入的序列号是否存在makeserial 的ms_code  条件ms_makecode=mc_makecode				
		rs = baseDao.queryForRowSet("select ms_nextstepcode, NVL(ms_status,0) ms_status ,ms_stepcode ,ms_outboxcode from makeSerial where ms_sncode=? and ms_mccode=?",mscode,mccode);
		if(rs.next()){
			if(ifGet){
				if(rs.getInt("ms_status") == 2){
					BaseUtil.showError("序列号："+mscode+"错误，序列号已完成上料采集");
				}
				//判断序列号的nextstepcode 是否等于 stepcode
				if(!makeCraftDao.ifNextStepcode(stepcode, mscode,mccode)){
					BaseUtil.showError("序列号："+mscode+",当前工序不是:"+stepcode);
				}
			}else{
				if(rs.getInt("ms_status") == 1 && !stepcode.equals(rs.getString("ms_stepcode"))){					
				    BaseUtil.showError("序列号："+mscode+",当前工序不是:"+stepcode);
				}else if(rs.getInt("ms_status") == 0){
					//判断序列号的nextstepcode 是否等于 stepcode
					if(!makeCraftDao.ifNextStepcode(stepcode, mscode,mccode)){
						BaseUtil.showError("序列号："+mscode+",当前工序不是:"+stepcode);
					}					
					//判断是否有上过料
					int cn = baseDao.getCount("select count(1) cn from craftMaterial where cm_mscode='"+mscode+"' and cm_mccode='"+mccode+"' and cm_stepcode='"+stepcode+"'");
				    if(cn == 0 ){
				    	BaseUtil.showError("序列号："+mscode+",未上料无需下料");
				    }
				}else if(rs.getInt("ms_status") == 2){
					BaseUtil.showError("序列号："+mscode+",已经包装不允许操作");
				}
			}			
		}else{
			BaseUtil.showError("序列号："+mscode+"错误，不存在制造单工序表中");
		}		
		Object ob = baseDao.getFieldDataByCondition("makeCraft", "mc_prodcode", "mc_code='"+mccode+"'");
		rs = baseDao.queryForRowSet("select A.sp_id sp_id,A.sp_soncode sp_soncode,C.pr_detail pr_detail,C.pr_spec pr_spec,A.sp_repcode sp_repcode,C.pr_id pr_id,case when nvl(B.if_pick,' ')=' ' then '未采集' ELSE  B.if_pick END if_pick,A.sp_type from stepproduct A  left join product C on C.pr_code=A.sp_soncode left join(select sp_id,'已采集' if_pick from stepproduct left join "+
				"product on pr_code=sp_soncode left join CraftMaterial on cm_spid=sp_id "+
				"left join makeCraft on cm_mccode=mc_code and sp_stepcode=cm_stepcode "+
				"where sp_stepcode=? And cm_mscode=? and mc_code=? and sp_mothercode=?) B on B.sp_id=A.sp_id where  A.sp_mothercode=? "+
				"and A.sp_stepcode=? order by A.SP_DETNO asc",stepcode,mscode,mccode,ob,ob,stepcode);
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}
		return null;
	}
	
	/**
	 * 上料
	 */
	@Override
	@Transactional
	public String getCraftMaterial(String mscode,String mccode, String licode, String sccode,
			String stepcode,String barcode,int sp_id) {
		//不允许重复上料
		SqlRowList rs = baseDao.queryForRowSet("select count(0) cn from Craftmaterial where cm_mccode=? and cm_mscode=? and cm_barcode=?",mccode,mscode,barcode);
		if(rs.next() && rs.getInt("cn") > 0){
			BaseUtil.showError("条码号或序列号："+barcode+"重复上料！");
		}
		//提示条码不允许重复上料
		rs = baseDao.queryForRowSet("select count(0) cn from Craftmaterial where cm_barcode=?",barcode);
		if(rs.next() && rs.getInt("cn") > 0){
			BaseUtil.showError("条码号："+barcode+"重复上料！");
		}
	   //插入表craftMaterial
       baseDao.execute("insert into Craftmaterial (cm_id,cm_mccode,cm_makecode,cm_maid,cm_maprodcode,cm_soncode,cm_mscode,cm_sncode,cm_stepcode,cm_stepname,cm_craftcode,"+
                     "cm_craftname,cm_barcode,cm_inqty,cm_indate,cm_inman,cm_linecode,cm_wccode,cm_sourcecode,cm_spid)"+
                     "select Craftmaterial_seq.nextval,mc_code,mc_makecode,mc_maid,mc_prodcode,sp_soncode,'"+mscode+"','"+mscode+"',mcd_stepcode,mcd_stepname,mc_craftcode,"+
                     "mc_craftname,'"+barcode+"',1,sysdate,'"+SystemSession.getUser().getEm_name()+"','"+licode+"',mc_wccode,'"+sccode+"','"+sp_id+"' from makeCraft left join makecraftdetail on mcd_mccode=mc_code "+
                     " left join stepproduct on sp_stepcode=mcd_stepcode  where mc_code='"+mccode+"'and sp_id="+sp_id+" and mcd_stepcode='"+stepcode+"'");
       //判断是否采集完成
       rs = baseDao.queryForRowSet("select count(0) cn  from stepProduct left join makeCraft on mc_prodcode=sp_mothercode left join CraftMaterial on cm_spid=sp_id where sp_stepcode='"+stepcode+"' and mc_code='"+mccode+"' and sp_id not in (select distinct cm_spid from CraftMaterial where cm_mccode='"+mccode+"' and cm_stepcode='"+stepcode+"' and cm_mscode='"+mscode+"')");
       if(rs.next() && rs.getInt("cn") > 0){//未完成采集   	
    	 //插入记录生产进度表
   		baseDao.execute("insert into MakeProcess(mp_id,mp_makecode,mp_maid,mp_mscode,mp_sncode,mp_stepcode,mp_stepname,"+
                " mp_craftcode,mp_craftname,mp_kind,mp_result,mp_indate,mp_inman,mp_wccode,mp_linecode,mp_sourcecode)"+
                " select MakeProcess_seq.nextval, Mc_makecode,mc_maid,'"+mscode+"','"+mscode+"',mcd_stepcode,mcd_stepname,"+
                " mc_craftcode,mc_craftname,'上料','上料成功',sysdate,'"+SystemSession.getUser().getEm_name()+"',mc_wccode,sc_linecode,sc_code from makeCraft left join makecraftdetail on mcd_mccode=mc_code left join source on sc_code=mc_sourcecode where mc_code='"+mccode+"'and mcd_stepcode='"+stepcode+"'");
      
       }else{
    	   //更新MakeSerial生成中[1]
    	   baseDao.updateByCondition("makeSerial", "ms_status=1", "ms_sncode='"+mscode+"' and ms_makecode=(select mc_makecode from makeCraft where mc_code='"+mccode+"')");
    	  // baseDao.updateByCondition("makecraftdetail", "mcd_inqty= mcd_inqty+1", "mcd_mccode='"+mccode+"' and mcd_stepcode='"+stepcode+"'");
    	   makeCraftDao.updateMakeMessage("上料", "上料成功", mscode, mccode, stepcode);
    	   return "success";
       }
       return null;
	}
	
	/**
	 * 取消上料
	 */
	@Override
	public String backCraftMaterial(String mscode,String mccode, String sccode,String barcode) {
		SqlRowList rs0 = baseDao.queryForRowSet("select cm_id,cm_stepcode,cm_spid from Craftmaterial where cm_mccode=? and cm_mscode=? and cm_barcode=? and cm_sourcecode=?",mccode,mscode,barcode,sccode);
		if(rs0.next()){//已上料,删除CraftMaterial记录
			baseDao.execute("delete from Craftmaterial where cm_id="+rs0.getInt("cm_id"));
			baseDao.execute("insert  into MakeProcess(mp_id,mp_makecode,mp_maid,mp_mscode,mp_sncode,mp_stepcode,mp_stepname,"
					+ " mp_craftcode,mp_craftname,mp_kind,mp_result,mp_indate,mp_inman,mp_wccode,mp_linecode,mp_sourcecode)"
					+ " Select MakeProcess_seq.nextval, mc_makecode,mc_maid,'"+mscode+"','" + mscode
					+ "',mcd_stepcode,mcd_stepname," + " mc_craftcode,mc_craftname,'下料'||'"+barcode+"','下料成功',sysdate,'"
					+ SystemSession.getUser().getEm_name() + "',mc_wccode,sc_linecode,sc_code from makeCraft left join makeCraftDetail"
					+ " on mc_id=mcd_mcid left join source on sc_stepcode=mcd_stepcode where mc_code='" + mccode + "' and mcd_stepcode='"
					+ rs0.getString("cm_stepcode") + "' and rownum=1");	
			//最后一道工序				
			Object ob = baseDao.getFieldDataByCondition("makeSerial", "ms_id", "ms_sncode='"+mscode+"' and ms_status=2");
			if(ob != null){			 
			   baseDao.updateByCondition("makeCraft", "mc_madeqty=mc_madeqty-1", "mc_code='"+mccode+"'");
			}
			baseDao.updateByCondition("makeSerial","ms_status=0,ms_nextstepcode=ms_stepcode","ms_sncode='"+mscode+"' and ms_status=1");
			SqlRowList rs1 = baseDao.queryForRowSet("select count(0) cn from craftMaterial where cm_mccode=? and cm_stepcode=? and cm_sncode=?",mccode,rs0.getString("cm_stepcode"),mscode);
			if( !rs1.next() && rs1.getInt("cn") == 0){		
			   //完成工序下料更新计数值	
				baseDao.updateByCondition("makecraftdetail", "mcd_inqty= mcd_inqty-1,mcd_outqty=mcd_outqty-1,mcd_okqty=mcd_okqty-1", "mcd_mccode='"+mccode+"' and mcd_stepcode='"+rs0.getString("cm_stepcode")+"'");				
				return "success"+rs0.getString("cm_spid");
			}
		}else{
			BaseUtil.showError("条码或者序列号:"+barcode+"未上料!");
		}
		return rs0.getString("cm_spid");
	}
	
	@Override
	public Map<String, Object> getBarDescription(String condition) {
		SqlRowList rs  = baseDao.queryForRowSet("select bar_prodcode,pr_id,pr_ifbarcodecheck ,pr_exbarcode ,bs_lenprid,bs_prtypepreix,bar_code from barcode left join product on bar_prodcode=pr_code left join barcodeset on 1=1  where bar_code='"+condition+"' and bs_type='BATCH'");
	    if(rs.next()){
	    	return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
	    }else{
	      BaseUtil.showError("条码号不存在!");	
	    }
		return null;
	}

}
