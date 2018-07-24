package com.uas.pdaio.service.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.pdaio.service.PdaioOutService;
import com.uas.pdaio.service.PdaioPowerDao;

@Service("PdaioOutServiceImpl")
public class PdaioOutServiceImpl implements PdaioOutService{
	
	@Autowired 
	BaseDao baseDao;
	@Autowired
	PdaioPowerDao pdaioPowerDao ;
	
	@Override
	public List<Map<String, Object>> fuzzySearch(String inoutNo) {
		SqlRowList rs;
		inoutNo = inoutNo.toUpperCase();
		rs = baseDao
				.queryForRowSet("select * from (select  pi_id,pi_inoutno,pi_class,pi_status,pi_invostatus,pi_pdastatus from prodinout left join documentsetup  on pi_class=ds_name where pi_inoutno like ? "
					+ " and (ds_inorout = '-IN' OR ds_inorout = 'OUT') and nvl(pi_class,' ') in ('拨出单','其它出库单','生产领料单','委外领料单','出货单') and nvl(pi_pdastatus,' ')<>'已备料 ' and nvl(pi_invostatuscode,' ')<>'ENTERING'  order by pi_id desc) where rownum<=10","%"+inoutNo+"%");
		if (rs.next()) {
			return rs.getResultList();
		}
		return null;
	}

	@Override
	public Map<String, Object> getNextData(Integer pi_id) {
		SqlRowList rs = null ;
		String order = "";
		String cond = "";
		String con = "";
		boolean bo = baseDao.isDBSetting("BarCodeSetting", "reverseOrder");
		if(bo){
			order = "desc";
			cond = " and B.pd_brand = A.pd_brand ";
			con = " and bi_brand = pd_brand ";
		}else{
			order = "asc";
		}
		rs = baseDao.queryForRowSet("select B.pd_prodcode pd_prodcode,B.pd_outqty-nvl(bi_outqty,0) pd_outqty,B.pd_whcode pd_whcode,B.pd_brand pd_brand from (select * from (select * from  (select sum(pd_outqty) pd_outqty,pd_prodcode,pd_whcode,pd_brand from prodiodetail where pd_piid=? "
				+" group by pd_prodcode,pd_whcode,pd_brand) left join (select sum(bi_outqty) bi_outqty,bi_prodcode,bi_whcode,bi_brand from barcodeio  "
				+" where bi_piid = ? group by bi_prodcode,bi_whcode,bi_brand) on bi_whcode = pd_whcode and bi_prodcode = pd_prodcode "+con+") where nvl(pd_outqty,0) "
				+" - nvl(bi_outqty,0)>0) A left join prodiodetail B on A.pd_prodcode = B.pd_prodcode and A.pd_whcode = B.pd_whcode "+cond
				+" left join product on pr_code = A.pd_prodcode where pd_piid = ? order by pd_pdno "+order,pi_id,pi_id,pi_id);
		if(rs.next()){
			return rs.getCurrentMap();
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getProdinoutList(String condition, Integer page, Integer pageSize) {
		StringBuffer sb = new StringBuffer();
		try{
			if(pdaioPowerDao.preSeeAllHandle("ProdInOut!AppropriationOut")){
				sb.append("'拨出单',");
			}
			if(pdaioPowerDao.preSeeAllHandle("ProdInOut!OtherOut")){
				sb.append("'其它出库单',");
			}
			if(pdaioPowerDao.preSeeAllHandle("ProdInOut!Picking")){
				sb.append("'生产领料单',");
			}
			if(pdaioPowerDao.preSeeAllHandle("ProdInOut!OutsidePicking")){
				sb.append("'委外领料单',");
			}
			if(pdaioPowerDao.preSeeAllHandle("ProdInOut!Sale")){
				sb.append("'出货单',");
			}
		}catch(Exception e){
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,e.getMessage());
		}
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
			con = " ( pi_inoutno like '%"+condition+"%' or pi_whcode like '%"+condition+"%' or pi_recordman like '%"+condition+"%' ) ";
		}
		if(StringUtils.isEmpty(cond)){
			cond = "1=2";
		}else{
			cond = "pi_class in ("+cond+")";
		}
		SqlRowList rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select pi_inoutno,pi_whcode,pi_whname,pi_cardcode,pi_title,pi_status,pi_class,pi_id,pi_invostatus,pi_recordman,pi_recorddate,pi_pdastatus from prodinout left join documentsetup  on pi_class=ds_name "
					+ " where "+cond+" and (ds_inorout = '-IN' OR ds_inorout = 'OUT') and nvl(pi_statuscode,' ')='UNPOST' and "+con
				+ " order by pi_id desc) tt where rownum<="+end+" )where rn>="+start);
		if(rs.next()){
			return rs.getResultList();
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getBarcodeDetail(Integer piid, Integer page, Integer pageSize, String condition) {

		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		String con;
		if (StringUtils.isEmpty(condition)){			
			con = "and 1=1";
		}else{
			con = " and bi_content like '%"+condition+"%'";
		}
		SqlRowList rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select bi_id,bi_barcode,bi_content,bi_prodcode,bi_outqty,bi_inoutno,bi_whcode,bi_brand,bi_ordercode,em_name,bi_datecode,bi_lotno,case when bi_status>0 then '已采集' else '未采集' end bi_status from barcodeio left join employee on bi_inman=em_code where bi_piid="+piid+con+"  order by bi_id desc) tt where rownum<="+end+" )where rn>="+start);
		if(rs.next()){
			return rs.getResultList();
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getProdInoutQtySum(Integer piid, Integer page, Integer pageSize) {
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		SqlRowList rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select sum(bi_outqty) bi_outqty,bi_prodcode,bi_inoutno,bi_ordercode,pr_detail,pr_spec,bi_brand from barcodeio left join product on pr_code = bi_prodcode where bi_piid ="+piid+" group by bi_inoutno,bi_ordercode,bi_prodcode,pr_detail,pr_spec,bi_brand) tt where rownum<="+end+" )where rn>="+start);
		if(rs.next()){
			return rs.getResultList();
		}
		return null;
	}

	@Override
	public Map<String, Object> deleteBarcode(Integer piid, String type, Integer biid) {
		// type为All的时候全部删除
		SqlRowList rs = baseDao.queryForRowSet("select pi_class from prodinout where pi_id = ?",piid);
		if(rs.next()){
			String caller =returnCaller(rs.getString("pi_class"));
			try{
				pdaioPowerDao.preChangeHandle(caller,piid);
			}
			catch(Exception e){
				String error =e.getMessage();
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,error.substring(error.indexOf(":")+1,error.length()));
			}
			int count = 0;
			if(("All").equals(type)){
					count = baseDao.getCount("select count(1) cn from barcodeio where bi_piid ="+piid);
					if(count>0){
						baseDao.deleteById("Barcodeio", "bi_piid", piid);
						baseDao.execute("update prodinout set pi_pdastatus='未备料' where pi_id = ?",piid);
						baseDao.logger.others("删除条码", "删除条码成功", "PDA", "pi_id", piid);						
					}else{
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该单据不存在条码");
					}
			}else{
					count = baseDao.getCount("select count(1) cn from barcodeio where bi_piid ="+piid+"and bi_id = "+biid);
					if(count>0){
						baseDao.deleteById("Barcodeio", "bi_id", biid);	
						updatePdaStatus(piid);
						baseDao.logger.others("删除条码", "删除条码ID"+biid+"成功", "PDA", "pi_id", piid);
					}else{
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该条码不存在");
					}
			}
			return null;
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"单据不存在");
		}	
	}
	
	//更新主表状态
	private void updatePdaStatus(int id){
		Double pd_outqty;
		Double bi_outqty;
		SqlRowList rs = baseDao.queryForRowSet("select nvl(sum(pd_outqty),0) pd_outqty from prodiodetail where pd_piid = ? ",id);
		if(rs.next()){
			pd_outqty = rs.getDouble("pd_outqty");
			rs = baseDao.queryForRowSet("select nvl(sum(bi_outqty),0) bi_outqty from barcodeio where bi_piid = ? ",id);
			if(rs.next()){
				 bi_outqty = rs.getDouble("bi_outqty");
				 if(NumberUtil.compare(pd_outqty, bi_outqty) == 0){
					 baseDao.execute("update prodinout set pi_pdastatus='已备料' where pi_id = ?",id);
				 }else if(NumberUtil.compare(pd_outqty, bi_outqty) == 1 && bi_outqty > 0){
					 baseDao.execute("update prodinout set pi_pdastatus='备料中' where pi_id = ?",id);
				 }
				 if(bi_outqty == 0){
					 baseDao.execute("update prodinout set pi_pdastatus='未备料' where pi_id = ?",id);
				 }
			}
		}
	}

	@Override
	public Map<String, Object> getProdOut(String inoutNo) {
		SqlRowList rs = baseDao.queryForRowSet("select  pi_id,pi_inoutno,pi_class,pi_status,pi_cardcode,pi_title,pi_invostatus,pi_pdastatus from prodinout left join documentsetup  on pi_class=ds_name where pi_inoutno = ? and (ds_inorout = '-IN' OR ds_inorout = 'OUT')",inoutNo);
		if(rs.next()){
			return rs.getCurrentMap();
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"出库单:"+inoutNo+"不存在");
		}
	}

	@Override
	public Map<String, Object> collectBarcode(Integer pi_id,String barcode) {
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno,pi_class from prodinout where pi_id = ?",pi_id);
		if(rs.next()){
			String caller =returnCaller(rs.getString("pi_class"));
			try{
				pdaioPowerDao.preChangeHandle(caller,pi_id);
			}
			catch(Exception e){
				String error =e.getMessage();
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,error.substring(error.indexOf(":")+1,error.length()));
			}
			String res = baseDao.callProcedure("SP_PDAIO_OUT", new Object[] { pi_id,SystemSession.getUser().getEm_code(),barcode});
			if (res != null && !("").equals(res.trim())) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,res);
			}
			 Map<String, Object> map = new HashMap<String, Object>();
			   rs = baseDao.queryForRowSet("select bi_prodcode,bi_ordercode,bi_lotno,bi_datecode,bi_outqty,bi_piid,bi_id,pr_detail,pr_spec,bi_brand from barcodeio left join product on pr_code = bi_prodcode where bi_content = ?",barcode);
			   if(rs.next()){				   
				   map.put("barcode", rs.getCurrentMap());
				   try{ 
					   map.put("next", getNextData(pi_id));	
				   }
				   catch (Exception e) {
					   map.put("next", null);		
				   }   	
			   }else{
				   throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码采集失败");
			   }
				return map;
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该单据不存在");
		}
	}

	@Override
	public Map<String, Object> revokeBarcode(Integer pi_id, String barcode) {
		double sumQty = 0;
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno,pi_class from prodinout where pi_id = ?",pi_id);
		if(rs.next()){
			String caller =returnCaller(rs.getString("pi_class"));
			try{
				pdaioPowerDao.preChangeHandle(caller,pi_id);
			}
			catch(Exception e){
				String error =e.getMessage();
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,error.substring(error.indexOf(":")+1,error.length()));
			}
			rs = baseDao.queryForRowSet("select bi_prodcode,bi_ordercode,bi_lotno,bi_datecode,bi_outqty,bi_piid,bi_id,pr_detail,pr_spec,bi_brand from barcodeio left join product on pr_code = bi_prodcode where bi_content = ?",barcode);
			   if(rs.next()){				   
				   SqlRowList rsSum = baseDao.queryForRowSet("select nvl(sum(bi_outqty),0) bi_outqty from barcodeio where bi_piid = ? and bi_content = ? ",rs.getInt("bi_piid"),barcode);
				   if(rsSum.next()){
					   sumQty = rsSum.getDouble("bi_outqty");
				   }
				   modelMap = rs.getCurrentMap();
				   modelMap.put("sumQty", sumQty);
				   String res = baseDao.callProcedure("SP_PDAIO_DELETEOUT", new Object[] { pi_id,SystemSession.getUser().getEm_code(),barcode});
					if (res != null && !("").equals(res.trim())) {
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,res);
					}
			       return modelMap;
			   }else{
				   throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码不存在");
			   }
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该单据不存在");
		}
	}

	@Override
	public List<Map<String, Object>> getProdOutStatus(String ids) {
		SqlRowList rs = baseDao.queryForRowSet("select pi_id,pi_inoutno,pi_pdastatus,pi_cardcode,pi_title from prodinout where pi_id in ("+ids+")");
		if(rs.next()){
			return rs.getResultList();
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"单据不存在");
		}
	}
	
	private String returnCaller(String pi_class){
		String caller = null;
		switch(pi_class){
		case "拨出单":
			caller = "ProdInOut!AppropriationOut";
			break;
		case "其它出库单":
			caller = "ProdInOut!OtherOut";
			break;
		case "生产领料单":
			caller = "ProdInOut!Picking";
			break;
		case "委外领料单":
			caller = "ProdInOut!OutsidePicking";
			break;
		case "出货单":
		    caller = "ProdInOut!Sale";
		    break;
		default:
		    break;
		}
		return caller;
	}
	
}