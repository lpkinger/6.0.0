package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeCraftDao;
import com.uas.erp.service.pm.StepTestService;
import com.uas.pda.dao.PdaCommonDao;

@Service("StepTestService")
public class StepTestServiceImpl implements StepTestService {
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private PdaCommonDao pdaCommonDao;
    @Autowired
    private MakeCraftDao makeCraftDao;
	@Override
	public Map<String, Object> getFormStore(String condition) {
		// TODO Auto-generated method stub
		SqlRowList rs;
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(condition);
		//判断资源编号是否存在		
        Object ob = baseDao.getFieldDataByCondition("source", "sc_statuscode", "sc_code='"+map.get("sc_code")+"'");
        if(ob == null){
        	BaseUtil.showError("资源编号："+map.get("sc_code")+"不存在！");
        }else if(!(ob.toString().equals("AUDITED"))){
        	BaseUtil.showError("资源编号："+map.get("sc_code")+"未审核！");
        }
       //判断作业单是否已审核且mc_madeqty<mc_qty。
        rs = baseDao.queryForRowSet("select mc_statuscode,case when(NVL(mc_madeqty,0)<NVL(mc_qty,0)) then 1 else 0 end en,mc_id from makeCraft where mc_code='"+map.get("mc_code")+"'");
        if(rs.next()){
        	if(!rs.getString("mc_statuscode").equals("AUDITED")){
        		BaseUtil.showError("作业单："+map.get("mc_code")+"未审核！");
        	}else if(rs.getInt("en") == 0){
        		BaseUtil.showError("作业单:"+map.get("mc_code")+"已经完成生产");
        	}
        }else{
        	BaseUtil.showError("作业单："+map.get("mc_code")+"不存在！");
        }
       //判断工序是否存在于makecraftdetail 的mcd_stepcode。
        rs = baseDao.queryForRowSet("select  count(0) cn from makeCraft left join makeCraftdetail on mc_id=mcd_mcid where mc_code='"+map.get("mc_code")+"' and mcd_stepcode='"+map.get("st_code")+"'");
       if(!rs.next() || rs.getInt("cn") == 0){
    	   BaseUtil.showError("工序编号："+map.get("st_code")+"不存在作业单中");
       }
		rs = baseDao.queryForRowSet("select sc_code ,sc_name , st_code ,st_name ,mc_code ,mc_makecode ,mc_prodcode ,pr_detail ,mc_qty ,mcd_inqty ,mc_qty-mcd_inqty "+
                       " from makeCraft left join makecraftdetail  on mc_id=mcd_mcid left join  product on mc_prodcode=pr_code left join step"+
                       " on mcd_stepcode=st_code  left join source on sc_stepcode=st_code where sc_code=? and mc_code=? and st_code=?",map.get("sc_code"),map.get("mc_code"),map.get("st_code"));
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
		}
		return null;
	}

	@Override
	public List<Map<String,Object>> checkStep(String makecode, String stepcode, String mscode,String mccode) {
		// TODO Auto-generated method stubR
		SqlRowList rs;
		rs = baseDao.queryForRowSet("select  NVL(cd_iftest,0) cd_iftest,cr_code from Craft left join CraftDetail on cr_id=cd_crid left join makeSerial on ms_craftcode=cr_code where ms_sncode='"+mscode+"' and cd_stepcode='"+stepcode+"'");
		if(rs.next()){
			if(rs.getInt("cd_iftest") == 0){ 
				BaseUtil.showError("工序编号："+stepcode+"不属于测试工序!");
			}
		}else{
			BaseUtil.showError("工序编号："+stepcode+"不存在工艺路线:"+rs.getString("cr_code")+"!");
		}
		//判断makeserial.ms_nextstepcode 是否等于当前工序，如果不是则清空，提示栏显示序列号{?}当前工序不是{?}
		rs = baseDao.queryForRowSet("select ms_nextstepcode from makeSerial where ms_sncode=? and ms_makecode=?",mscode,makecode);
		if(rs.next()){
			if(!makeCraftDao.ifNextStepcode(stepcode, mscode,mccode)){
				BaseUtil.showError("序列号："+mscode+"当前工序不是："+stepcode);
			}
		}else{
			BaseUtil.showError("序列号："+mscode+"，不存在或者不属于制造单："+makecode);
		}
		rs = baseDao.queryForRowSet("Select mb_id,mb_sncode,mb_badcode,bc_name,bc_groupcode,mb_badremark,bc_note,bc_dutyman,mb_status"+
				 " from Makebad left join badcode on mb_badcode=bc_code where mb_sncode=? ",mscode);
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}
		return null;
	}

	@Override
	public Map<String, Object> confirmQualified(String mcd_stepcode, String mc_code,
			String sc_code, String ms_code,String makecode) {
		// TODO Auto-generated method stub
		//List<Map<String,Object>> list = checkStep( makecode,mcd_stepcode, ms_code,mc_code);
		
		//测试合格通过之前判断该序号是否存在有不良记录
		int count = baseDao.getCount("select count(1) from makeBad where mb_sncode='"+ms_code+"'and mb_status not in (1,-1)");
		if(count > 0 ){
			BaseUtil.showError("序列号:"+ms_code+"存在不良记录，不允许测试合格通过!");
		}
		makeCraftDao.updateMakeMessage("测试", "合格", ms_code, mc_code, mcd_stepcode);
		SqlRowList rs = baseDao.queryForRowSet("select sc_code,sc_name,mc_code,mcd_inqty,(mc_qty-mcd_inqty) mc_restqty,mc_qty from makeCraft left join makeCraftDetail" +
				" on mc_id=mcd_mcid left join source on sc_stepcode=mcd_stepcode where sc_code='"+sc_code+"' and mc_code='"+mc_code+"'");
		if(rs.next()){
		   return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
		}
		return null;
	}
    
	/**
	 * 保存不良原因
	 */
	@Override
	public void saveBadReason(String mcd_stepcode, String mc_code,
			String sc_code, String ms_code, String bc_reason,String bc_remark) {
		// TODO Auto-generated method stub
		int count = baseDao.getCount("select count(1) from makebad where mb_badcode='"+bc_reason+"' and mb_sncode='"+ms_code+"'");
		if(count == 1){
			BaseUtil.showError("不良原因重复!");
		}
		baseDao.execute("insert into makebad(mb_id,mb_makecode,mb_mscode,mb_sncode,mb_inman,mb_indate,mb_stepcode," +
				" mb_sourcecode,mb_badcode,mb_badtable,mb_soncode,mb_status,mb_badremark) " +
				" select makebad_seq.nextval,mc_makecode,ms_code,ms_sncode,'"+SystemSession.getUser().getEm_name()+"',sysdate,'"+mcd_stepcode+"','"+
				 sc_code+"','"+bc_reason+"',mc_table,sp_soncode,'0','"+bc_remark+"' from makeCraft left join makeSerial on ms_makecode=mc_makecode left join stepProduct on sp_mothercode=mc_prodcode and sp_stepcode=ms_nextstepcode" +
				 		" where mc_code='"+mc_code+"' and ms_sncode='"+ms_code+"' and ms_nextstepcode='"+mcd_stepcode+"'");
		//更新MAKEserial 测试采集结果为不良则更新成3，3待维修
		baseDao.updateByCondition("makeSerial", "ms_status=3", "ms_sncode='"+ms_code+"' and ms_mccode='"+mc_code+"'");
	}
   
    /**
     * 确认返修工序
     * @return 
     */
	@Override
	public Map<String, Object> confirmRepairStep(String mcd_stepcode, String mc_code,
			String sc_code, String ms_code, String st_rcode) {
		// TODO Auto-generated method stub
		//判断是否有不良记录，如果有则转返修，没有则不转
		int count = baseDao.getCount("select count(1) from makeBad where mb_sncode='"+ms_code+"' and mb_status=0");
		if(count == 0 ){
			BaseUtil.showError("序列号:"+ms_code+"没有不良记录或者维修完成，不允许转返修!");
		}
		Object []obs = baseDao.getFieldsDataByCondition("step", new String []{"st_statuscode","nvl(st_ifrepair,0)"}, "st_code='"+st_rcode+"'");
		if(obs != null){
			if(obs[0] != null && !(obs[0].toString().equals("AUDITED"))){
				BaseUtil.showError("工序"+st_rcode+"未审核!");
			}
			if(obs[0] != null && obs[0].toString().equals("0")){
				BaseUtil.showError("工序"+st_rcode+"不属于返修工序!");
			}
		}else{
			BaseUtil.showError("工序"+st_rcode+"不存在!");
		}
		baseDao.execute("Update makeserial set ms_nextstepcode='"+st_rcode+"',ms_stepcode='"+mcd_stepcode+"' where ms_sncode='"+ms_code+"'");
		//如果已经采集的工序，返修再次采集不需要更新，通过ms_paststep记录
		boolean bo = makeCraftDao.checkHaveGetStep(ms_code, mcd_stepcode);
	    if(!bo){//未采集
			//更新makeCraftDetail inqty outqty okqty
			baseDao.execute("Update makecraftdetail set mcd_inqty=mcd_inqty+1,mcd_outqty=mcd_outqty+1" +
					" where mcd_id=(select mcd_id from makeCraft left join makecraftdetail on mc_id=mcd_mcid where mc_code='"+mc_code+"' and mcd_stepcode='"+mcd_stepcode+"')");//更新已经采集的工序
			//更新已经采集的工序
			baseDao.execute("Update makeserial 	set ms_paststep = ms_paststep ||',"+mcd_stepcode+"' where ms_sncode='"+ms_code+"'");
		}			
		Object ob1 = baseDao.getFieldDataByCondition("makeSerial left join makeCraft on ms_craftcode=mc_craftcode", "ms_code", "ms_sncode='"+ms_code+"' and mc_code='"+mc_code+"'");
		if(ob1 == null){//返修工序
			//插入记录到MakeProcess
			baseDao.execute("insert  into MakeProcess(mp_id,mp_makecode,mp_maid,mp_mscode,mp_sncode,mp_stepcode,mp_stepname," +
				" mp_craftcode,mp_craftname,mp_kind,mp_result,mp_indate,mp_inman,mp_wccode,mp_linecode,mp_sourcecode)"+
				" Select MakeProcess_seq.nextval, mc_makecode,mc_maid,'"+ms_code+"','"+ms_code+"',ms_nextstepcode,sc_stepname," +
				" ms_craftcode,cr_name,'测试','不合格',sysdate,'"+SystemSession.getUser().getEm_name()+"',mc_wccode,sc_linecode,sc_code from makeCraft " +
				" left join makeSerial on ms_mccode=mc_code left join source on sc_stepcode=ms_nextstepcode left join craft on cr_code=ms_craftcode where mc_code='"+mc_code+"' and ms_nextstepcode='"+mcd_stepcode+"' and ms_sncode='"+ms_code+"'");
		}else{
			//插入记录到MakeProcess
			baseDao.execute("insert  into MakeProcess(mp_id,mp_makecode,mp_maid,mp_mscode,mp_sncode,mp_stepcode,mp_stepname," +
				" mp_craftcode,mp_craftname,mp_kind,mp_result,mp_indate,mp_inman,mp_wccode,mp_linecode,mp_sourcecode)"+
				" Select MakeProcess_seq.nextval, mc_makecode,mc_maid,'"+ms_code+"','"+ms_code+"',mcd_stepcode,mcd_stepname," +
				" mc_craftcode,mc_craftname,'测试','不合格',sysdate,'"+SystemSession.getUser().getEm_name()+"',mc_wccode,sc_linecode,sc_code from makeCraft left join makeCraftDetail" +
				" on mc_id=mcd_mcid left join source on sc_stepcode=mcd_stepcode where mc_code='"+mc_code+"' and mcd_stepcode='"+mcd_stepcode+"'");			
		}
		//判断采集点工序是否为扣料工序
		ob1 = baseDao.getFieldDataByCondition("makeSerial left join Craft on cr_code=ms_craftcode left join CraftDetail on cd_crid=cr_id", "cd_stepcode", "ms_sncode='"+ms_code+"' and cd_stepcode='"+mcd_stepcode+"' and cd_ifreduce=-1");
		if(ob1 != null){
			//如果是采集点工序是扣料工序，更新makesmtlocation 的msl_remainqty=msl_remainqty-NVL(msl_baseqty,0) where  msl_mccode=作业单号
			baseDao.updateByCondition("makesmtlocation", "msl_remainqty=msl_remainqty-NVL(msl_baseqty,0)", "msl_mccode ='"+mc_code+"' and NVL(msl_status,0)=0");
			//判断站位用量是否达到需求量，如果达到则自动生成领料单
			SqlRowList rs = baseDao.queryForRowSet("select sum(count(1)) sm from makeSMtlocation where msl_mccode='"+mc_code+"' group by msl_location,msl_needqty having sum(msl_getqty)-sum(msl_remainqty)< msl_needqty order by msl_location");
			if( !rs.next() || rs.getInt("sm") == 0){
				//判断是否根据作业单生成了领料单
				ob1 = baseDao.getFieldDataByCondition("prodInOut", "pi_inoutno", "pi_sourcecode='"+mc_code+"'");
				if(ob1 == null){
					makeCraftDao.turnProdOut(mc_code);
				}
			}
		}	
		SqlRowList rs = baseDao.queryForRowSet("select sc_code,sc_name,mc_code,mcd_inqty,(mc_qty-mcd_inqty) mc_restqty,mc_qty from makeCraft left join makeCraftDetail" +
			" on mc_id=mcd_mcid left join source on sc_stepcode=mcd_stepcode where sc_code='"+sc_code+"' and mc_code='"+mc_code+"'");
		if(rs.next()){
		     return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getBadCode(String condition) {
		// TODO Auto-generated method stub
		SqlRowList rs = baseDao.queryForRowSet("select bc_code, bc_name from badcode where bc_groupcode='"+condition+"' and bc_statuscode='AUDITED'");
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}
		return null;
	}

	@Override
	public void deleteTestBadCode(String condition) {
		// TODO Auto-generated method stub
		List<Map<Object,Object>> list = BaseUtil.parseGridStoreToMaps(condition);
		String ids = CollectionUtil.pluckSqlString(list, "mb_id");
		Object ob = baseDao.getFieldDataByCondition("makeBad", "wm_concat(mb_badcode)", "mb_id  in ("+ids+")and mb_status=1");
		if(ob != null){//已经维修过的，不允许删除
			BaseUtil.showError("不良记录:"+ob+"已经维修完成，不允许删除!");
		}
		baseDao.deleteByCondition("makeBad", "mb_id in("+ids+")");
		//如果没有不良原因了则更新
	}

	@Override
	public Map<String, Object> getSourceM(String condition) {
		// TODO Auto-generated method stub		
		SqlRowList rs = baseDao.queryForRowSet("select sc_code ,sc_name , st_code , st_name,st_rstepcode "+
                " from  source left join step on st_code=sc_stepcode where sc_code=? ",condition);
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
		}else{
			BaseUtil.showError("资源编号:"+condition+"错误!，不存在或未审核!");
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getBadGroup() {
		// TODO Auto-generated method stub
		SqlRowList rs = baseDao.queryForRowSet("select bg_code,bg_name from badGroup where bg_statuscode='AUDITED'");
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}
		return null;
	}


}
