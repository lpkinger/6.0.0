package com.uas.pdaio.service.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.ProdInOutService;
import com.uas.pdaio.service.PdaioInService;
import com.uas.pdaio.service.PdaioPowerDao;

@Service("PdaioInServiceImpl")
public class PdaioInServiceImpl implements PdaioInService{
	
	@Autowired 
	BaseDao baseDao;
	@Autowired
	private ProdInOutService prodInOutService;
	@Autowired
	PdaioPowerDao pdaioPowerDao ;
	
	
	@Override
	@Transactional
	public Map<String, Object> addProdinout(String pi_class, String pi_cardcode, String pi_whcode,HttpSession session) {
		//执行后调用存储过程
		int pi_id = 0;
		String pi_inoutno ="";
		String pi_status="";
		String pi_invostatus="";
		String caller =returnCaller(pi_class);
		try{
			pdaioPowerDao.preSaveHandle(caller);
		}
		catch(Exception e){
			String error =e.getMessage();
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,error.substring(error.indexOf(":")+1,error.length()));
		}
		SqlRowList rsForm= baseDao.queryForRowSet("select fd_allowblank,fd_caption from formdetail left join form on fd_foid = fo_id where fo_caller ='"+caller+"' and fd_field = 'pi_cardcode'");
		if(rsForm.next()){
			if("F".equals(rsForm.getString("fd_allowblank"))){
				 if(StringUtils.isEmpty(pi_cardcode)){
					throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, rsForm.getString("fd_caption")+"不能为空");
				}
			}
		}
		List<String> res = baseDao.callProcedureWithOut("SP_PDANEWPRODINOUT",new Object[] { SystemSession.getUser().getEm_code(),pi_class,pi_whcode,pi_cardcode},new Integer[]{1,2,3,4},new Integer[]{5,6});
		if(res.get(0) == null || ("").equals(res.get(0))){
			pi_id = Integer.valueOf(res.get(1).toString());
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,res.get(0).toString());	
		}
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno,pi_status,pi_statuscode,pi_invostatus,pi_invostatuscode from prodinout where pi_id= ?",pi_id);
		if(rs.next()){
			pi_inoutno = rs.getString("pi_inoutno");
			pi_invostatus = rs.getString("pi_invostatus");
			pi_status = rs.getString("pi_status");
		}
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("inoutno", pi_inoutno);
		modelMap.put("id", pi_id);
		modelMap.put("pi_invostatus",pi_invostatus);
		modelMap.put("pi_status", pi_status);
		return modelMap;
	}


	@Override
	public Map<String, Object> getWhcode(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String con = null;
		if (StringUtils.isEmpty(condition)){			
			con = "1=1";
		}else{
			con = "wh_code like '%"+condition+"%' or wh_description like '%"+condition+"%'";
		}
		SqlRowList rs = baseDao.queryForRowSet("select wh_code, wh_description,wh_type  from warehouse where nvl(wh_statuscode,' ')='AUDITED' and "+con);
		if(rs.next()){
			modelMap.put("whcode",rs.getResultList());
		}else{
			modelMap.put("whcode","");
		}
		return modelMap;
	}


	@Override
	public Map<String, Object> getVendor(String condition,Integer page,Integer pageSize) {
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String con = null;
		if (StringUtils.isEmpty(condition)){
			con = "1=1";			
		}else{
			con = "ve_code like '%"+condition+"%' or ve_name like '%"+condition+"%' or ve_shortname like '%"+condition+"%'";
		}
		SqlRowList rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select ve_code,ve_name,ve_shortname from vendor where nvl(ve_auditstatuscode,' ')='AUDITED' and "+con+" order by ve_id desc) tt where rownum<="+end+" )where rn>="+start);
		if(rs.next()){
			modelMap.put("vendor",rs.getResultList());
		}else{
			modelMap.put("vendor","");
		}
		return modelMap;
	}


	@Override
	@Transactional
	public Map<String, Object> addProdiodetail(String inoutno) {
		SqlRowList rs = baseDao.queryForRowSet("select pi_id,pi_class from prodinout where pi_inoutno = ?",inoutno);
		if(rs.next()){
			String caller =returnCaller(rs.getString("pi_class"));
			try{
				pdaioPowerDao.preChangeHandle(caller,rs.getInt("pi_id"));
			}
			catch(Exception e){
				String error =e.getMessage();
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,error.substring(error.indexOf(":")+1,error.length()));
			}
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"单据:"+inoutno+"不存在");
		}
		String res=baseDao.callProcedure("SP_PDAINFINISH",new Object[] {inoutno, SystemSession.getUser().getEm_code()});
		if (res != null && !("").equals(res.trim())) {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,res);
		}
		return null;
	}
	
	@Override
	@Transactional
	public Map<String, Object> getBarcodeInfo(String inoutno,String barcode,Integer allowRepeat) {
		SqlRowList inout = baseDao.queryForRowSet("select pi_class,pi_id from prodinout where pi_inoutno =?",inoutno) ;
		if(inout.next()){
			String caller =returnCaller(inout.getString("pi_class"));
			try{
				pdaioPowerDao.preChangeHandle(caller,inout.getInt("pi_id"));
			}
			catch(Exception e){
				String error =e.getMessage();
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,error.substring(error.indexOf(":")+1,error.length()));
			}
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int count = baseDao.getCount("select count(1) cn from barcodeio where bi_content = '"+barcode+"'");
		if(count > 0){
			if(allowRepeat == 0){
				modelMap.put("repeat", "-1");
				return modelMap;
			}
		}
		String res = baseDao.callProcedure("SP_PDAIO_IN", new Object[] { inoutno,SystemSession.getUser().getEm_code(),barcode});
		double sumQty = 0;
		if (res != null && !("").equals(res.trim())) {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,res);
		}else{
			SqlRowList rs = baseDao.queryForRowSet("select bi_prodcode,bi_ordercode,bi_lotno,bi_datecode,bi_inqty,bi_piid,bi_id,bi_brand from barcodeio where bi_content = ?",barcode);
			if(rs.next()){
				SqlRowList rsSum = baseDao.queryForRowSet("select nvl(sum(bi_inqty),0) bi_inqty from barcodeio where bi_piid = ? and bi_ordercode = ? and bi_prodcode =? and bi_brand =?",rs.getInt("bi_piid"),rs.getString("bi_ordercode"),rs.getString("bi_prodcode"),rs.getString("bi_brand"));
				if(rsSum.next()){
					sumQty = rsSum.getDouble("bi_inqty");
				}
				modelMap = rs.getCurrentMap();
				modelMap.remove("bi_piid");
				modelMap.put("sumQty", sumQty);
				return modelMap;
			}else{
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码不存在");
			}
		}
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"请检查单据是否存在");
		}
	}


	@Override
	public List<Map<String, Object>> getProdinoutList(String condition,Integer page,Integer pageSize) {
		StringBuffer sb = new StringBuffer();
		try{
			if(pdaioPowerDao.preSeeAllHandle("ProdInOut!PurcCheckin")){
				sb.append("'采购验收单',");
			}
			if(pdaioPowerDao.preSeeAllHandle("ProdInOut!OtherIn")){
				sb.append("'其它入库单',");
			}
			if(pdaioPowerDao.preSeeAllHandle("ProdInOut!AppropriationIn")){
				sb.append("'拨入单',");
			}
			if(pdaioPowerDao.preSeeAllHandle("ProdInOut!OtherPurcIn")){
				sb.append("'其它采购入库单',");
			}
			if(pdaioPowerDao.preSeeAllHandle("ProdInOut!Make!Return")){
				sb.append("'生产退料单',");
			}
			if(pdaioPowerDao.preSeeAllHandle("ProdInOut!OutsideReturn")){
				sb.append("'委外退料单',");
			}
		}catch(Exception e){}
		String cond= null;
		if(sb.length()>0){	
			cond = sb.toString();
			cond = cond.substring(0, sb.length()-1);
		}
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		String con = null;
		if (StringUtils.isEmpty(condition)){
			con = "1=1";			
		}else{
			con = " (pi_inoutno like '%"+condition+"%' or pi_whcode like '%"+condition+"%' or pi_recordman like '%"+condition+"%') ";
		}
		if(StringUtils.isEmpty(cond)){
			cond = "1=2";
		}else{
			cond = "pi_class in ("+cond+")";
		}
		SqlRowList rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select pi_inoutno,pi_whcode,pi_whname,pi_cardcode,pi_title,pi_status,pi_class,pi_id,pi_invostatus,pi_recordman,pi_recorddate,pi_pdastatus from prodinout where "+cond+" and nvl(pi_statuscode,' ')='UNPOST' and "+con
				+ " order by pi_id desc) tt where rownum<="+end+" )where rn>="+start);
		if(rs.next()){
			return rs.getResultList();
		}
		return null;
	}


	@Override
	@Transactional
	public Map<String, Object> deleteInoutAndDetail(Integer piid) {
		String pi_class = null;
		String caller = null;
		SqlRowList rs = baseDao.queryForRowSet("select pi_class from prodinout where pi_id = ?",piid);
		if(rs.next()){
			pi_class = rs.getString("pi_class");
			caller =returnCaller(pi_class);
			try{
				pdaioPowerDao.preDeleteHandle(caller,piid);
			}
			catch(Exception e){
				String error =e.getMessage();
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,error.substring(error.indexOf(":")+1,error.length()));
			}
			
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"请确认单据是否存在");
		}
		prodInOutService.deleteProdInOut(caller,piid);
		return null;
	}


	@Override
	public List<Map<String, Object>> getBarcodeDetail(Integer piid,Integer page,Integer pageSize,String condition) {
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		String con;
		if (StringUtils.isEmpty(condition)){			
			con = "and 1=1";
		}else{
			con = " and bi_content like '%"+condition+"%'";
		}
		SqlRowList rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select bi_id,bi_barcode,bi_content,bi_prodcode,bi_inqty,bi_inoutno,bi_whcode,bi_brand,bi_ordercode,em_name,bi_datecode,bi_lotno,case when bi_status>0 then '已入库' else '未入库' end bi_status from barcodeio left join employee on bi_inman=em_code where bi_piid="+piid+con+"  order by bi_id desc) tt where rownum<="+end+" )where rn>="+start);
		if(rs.next()){
			return rs.getResultList();
		}
		return null;
	}


	@Override
	public List<Map<String, Object>> getProdInoutQtySum(Integer piid,Integer page,Integer pageSize) {
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		SqlRowList rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select sum(bi_inqty) bi_inqty,bi_prodcode,bi_inoutno,bi_ordercode,pr_detail,pr_spec,bi_brand from barcodeio left join product on pr_code = bi_prodcode where bi_piid ="+piid+" group by bi_inoutno,bi_ordercode,bi_prodcode,pr_detail,pr_spec,bi_brand) tt where rownum<="+end+" )where rn>="+start);
		if(rs.next()){
			return rs.getResultList();
		}
		return null;
	}


	@Override
	public Map<String, Object> deleteBarcode(Integer piid, String type, Integer biid) {
		// type为All的时候全部删除
		SqlRowList rs = baseDao.queryForRowSet("select pi_pdastatus,pi_class from prodinout where pi_id =?",piid);
		if(rs.next()){
			String caller =returnCaller(rs.getString("pi_class"));
			try{
				pdaioPowerDao.preChangeHandle(caller,piid);
			}
			catch(Exception e){
				String error =e.getMessage();
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,error.substring(error.indexOf(":")+1,error.length()));
			}
			if(("已入库").equals(rs.getString("pi_pdastatus"))){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该单据已入库,不允许删除条码");
			}
			int count = 0;
			if(("All").equals(type)){
				count = baseDao.getCount("select count(1) cn from barcodeio where bi_piid = "+piid +"and nvl(bi_status,0) = 99");
				if(count>0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"存在条码已入库,不允许删除");
				}else{
					count = baseDao.getCount("select count(1) cn from barcodeio where bi_piid ="+piid);
					if(count>0){
						baseDao.deleteById("Barcodeio", "bi_piid", piid);
						baseDao.logger.others("删除条码", "删除条码成功", "PDA", "pi_id", piid);						
					}else{
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该单据不存在条码");
					}
				}
			}else{
				count = baseDao.getCount("select count(1) cn from barcodeio where bi_piid = "+piid +" and bi_id ="+biid+"and nvl(bi_status,0) = 99");
				if(count>0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该条码已入库,不允许删除");
				}else{
					count = baseDao.getCount("select count(1) cn from barcodeio where bi_piid ="+piid+"and bi_id = "+biid);
					if(count>0){
						baseDao.deleteById("Barcodeio", "bi_id", biid);						
						baseDao.logger.others("删除条码", "删除条码ID"+biid+"成功", "PDA", "pi_id", piid);
					}else{
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该条码不存在");
					}
				}
			}
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"单据不存在");
		}
		return null;
	}


	@Override
	public Map<String, Object> revokeBarcode(String inoutno) {
		Integer pi_id= 0;
		String caller = "";
		String pi_class = "";
		SqlRowList rs = baseDao.queryForRowSet("select pi_id,pi_class,nvl(pi_statuscode,' ') pi_statuscode,nvl(pi_invostatuscode,' ') pi_invostatuscode,nvl(pi_pdastatus,' ') pi_pdastatus from prodinout where pi_inoutno = ? ",inoutno);
		if(rs.next()){		
			pi_id = rs.getInt("pi_id");
			pi_class = rs.getString("pi_class");
			caller =returnCaller(rs.getString("pi_class"));
			try{
				pdaioPowerDao.preChangeHandle(caller,pi_id);
			}
			catch(Exception e){
				String error =e.getMessage();
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,error.substring(error.indexOf(":")+1,error.length()));
			}
			if(!("UNPOST").equals(rs.getString("pi_statuscode"))){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"未过账状态才可以撤销入库");
			}
			if(!("ENTERING").equals(rs.getString("pi_invostatuscode"))){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"在录入状态才可以撤销入库");
			}
			if(!("已入库").equals(rs.getString("pi_pdastatus"))){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"已入库状态才可以撤销入库");
			}
			rs = baseDao.queryForRowSet("select  bi_id from barcodeio where bi_piid = ? ",pi_id);
			if(rs.next()){
				baseDao.execute("update prodinout set pi_pdastatus='未入库' where pi_id = "+pi_id);
				baseDao.execute("update barcodeio set bi_status = 0 where bi_piid = "+pi_id);
				baseDao.logger.others("撤销入库", "撤销入库成功", caller,"pi_id", pi_id);
			}else{
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该单据不存在条码");
			}
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"请检查单据是否存在");
		}
		return null;
	}


	@Override
	public Map<String, Object> getLatestProdinout(String emcode) {
		if(SystemSession.getUser() == null || ("").equals(SystemSession.getUser())){
			return null;
		}
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno,pi_id,pi_class,pi_cardcode,pi_title,pi_whcode,pi_whname,pi_pdastatus,pi_status,pi_recordman,pi_recorddate,pi_invostatus from prodinout where pi_recordman = '"+SystemSession.getUser().getEm_name()+"' and pi_class in ('采购验收单','其它入库单','拨入单','其它采购入库单','生产退料单','委外退料单') and nvl(pi_pdastatus,' ')<>'已入库' order by pi_id desc"); 
		if(rs.next()){
			return rs.getCurrentMap();
		}else{
			return null;
		}
	}
	private String returnCaller(String pi_class){
		String caller = null;
		switch(pi_class){
		case "采购验收单":
			caller = "ProdInOut!PurcCheckin";
			break;
		case "其它入库单":
			caller = "ProdInOut!OtherIn";
			break;
		case "拨入单":
			caller = "ProdInOut!AppropriationIn";
			break;
		case "其它采购入库单":
			caller = "ProdInOut!OtherPurcIn";
			break;
		case "生产退料单":
			caller = "ProdInOut!Make!Return";
			break;
		case "委外退料单":
			caller = "ProdInOut!OutsideReturn";
			break;
		default:
		    break;
		}
		return caller;
	}


	@Override
	public Map<String, Object> updatePiCardcde(Integer piid, String newVendor) {
		SqlRowList rs = baseDao.queryForRowSet("select pi_class,pi_invostatuscode,pi_statuscode,pi_pdastatus,pi_cardcode from prodinout where pi_id =?",piid);
		String pi_class ="";
		if(rs.next()){
			if(!("ENTERING").equals(rs.getString("pi_invostatuscode"))){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"单据非在录入状态无法更新供应商");
			}
			if(("POSTED").equals(rs.getString("pi_statuscode"))){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"单据已过账无法更新供应商");
			}
			if(newVendor.equals(rs.getString("pi_cardcode"))){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"供应商编号一致,不用更新");
			}
			pi_class = rs.getString("pi_class");
			rs = baseDao.queryForRowSet("select ve_auditstatuscode,ve_code,ve_name,ve_shortname from vendor where ve_code  ='"+newVendor+"'");
			if(rs.next()){
				if(!("AUDITED").equals(rs.getString("ve_auditstatuscode"))){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"供应商:"+newVendor+"不是已审核状态");
				}
				baseDao.execute("update prodinout set (pi_cardcode,pi_title) = (select ve_code,ve_name from vendor where ve_code=?) where pi_id = ?",newVendor,piid);
				baseDao.logger.others("更新供应商","更新供应商成功", returnCaller(pi_class), "pi_id", piid);				
			}else{
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"供应商:"+newVendor+"不存在");
			}
			
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"无当前单据记录,请检查单据是否存在");
		}
		return rs.getCurrentMap();
	}
	
}