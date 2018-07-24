package com.uas.pda.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MakePrepareService;
import com.uas.pda.dao.PdaCommonDao;
import com.uas.pda.service.PdaMakePrepareService;

@Service("pdaMakePrepareService")
public class PdaMakePrepareServiceImpl implements PdaMakePrepareService{
      @Autowired
      private BaseDao baseDao;
      @Autowired
      private PdaCommonDao pdaCommonDao;
      @Autowired
      private MakePrepareService makePrepareService;
	@Override
	public Map<String,Object> searchMp(String mp_code,String type) {
		// TODO Auto-generated method stub
		SqlRowList rs;
		rs = baseDao.queryForRowSet("select mp_id,mp_code,mp_maid,mp_whcode,mp_statuscode,mp_status,ma_code from makePrepare left join make on ma_id=mp_maid where mp_code='"+mp_code+"'");
		if(rs.next()){
			if(!rs.getString("mp_statuscode").equals("AUDITED")){
				BaseUtil.showError("备料单号："+mp_code+",未审核!");
			}
			if(rs.getString("mp_status").equals("已上线")){
				BaseUtil.showError("备料单号："+mp_code+",已上线，不允许修改!");
			}
			if(type.equals("barcode")){
				if(rs.getString("mp_status").equals("已上飞达")){
					BaseUtil.showError("备料单号："+mp_code+",已上飞达!");
				}				
			}	
			if(type.equals("feeder")){
				if(rs.getString("mp_status").equals("已审核")){
					BaseUtil.showError("备料单号："+mp_code+",未备料,请先备料!");
				}	
			}			
			return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
		}else{
			BaseUtil.showError("备料单号："+mp_code+",不存在!");
		}
		return null;
	}
	@Override
	public List<Map<String, Object>> barcodeList(int mp_id) {
		// TODO Auto-generated method stub
		SqlRowList rs = baseDao.queryForRowSet("select mp_id,mp_maid,md_devcode,md_status,md_qty,md_barcode,md_prodcode,md_mmdetno,md_detno,md_mpid,md_id from MakePrepare left join MakePrepareDetail on mp_id=md_mpid where md_mpid="+mp_id);
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}
		return null;
	}
	@Override
	public List<Map<String, Object>> needPreparedList(int mp_id) {
		// TODO Auto-generated method stub
		SqlRowList rs = baseDao.queryForRowSet("select pr_wiplocation,md_prodcode,md_detno,pr_detail,pr_spec,md_location,md_baseqty,md_needqty,md_id,mp_id,md_repcode,NVL(md_qty,0) md_qty,md_lastlocation from MakePrepare left join MakePrepareDetail on mp_id=md_mpid left join product on pr_code=md_prodcode where mp_id="+mp_id+" and NVL(md_qty,0)=0 order by md_location asc");
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}
		return null;
	}
	@Override
	public Map<String,Object> barGet(String data) {
		// TODO Auto-generated method stub
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(data);
		String barcode = map.get("barcode").toString(),  whcode = map.get("whcode").toString();//,  md_location = map.get("md_location").toString();
		int maid = Integer.valueOf(map.get("maid").toString()),  mpid = Integer.valueOf(map.get("mpid").toString());
		return makePrepareService.getBar(barcode, whcode, maid, mpid);	
	}
	@Override
	public Map<String, Object> barBack(String barcode, int mpid) {
		// TODO Auto-generated method stub
		return makePrepareService.returnBar(barcode, mpid);
	}	
	@Override
	public List<Map<String, Object>> getMpcodeList(String type) {
		// TODO Auto-generated method stub
		SqlRowList rs ;
		if(type.equals("barcode")){
			rs = baseDao.queryForRowSet("select mp_id,mp_code,mp_makecode,mp_mccode,mp_linecode,mp_whcode from makeprepare where mp_statuscode='AUDITED' and mp_status  in('已审核','已备料') order by mp_code asc ");
			if(rs.next()){
				return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
			}
		}else if(type.equals("feeder")){
			rs = baseDao.queryForRowSet("select mp_id,mp_code,mp_makecode,mp_mccode,mp_linecode,mp_whcode from makeprepare where mp_statuscode='AUDITED' and mp_status in('已备料','已上飞达')");
			if(rs.next()){
				return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
			}
		}
		return null;
	}
	@Override
	public String makePrepareFeederGet(String data) {
		// TODO Auto-generated method stub		
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(data);
		String fe_code = map.get("fe_code").toString();
		int mp_id = Integer.valueOf(map.get("mp_id").toString());
		//判断备料单状态
		checkMPstatuscode(mp_id);
		int cn = baseDao.getCount("select count(1)cn  from  makePrepareDetail  where md_location='"+map.get("md_location")+"' and md_mpid="+map.get("mp_id")+" and md_fecode is not null");
		if(cn > 0){
			BaseUtil.showError("站位："+map.get("md_location")+",已经上飞达!");
		}
		//飞达编号采集后判断规格是否等于备料单明细行的飞达规格
		Object code = baseDao.getFieldDataByCondition("feeder", "fe_statuscode", "fe_code='"+fe_code+"'");
		if(code != null){
			if(!code.equals("AUDITED")){
				BaseUtil.showError("飞达编号："+fe_code+",未审核!");
			}
		}else{
			BaseUtil.showError("飞达编号："+fe_code+",不存在!");
		}
		code = baseDao.getFieldDataByCondition("feeder","fe_code", "fe_code='"+fe_code+"' and fe_spec='"+map.get("md_fespec")+"'");
		if(code == null){
			BaseUtil.showError("飞达编号："+fe_code+"的规格与当前明细行所需的飞达规格不符!");
		}else{
			cn = baseDao.getCount("select count(1)cn  from  makePrepareDetail  where md_fecode='"+fe_code+"' and md_mpid="+mp_id);
			if(cn > 0){
				BaseUtil.showError("飞达编号："+fe_code+",已经上料!");
			}
		}
		//确认飞达上料
		baseDao.updateByCondition("makePrepareDetail", "md_fecode='"+fe_code+"'", "md_location='"+map.get("md_location")+"' and md_mpid="+mp_id);	
		//判断是否所有明细都上飞达，更新mp_status='已上飞达'
		cn = baseDao.getCount("select count(1) cn from makePrepareDetail where md_mpid="+mp_id+" and md_fecode is null");
		if(cn == 0){
			baseDao.updateByCondition("makePrepare", "mp_status='已上飞达'", "mp_id="+mp_id);
			return "完成飞达上料!";
		}
		return null;
	}
	@Override
	public List<Map<String, Object>> preparedFeederList(int mp_id) {
		// TODO Auto-generated method stub
		SqlRowList rs = baseDao.queryForRowSet("select md_barcode,md_prodcode,md_location,md_baseqty,md_needqty,md_id,md_repcode,md_fespec from  MakePrepareDetail where md_mpid="+mp_id+" and NVL(md_qty,0)>0 ");
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}
		return null;
	}
	@Override
	public void makePrepareFeederBack(String bar_code, int mp_id) {
		// TODO Auto-generated method stub
		//判断备料单状态
		checkMPstatuscode(mp_id);
		//如果是已上飞达的则将飞达编号更新为空，提示取消上料成功。否则提示料卷号错误，或此料未上飞达
		Object []obs = baseDao.getFieldsDataByCondition("makePrepareDetail", new String[]{"md_barcode","md_fecode"}, "md_barcode='"+bar_code+"' and md_mpid="+mp_id);
		if( obs != null){
			if(obs[1] != null ){//将飞达编号更新为空
				baseDao.updateByCondition("makePrepareDetail", "md_fecode=''", "md_mpid="+mp_id+" and md_barcode='"+bar_code+"'");
			    //更新mp_status='已备料'
				baseDao.updateByCondition("makePrepare", "mp_status='已备料'","mp_id="+mp_id+" and mp_status='已上飞达'");
			}else{
				BaseUtil.showError("料卷号："+bar_code+",未上飞达！");
			}
		}else{
			BaseUtil.showError("料卷号："+bar_code+",错误不存在该备料单中！");
		}
	}
	/**
	 * 判断备料单状态是否能够上飞达
	 * @param mp_id
	 */
	private void checkMPstatuscode (int mp_id){
		//判断该备料单状态
		Object []obs = baseDao.getFieldsDataByCondition("makePrepare", new String []{"mp_status","mp_statuscode"}, "mp_id="+mp_id);
		if(obs != null){
			if(!obs[1].toString().equals("AUDITED")){
				BaseUtil.showError("备料单未审核，请先审核再进行备料相关操作!");
			}
			if(obs[0].toString().equals("已审核")){
				BaseUtil.showError("备料单还未备料，请先备料在进行相关操作!");
			}
			if(obs[0].toString().equals("已上线")){
				BaseUtil.showError("备料单已上线，不允许修改!");
			}
		}
	}
	@Override
	public void updateChecked(String data) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		int mc_id = Integer.valueOf(map.get("id").toString());
		String code = map.get("code").toString();
		if(map.get("type").equals("location")){//站位
			baseDao.updateByCondition("makeSMTLocation", "msl_ifcheck=1", "msl_mcid="+mc_id+" and msl_location='"+code+"'");
		}else if(map.get("type").equals("fecode")){//飞达编号
			baseDao.updateByCondition("makeSMTLocation", "msl_ifcheck=1", "msl_mcid="+mc_id+" and msl_fecode='"+code+"'");
		}else if(map.get("type").equals("barcode")){//料卷号
			baseDao.updateByCondition("makeSMTLocation", "msl_ifcheck=1", "msl_mcid="+mc_id+" and msl_barcode='"+code+"'");
		}
	}
}
