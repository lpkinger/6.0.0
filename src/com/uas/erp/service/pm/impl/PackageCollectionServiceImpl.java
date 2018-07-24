package com.uas.erp.service.pm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeCraftDao;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.service.pm.PackageCollectionService;
import com.uas.pda.dao.PdaCommonDao;

@Service("packageCollectionService")
public class PackageCollectionServiceImpl implements PackageCollectionService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PdaCommonDao pdaCommonDao;
	@Autowired 
	private  VerifyApplyDao verifyApplyDao;
	@Autowired 
	private MakeCraftDao makeCraftDao;
	@Override
	public List<Map<String, Object>> loadQueryGridStore(String caller,
			String condition) {
		SqlRowList rs;
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(condition);
		//查询之前检测
		checkBefore(map.get("mc_makecode"),map.get("sc_code"),map.get("pa_code"),map.get("mc_code"));		
		rs = baseDao.queryForRowSet("select * from packageDetail left join package on pa_id=pd_paid where pa_outboxcode='"+map.get("pa_code")+"'");
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}
		return null;
	}
	
	@Override
	public Map<String, Object> generatePackage(double pa_totalqty,
			String pa_prodcode, String pr_id, String pa_makecode,String pa_outboxcode) {
		   Map<String,Object> map =  new HashMap<String, Object>();
		if(pa_outboxcode == null || pa_outboxcode.equals("")){
			String out_boxcode = verifyApplyDao.outboxMethod(pr_id,"1");
			int  pa_id = baseDao.getSeqId("PACKAGE_SEQ");
		    //插入记录Package	
		    baseDao.execute("insert into package (pa_id,pa_outboxcode,pa_prodcode,pa_packdate,pa_packageqty,pa_totalqty,pa_status,pa_indate,pa_makecode)values" +
		    		"("+pa_id+",'"+out_boxcode+"','"+pa_prodcode+"',sysdate,'"+pa_totalqty+"','"+pa_totalqty+"','0',sysdate,'"+pa_makecode+"')");	     
	        map.put("pa_code", out_boxcode);
	        map.put("pa_totalqty", pa_totalqty);
       }else{
    	   //判断箱号是否已经存在
    	   Object ob = baseDao.getFieldDataByCondition("package", "pa_id", "pa_outboxcode='"+pa_outboxcode+"'");
    	   if(ob != null){
    		   BaseUtil.showError("箱号["+pa_outboxcode+"]已经存在，不允许重复生成");
    	   }else{
    		   int  pa_id = baseDao.getSeqId("PACKAGE_SEQ");
	   		    //插入记录Package	
	   		    baseDao.execute("insert into package (pa_id,pa_outboxcode,pa_prodcode,pa_packdate,pa_packageqty,pa_totalqty,pa_status,pa_indate,pa_makecode)values" +
	   		    		"("+pa_id+",'"+pa_outboxcode+"','"+pa_prodcode+"',sysdate,'"+pa_totalqty+"','"+pa_totalqty+"','0',sysdate,'"+pa_makecode+"')");	
	   	        map.put("pa_code", pa_outboxcode);
	   	        map.put("pa_totalqty", pa_totalqty);
    	   }
       }		
		return map;
	}
	
	@Override
	public List<Map<String,Object>> getPackageDetail(String data){
		SqlRowList rs;
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(data);
		String st_code = map.get("st_code").toString(),ms_sncode = map.get("ms_sncode").toString(),
			makecode = map.get("mc_makecode").toString(),pa_code = map.get("pa_code").toString(),
			mc_code = map.get("mc_code").toString();
		//检测箱号，制造单号，资源编号一致性
		checkBefore(makecode,map.get("sc_code"),pa_code,map.get("mc_code"));		
		rs = baseDao.queryForRowSet("select ms_makecode,NVL(ms_outboxcode,'0') boxcode from makeSerial where ms_sncode='"+ms_sncode+"'");
		if(rs.next()){
			 if(!rs.getString(1).equals(makecode)){//校验工单号是否一致。
				BaseUtil.showError("序列号:"+ms_sncode+"与制造单号:"+makecode+"不一致!");
			}else if (!rs.getString(2).equals("0")){//校验是否已经装箱，ms_outboxcode是否有值。
				BaseUtil.showError("序列号:"+ms_sncode+"已经在箱号:"+rs.getString(2)+"内!");
			}
		}else{
			BaseUtil.showError("序列号:"+ms_sncode+"不存在!");
		}	
		if(!makeCraftDao.ifNextStepcode(st_code, ms_sncode,mc_code)){//校验该序列号的nextstep是否为当前工序。
			BaseUtil.showError("序列号:"+ms_sncode+"的当前工序不是:"+st_code);
		}		
		//当前装箱数量是否已经达到了总容量
		rs = baseDao.queryForRowSet("select count(0) cn  from package left join packagedetail on pa_id=pd_paid  where pa_outboxcode='"+pa_code+"' group by pa_totalqty having  pa_totalqty = SUM(pd_innerqty)" );
		if(rs.next() && rs.getInt("cn") > 0){
			BaseUtil.showError("箱号:"+pa_code+"当前装箱数量已经达到总容量!");
		}
		//插入记录到packageDetail，更新主表的箱内数量。刷新界面的已装箱数和剩余数	
       baseDao.execute("insert into packagedetail (pd_id,pd_paid,pd_outboxcode,pd_barcode,pd_innerqty)" +
       		   " select packagedetail_seq.nextval,pa_id,pa_outboxcode,'"+map.get("ms_sncode")+"',1 from package where pa_outboxcode='"+pa_code+"'");
     //插入记录到packageDetail，更新主表的箱内数量。刷新界面的已装箱数和剩余数	
       baseDao.execute("update makeSerial set ms_outboxcode='"+pa_code+"' where ms_sncode='"+ms_sncode+"'");
       //更新make**相关表数据
       makeCraftDao.updateMakeMessage("包装采集", "采集成功", ms_sncode, mc_code, st_code);     
       rs = baseDao.queryForRowSet("select * from package left join packageDetail on pa_id=pd_paid where pa_outboxcode='"+pa_code+"'");
	   if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
	   }
       return null;
	}
	
	/**
	 * 检测选择的资源编号，制造单号是否一致
	 * @param 
	 */
	private void checkBefore(Object makecode,Object sccode,Object outboxcode,Object mccode) {
		SqlRowList rs = baseDao.queryForRowSet("select mc_statuscode,sc_statuscode from makeCraft left join makeCraftDetail on mc_id=mcd_mcid left join source on sc_stepcode=mcd_stepcode where mc_code='"+mccode+"' and sc_code='"+sccode+"'");
		if(rs.next()){
			if(!rs.getString("sc_statuscode").equals("AUDITED")){
				BaseUtil.showError("资源编号："+sccode+"'未审核！");
			}
		}else{
			BaseUtil.showError("资源编号："+sccode+",与作业单号："+mccode+"不对应!");
		}
		rs = baseDao.queryForRowSet("select pa_makecode from package where pa_outboxcode='"+outboxcode+"'");
		if(rs.next()){
			if(!rs.getString("pa_makecode").equals(makecode)){
				BaseUtil.showError("箱号："+outboxcode+",与作业单对应的制造单号："+ makecode+"不对应!");
			}			
		}else{
			BaseUtil.showError("箱号："+outboxcode+"不存在！");
		}		
	}

	@Override
	public void clearPackageDetail(String caller, String outbox, String sncode) {
		//判断制造单状态，转收料或部分收料
		//判断包装箱内是否存在该序列号
		int cn = baseDao.getCount("select count(1) cn from package left join packagedetail on pa_id=pd_paid where pa_outboxcode='"+outbox+"' and pd_barcode='"+sncode+"'");
		if(cn == 0){
			BaseUtil.showError("清除失败，序列号："+sncode+"不在箱号:"+outbox+"内");			
		}
		Object[] ob0 = baseDao.getFieldsDataByCondition("makeSerial",new String []{"ms_status","ms_stepcode","ms_mccode"}, "ms_sncode='"+sncode+"'");
		//获取上一工序
		Object ob = baseDao.getFieldDataByCondition("makecraft left join makecraftdetail on mcd_mcid=mc_id", "mcd_stepcode", "mc_code='"+ob0[2]+"' and mcd_nextstepcode='"+ob0[1]+"'");
		//设置序列号的外箱号为空r
		baseDao.execute("update makeserial set ms_outboxcode='',ms_nextstepcode=ms_stepcode,ms_status=1,ms_stepcode=? where ms_sncode=?",ob,sncode);
		baseDao.execute("delete from packagedetail where pd_barcode='"+sncode+"'");
		baseDao.updateByCondition("makecraftdetail", "mcd_inqty= mcd_inqty-1,mcd_outqty=mcd_outqty-1,mcd_okqty=mcd_okqty-1", "mcd_mccode='"+ob0[2]+"' and mcd_stepcode='"+ob0[1]+"'");
		if(ob0[0] != null && "2".equals(String.valueOf(ob0[0]))){
			baseDao.updateByCondition("makecraft", "mc_madeqty=mc_madeqty-1", "mc_code='"+ob0[2]+"'");
		}
	}

	@Override
	public void updatePackageQty(String caller, String pa_outboxcode, long pa_totalqty) {		
		//判断箱号是否存在
		Object ob = baseDao.getFieldDataByCondition("package", "pa_id","pa_outboxcode='"+pa_outboxcode+"'");
		if(ob != null){
			//限制修改的箱内容量不允许小于已装数
			int cn = baseDao.getCount("select count(pd_id) from packagedetail where pd_paid="+ob);
			if(cn > pa_totalqty){
				BaseUtil.showError("箱内容量不允许小于箱内已采集的序列号数量");
			}
			baseDao.updateByCondition("package", "pa_totalqty="+pa_totalqty,"pa_id="+ob);
		}else{
			BaseUtil.showError("箱号："+pa_outboxcode+",不存在！");
		}		
	}

	@Override
	public String printPackageSN(String caller, String pa_outboxcode,long lps_id) {
		StringBuffer strs = new StringBuffer();
		Object ob = baseDao.getFieldDataByCondition("package", "pa_id","pa_outboxcode='"+pa_outboxcode+"'");
		if(ob != null){
			//判断标签模板是否存在，是否审核
			/*Object [] obs = baseDao.getFieldsDataByCondition("LabelPrintSetting", new String []{"lps_statuscode","lps_labelurl"}, "lps_id="+lps_id);
			if(obs != null){
				if(!obs[0].equals("AUDITED")){
					BaseUtil.showError("模板标签未审核！");
				}else if(obs[1] == null){
					BaseUtil.showError("模板标签路径未维护！");
				}
			}else{
				BaseUtil.showError("模板标签，不存在！");
			}	*/
			//清空packagePrint表中数据
			baseDao.execute("delete from packagePrint");
			//生成数据插入表packagePrint 中
			baseDao.execute("insert into packagePrint(pp_id,pp_outboxcode,pp_barcode,pp_caller,pp_macode,pp_custcode,pp_source,pp_prodcode)select PACKAGE_SEQ.nextval,pd_outboxcode,pd_barcode,'"+caller+"',ms_makecode,ma_custcode,'包装',ms_prodcode from packagedetail left join makeserial on ms_sncode=pd_barcode left join make on ma_code=ms_makecode where pd_paid="+ob.toString());		
		}else{
			BaseUtil.showError("箱号："+pa_outboxcode+",不存在！");
		}		
		return strs.toString();
	}

	@Override
	public List<Map<String, Object>> getPrintTemplates(String caller,
			String condition) {
		SqlRowList rs=  baseDao.queryForRowSet("select lps_id  as \"lps_id\",lps_code as \"lps_code\" from labelPrintSetting where lps_caller='"+condition+"' and lps_statuscode='AUDITED'");				
		return rs.getResultList();
	}
}
