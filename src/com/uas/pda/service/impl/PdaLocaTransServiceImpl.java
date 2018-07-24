package com.uas.pda.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.pda.service.PdaLocaTransService;

@Service("pdaLocaTransService")
public class PdaLocaTransServiceImpl implements PdaLocaTransService{
	@Autowired
	private BaseDao baseDao;
    
	@Override
	public Map<String, Object> getCodeData(String whcode, String code,
			String type) {
		SqlRowList rs;
		Object ob = baseDao.getFieldDataByCondition("WAREHOUSE", "wh_description", "wh_statuscode='AUDITED' AND wh_code='" + whcode+ "'");
		if (ob == null) {
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"仓库不存在或者未审核");
		}			
		if(type.equals("out_box")){
			rs  = baseDao.queryForRowSet("select  bar_prodcode,pr_detail,pr_spec,bar_location, bar_outboxcode1, bar_remain,bar_whcode"+
                       " from barcode left join product on pr_code=bar_prodcode"+
                       " where bar_whcode='"+ whcode+"' and bar_outboxcode1='"+code+"' and nvl(bar_status,0)=1");
		}else {
			rs = baseDao.queryForRowSet("select bar_prodcode,pr_detail,pr_spec,bar_location,bar_code,bar_remain,bar_whcode"+
                       " from barcode left join product on pr_code=bar_prodcode where bar_whcode='"+whcode+"' and bar_code='"+code+"' and nvl(bar_status,0)=1");
		}		
		if(rs.next()){
			return rs.getCurrentMap();
		}else {
			if(type.equals("out_box")){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"外箱号错误,不存在或不是在库状态或不在仓库["+whcode+"]中");
			}else
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码号错误,不存在或不是在库状态或不在仓库["+whcode+"]中");
		}
	}

	@Override
	public void locaTransfer(String data, String location) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);
		//记录条码储位变更操作日志
		int bl_id ;
		String sql;		
		List <String> sqls = new ArrayList<String>();
		for(Map<Object, Object> map:gstore){
			bl_id = baseDao.getSeqId("BARCODELOGGER_SEQ");
			Object bar_code = map.get("BAR_CODE");
			Object pa_outboxcode = map.get("BAR_OUTBOXCODE1");
			Object bar_prodcode = map.get("BAR_PRODCODE");
			Object bar_whcode = map.get("BAR_WHCODE");
			Object bar_location = map.get("BAR_LOCATION");
			if(bar_code != null &&  !"".equals(bar_code) ){
				//变更条码
			   sql = "update barcode set bar_location='"+location+"' where bar_code='"+bar_code+"' and nvl(bar_status,0)=1";
			   sqls.add(sql);
			   //记录日志			   
			   sql = "insert into barcodeLogger (bl_id,bl_barcode,bl_prodcode,bl_whcode,bl_description,bl_date) values("+bl_id+",'"+bar_code+"','"+bar_prodcode+"','"+bar_whcode+"','储位转移[原储位:"+bar_location+" 新储位:"+location +"]',sysdate)";
			   sqls.add(sql);	
			}else if(pa_outboxcode != null &&  !"".equals(pa_outboxcode)){
				//变更箱内条码
				sqls.add("update barcode set bar_location='"+location+"' where exists(select 1 from MES_PACKAGE_VIEW where v_barcode=bar_code and v_outboxcode ='"+pa_outboxcode+"' and nvl(bar_status,0)=0)");
				//记录日志
				sql = "insert into barcodeLogger (bl_id,bl_barcode,bl_prodcode,bl_whcode,bl_description,bl_date) values("+bl_id+",'"+pa_outboxcode+"','"+bar_prodcode+"','"+bar_whcode+"','箱号储位转移[原储位:"+bar_location+" 新储位:"+location +"]',sysdate)";
				sqls.add(sql);
			}					
		}		
		baseDao.execute(sqls);
	}

	@Override
	public Map<String, Object> getCodeWhcode(String code, String type) {
		SqlRowList rs;
		if(type.equals("out_box")){
			rs  = baseDao.queryForRowSet("select  bar_prodcode,pr_detail,pr_spec,bar_location, bar_outboxcode1, bar_remain,bar_whcode"+
					   " from barcode left join product on pr_code=bar_prodcode"+
                       " where bar_outboxcode1='"+code+"' and nvl(bar_status,0)=1");
		}else {
			rs = baseDao.queryForRowSet("select bar_prodcode,pr_detail,pr_spec,bar_location,bar_code,bar_remain,bar_whcode"+
                       " from barcode left join product on pr_code=bar_prodcode where bar_code='"+code+"' and nvl(bar_status,0)=1");
		}		
		if(rs.next()){
			return rs.getCurrentMap();
		}else {
			if(type.equals("out_box")){
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"箱号错误,不存在或不是在库状态");
			}else
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码号错误,不存在或不是在库状态");
		}
	}

	@Override
	public void whcodeTransfer(String data, String whcode) {
		List<Map<Object, Object>> gridStore = FlexJsonUtil.fromJsonArray(data, HashMap.class);
		if(whcode == null){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,"请填写库位!");
		}
		SqlRowList rs = baseDao.queryForRowSet("select wh_statuscode from warehouse where wh_code=?",whcode);
		if(rs.next()){
			if(!rs.getString("wh_statuscode").equals("AUDITED")){
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"库位"+whcode+"未审核!");
			}
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"库位"+whcode+"不存在!");
		}
		int bl_id ;
		String sql;		
		List <String> sqls = new ArrayList<String>();
		for(Map<Object, Object> map:gridStore){
			bl_id = baseDao.getSeqId("BARCODELOGGER_SEQ");
			Object bar_code = map.get("bar_code");
			Object pa_outboxcode = map.get("bar_outboxcode1");
			Object bar_procode = map.get("bar_prodcode");
			Object bar_whcode = map.get("bar_whcode");
			if(bar_code != null &&  !("").equals(bar_code)){
				//变更条码
			   sql = "update barcode set bar_whcode='"+whcode+"' where bar_code='"+bar_code+"'";
			   sqls.add(sql);
			   //记录日志			   
			   sql = "insert into barcodeLogger (bl_id,bl_barcode,bl_prodcode,bl_whcode,bl_description,bl_date) values("+bl_id+",'"+bar_code+"','"+bar_procode+"','"+map.get("bar_location")+"','条码转移[原条码:"+bar_code+" 新条码:"+whcode +"]',sysdate)";
			   sqls.add(sql);	
			}else if(pa_outboxcode != null &&  !"".equals(pa_outboxcode)){
				//变更箱号
				sqls.add("update package set pa_whcode='"+whcode+"' where pa_outboxcode="+pa_outboxcode);
				sqls.add("update barcode set bar_whcode="+whcode+" where exists(select 1 from MES_PACKAGE_VIEW"
							+" where v_barcode=bar_code and v_outboxcode ='"+pa_outboxcode+"' and nvl(bar_status,0)=0)");
				//记录日志
				sql = "insert into barcodeLogger (bl_id,bl_barcode,bl_prodcode,bl_whcode,bl_description,bl_date) values("+bl_id+",'"+pa_outboxcode+"','"+bar_procode+"','"+bar_whcode+"','箱号转移[原箱号:"+bar_whcode+" 新箱号:"+whcode +"]',sysdate)";
				sqls.add(sql);
			}					
		}		
		baseDao.execute(sqls);
	}
	

}
