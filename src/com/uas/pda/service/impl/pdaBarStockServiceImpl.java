package com.uas.pda.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.UserAgentUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.dao.common.SysPrintSetDao;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.SysPrintSet;
import com.uas.pda.service.PdaCommonService;
import com.uas.pda.service.pdaBarStockService;

@Service("pdaBarStockServiceImpl")
public class pdaBarStockServiceImpl implements pdaBarStockService{
	@Autowired 
	BaseDao baseDao;
    @Autowired 
    private  VerifyApplyDao verifyApplyDao;
    @Autowired 
   	private  PdaCommonService pdaCommonService;
	@Autowired
	private SysPrintSetDao sysPrintSetDao;
	@Autowired
	private EnterpriseDao enterpriseDao;
    
	@Override
	public List<Map<String, Object>> getBarStockList(Integer page,Integer pagesize) {
		int start = ((page - 1) * pagesize + 1);
		int end = page * pagesize;
		SqlRowList rs=baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select bs_id,bs_code,bs_indate,bs_inman,bs_whcode,wm_cgyname"
				 +" from barstocktaking  left join warehouse on wh_code=bs_whcode left join WarehouseMan on wh_id=wm_whid left join barstocktakingdetail"
				 +" on bs_id=bsd_bsid  where bs_statuscode='COMMITED' group by bs_id,bs_code,bs_indate,bs_inman,bs_whcode,wm_cgyname order by bs_indate desc,bs_id desc )tt "
				 +" where rownum<=? )where rn>=?",end,start);
		if(rs.next()){
			return rs.getResultList();
		}else{
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "不存在已提交的需要补生成条码的单据");
		}
	}
	
	@Override
	public List<Map<String, Object>> search(String condition,Integer page ,Integer pagesize) {
		int start = ((page - 1) * pagesize + 1);
		int end = page * pagesize;
		SqlRowList rs=baseDao.queryForRowSet("select bs_id,bs_code,bs_indate,bs_inman,bs_whcode,wm_cgyname from (select tt.*,rownum rn from "
				+ "(select bs_id,bs_code,bs_indate,bs_inman,bs_whcode,wm_cgyname  "
				+ "from barstocktaking left join warehouse on wh_code=bs_whcode left join WarehouseMan on wh_id=wm_whid where bs_statuscode='COMMITED' "
				+ "and "+condition+" order by bs_indate desc )tt where rownum<=?)where rn>=?",end,start);
		if(rs.next()){
			return rs.getResultList();
		}else{
			List<Map<String, Object>> data =  new ArrayList<Map<String, Object>>();
			return data;
		}
	}
	
	@Override
	public Map<String, Object> getBarStockByProdcode(Integer id,Integer page,Integer pagesize,String condition) {
		if(condition == null || ("").equals(condition)){
			condition="";
		}
		int start = ((page - 1) * pagesize + 1);
		int end = page * pagesize;
		Map<String, Object> map = new HashMap<String, Object>();
		SqlRowList rs=baseDao.queryForRowSet("select * from  (select tt.*,rownum rn from (select bsd_prodcode,sum(bsd_inqty)-nvl((select sum(bdd_inqty)"
				+" from barstocktakingdetaildet where bdd_prodcode=bsd_prodcode and bdd_bsid=?),0) as RESTQTY ,max(pr_location) "
				+ "pr_location,max(pr_whname) pr_whname,max(pr_detail) PR_DETAIL,max(pr_spec) pr_spec,max(nvl(pr_zxbzs,0)) pr_zxbzs from  "
				+"barstocktaking left join barstocktakingdetail on bsd_bsid=bs_id left join  product on pr_code=bsd_prodcode left join batch on ba_code=bsd_batchcode "
				+"and ba_whcode=bsd_whcode where bs_id=? and bsd_prodcode like'%"+condition+"%' group by bsd_prodcode)tt where rownum<=?  and restqty>0)where rn>=?",id,id,end,start);
		if(rs.next()){
			map.put("message",rs.getResultList());
			map.put("count",rs.getResultList().size());
		}else{
			List<Map<String, Object>> data =  new ArrayList<Map<String, Object>>();
			map.put("count", "0");
			map.put("message",data);
		}
		return map;
	}
	
	@Override
	public List<Map<String, Object>> getBarStockBatch (Integer id,String bsd_prodcode) {
		SqlRowList rs=baseDao.queryForRowSet("select * from (select bsd_id, bsd_inqty-nvl((select sum(bdd_inqty) from"
				+" barstocktakingdetaildet where bsd_id=bdd_bsdid and bsd_bsid=bdd_bsid),0) restqty,ba_custvendcode,nvl(pr_zxbzs ,0) pr_zxbzs,bsd_batchcode from barstocktakingdetail "
				+" left join batch  on ba_code=bsd_batchcode left join product on pr_code=bsd_prodcode  where bsd_bsid=? and bsd_prodcode=?) "
				+"where restqty>0",id,bsd_prodcode);
		if(rs.next()){
			return rs.getResultList();
		}else{
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "无需要补生成条码的批次号");
		}
	}
	
	@Override
	public String newBarcode(HttpServletRequest request,Integer id,String bsd_prodcode,boolean ifprint, String data){
		Object ob = baseDao.getFieldDataByCondition("barStockTaking ","bs_statuscode", "bs_id="+id);
    	if(ob == null){
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据不存在");
		}else if(!("COMMITED").equals(ob)){//判断状态码，必须是已提交 COMMITED
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据必须是已提交状态");
		}
    	//[{bsd_batchcode:批次号，bsd_id：bsd_id,restqty:补生成条码，zxbzs:最小包装数}]  前台传来的json格式数据
    	Double restqty=0.0;
    	double zxbzs=0;
    	Double rest=0.0;
    	String str2=null;
    	StringBuffer str0=new StringBuffer();
    	List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);
		Object bsd_batchcode = null;
		for (Map<Object, Object> map : gstore) {
			restqty = Double.valueOf(map.get("RESTQTY").toString());
			zxbzs = Double.valueOf(map.get("PR_ZXBZS").toString());
			Object bsd_id = map.get("BSD_ID");
			bsd_batchcode = map.get("BSD_BATCHCODE");
			SqlRowList rs = baseDao.queryForRowSet("select nvl(BSD_INQTY,0)-nvl((select sum(bdd_inqty) from barstocktakingdetaildet where bdd_prodcode=bsd_prodcode "
							+" and bdd_bsdid=bsd_id),0) rest,BSD_BATCHCODE,BA_CUSTVENDCODE from BARSTOCKTAKINGDETAIL left join batch on ba_code=bsd_batchcode "
							+" left join barstocktaking on bsd_bsid=bs_id and ba_whcode=nvl(bsd_whcode,bs_whcode) where bsd_id=?",bsd_id);
			if(rs.next()){
				rest = rs.getDouble("rest");
				int num = NumberUtil.compare(rest,restqty);				
				if(num == -1){
					 throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"批号"+bsd_batchcode+"可补生成条码数("+rest+")大于本次补生成条码数");
				}else{
					str2 = batchGenBarcode(restqty,bsd_batchcode, zxbzs, bsd_id,bsd_prodcode);
					str0.append(str2+",");	    		
				}
			}	
			baseDao.logger.others("PDA中补生成条码", "成功", "BarStock!Profit", "bs_id=",id);
		}
		if(ifprint){
			String str3=str0.toString().substring(0,str0.length()-1);
			return pdaCommonService.returnPdfUrl(request, "BarStock!BarcodePrint", str3, "");
		}
		return null;
	}
	
	final static String INSERT_BARCODE  = "insert into barcode ( bar_id,bar_code,bar_whcode,bar_batchcode,bar_remain,bar_batchqty,bar_prodcode,"
										+ "bar_prodid,bar_indate,bar_status,bar_batchid, bar_vendcode) values(?,?,?,?,?,?,?,?,sysdate,1,?,?)"; 
	
	final static String INSERT_BARSTOCKTAKINGDETAILDET  = "insert into barStockTakingDetailDet(bdd_id,bdd_bsdid,"
										+ "bdd_bsid,bdd_prodcode,bdd_prodid,bdd_barcode,bdd_inqty,bdd_indate,bdd_detno)"
										+ "values(?,?,?,?,?,?,?,sysdate,?)";

	//生成条码
	private String batchGenBarcode(double restqty,Object bsd_batchcode,double zxbzs,Object bsd_id,String pr_code){
		StringBuffer sb=new StringBuffer();
		if(restqty>0 && zxbzs>0){
			 int bqty = 0;
		     double aqty = 0;
		     String barcode=null;
		     String pr_id = null;
		     String bs_id = null;
		     int bdd_id=0;
		     bqty = (int) (restqty / zxbzs); //本次数量除以最小包装数，最小包装数件数
	         aqty = NumberUtil.sub(restqty,NumberUtil.mul(Double.valueOf(bqty), zxbzs));//取余数尾数包
	         SqlRowList rs=baseDao.queryForRowSet("select nvl(bsd_whcode,bs_whcode) whcode,bs_id,bsd_inqty,pr_id,ba_id,bsd_detno from BarStocktaking "
	        		 +" left join BarStocktakingDetail on bs_id=bsd_bsid left join product on bsd_prodcode=pr_code left join batch "
	        		 +" on ba_code=bsd_batchcode where bsd_id=?",bsd_id);
	         if(rs.next()){
	        	 bs_id = rs.getString("bs_id");
		         pr_id=rs.getString("pr_id");
		         if (bqty >= 1) {
		             for (int i = 0; i < bqty; i++) {
			             barcode = verifyApplyDao.barcodeMethod(pr_code, "",0);
			             bdd_id = baseDao.getSeqId("barStockTakingDetailDet_seq");
			             sb.append(bdd_id+",");
						 baseDao.execute(INSERT_BARSTOCKTAKINGDETAILDET,new Object[]{bdd_id,bsd_id,bs_id,pr_code,pr_id,barcode,zxbzs,rs.getString("bsd_detno")});
		             }
		         }
				if (aqty > 0){
					 barcode = verifyApplyDao.barcodeMethod(pr_code, "",0);
			         bdd_id = baseDao.getSeqId("barStockTakingDetailDet_seq");
		             sb.append(bdd_id+",");
		             baseDao.execute(INSERT_BARSTOCKTAKINGDETAILDET,new Object[]{bdd_id,bsd_id,bs_id,pr_code,pr_id,barcode,aqty,rs.getString("bsd_detno")});
	             }
	         }else{
	        	 throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据不存在");
	         }	        
		}else{
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"数量必须大于0");
		}
		String str=sb.toString();
		return str.substring(0,str.length()-1);    
	}
	
	@Override
	public Map<String, List<Map<String, Object>>> getHaveStockBatch (int id) {
		SqlRowList rs=baseDao.queryForRowSet("select bsd_id,bsd_prodcode,bsd_batchcode,sum(bdd_inqty)bsd_barcodeinqty"
				+",pr_detail,nvl(pr_spec,' ')pr_spec,count(1)cn from (select bsd_id,bsd_prodcode,bsd_batchcode,bdd_inqty,pr_detail,pr_spec from barstocktaking left join"
				+" barstocktakingdetail on bsd_bsid=bs_id left join product on pr_code=bsd_prodcode left join batch on ba_code=bsd_batchcode and"
				+" ba_whcode=bsd_whcode left join barstocktakingdetaildet on bdd_bsid=bs_id and  bdd_bsdid=bsd_id and bdd_prodcode=bsd_prodcode "
				+" where bs_id=?) T where nvl(bdd_inqty,0)>0 group by bsd_id ,bsd_prodcode,bsd_batchcode,pr_detail,pr_spec  order by bsd_prodcode,bsd_batchcode",id);
		if(rs.next()){
			return groups(rs.getResultList());
		}else{
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"无需要补打印条码的批次号");
		}
	}
	
	private Map<String,List<Map<String, Object>>> groups (List<Map<String, Object>>maps){
		Map<String, List<Map<String, Object>>> set = new HashMap<String, List<Map<String, Object>>>();
		List<Map<String, Object>> list = null;
		String keyValue = null;
		Object value = null;
		for (Map<String, Object> map : maps) {
			    keyValue = "";
				value = map.get("bsd_prodcode")+" "+map.get("pr_detail")+" "+map.get("pr_spec").toString().trim();
				keyValue += value == null ? "" : value;
			if (keyValue != null && !keyValue.equals("") && set.containsKey(keyValue)) {
				list = set.get(keyValue);
			} else {
				list = new ArrayList<Map<String, Object>>();
			}
			list.add(map);
			set.put(keyValue, list);
		}
		return set;
		}
	//勾选打印
	@Override
	public String printBarcode(HttpServletRequest request,Integer id, boolean ifAll, String data) {
		String url=null;
		Object ob = baseDao.getFieldDataByCondition("barStockTaking ","bs_statuscode", "bs_id="+id);
    	if(ob == null){
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据不存在");
		}else if(!("COMMITED").equals(ob)){
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据必须是已提交状态");
		}
    		url=returnPdfUrl(request, "BarStock!BarcodePrint",id, "",ifAll,data);
		return url;
	}
	
	@Override  
	//修改在库条码数量
	public Map<String, Object> modifyNumber(String barcode, double nowqty) {
		SqlRowList rs = baseDao.queryForRowSet("select bar_id,bar_status,bar_prodcode,pr_detail,pr_spec,bar_batchcode, pr_tracekind,bar_whcode,bar_remain from barcode "
				+ "left join product on pr_code=bar_prodcode where bar_code=? and bar_status=1 ",barcode);
		if(rs.next()){
			/*if(rs.getInt("pr_tracekind")==1 && nowqty!=1 ){
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单件管控物料的最小包装数只能为1");
			}*/
			baseDao.execute("update barcode set bar_remain=? where bar_id=?",nowqty,rs.getInt("bar_id"));
			baseDao.execute("insert into stocktakingbarcode (stb_id,stb_prodcode,stb_barcode,stb_barid,stb_oldqty,stb_date,stb_whcode,stb_qty,"
						+"stb_man)select stocktakingbarcode_seq.nextval,bar_prodcode,bar_code,bar_id,bar_remain,sysdate,bar_whcode,?,? from barcode where bar_id=?",nowqty,SystemSession.getUser().getEm_name(),rs.getInt("bar_id"));
		}else{
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该条码不存在或者不是在库状态");
		}
		return null;
	}		  
	
	private String returnPdfUrl(HttpServletRequest request,String caller,Integer id,String reportName,boolean ifAll,String data) {
        SysPrintSet sysPrintSet = sysPrintSetDao.getSysPrintSet(caller,
				reportName);// 打印设置
		Enterprise enterprise = enterpriseDao.getEnterprise();// 企业信息
		String printurl = null;
		// 记录操作
		Map<String, String> params = new HashMap<String, String>();
		if(enterprise.getEn_Admin() == null ||!"jasper".equals(enterprise.getEn_Admin())){
			boolean accessible = UserAgentUtil.accessible(request, enterprise.getEn_intrajasperurl());
			if (accessible) {
				printurl = enterprise.getEn_intrajasperurl();//打印地址
			} else {
				printurl = enterprise.getEn_extrajasperurl();
			}
		}else{
			boolean accessible = UserAgentUtil.accessible(request, enterprise.getEn_printurl());
			if (accessible) {
				printurl = enterprise.getEn_printurl();//打印地址
			} else {
				printurl = enterprise.getEn_Url();
			}
		}	
		params.put("userName", enterprise.getEn_whichsystem());// 当前账套的用户名
		params.put("reportName", sysPrintSet.getReportname());// 报表的名称
		if(ifAll){
			params.put("whereCondition", " where bdd_bsid ="+id);// 条件				
		} else {
			params.put("whereCondition", " where bdd_bsdid in(" + data + ")");// 条件		
		}
		params.put("printtype", sysPrintSet.getPrinttype() == null ? "": sysPrintSet.getPrinttype());// 输出方式
		
		String url = printurl+"/pdfData?userName="+enterprise.getEn_whichsystem()+"&reportName="+sysPrintSet.getReportname()+"&whereCondition="+params.get("whereCondition");
		return url;
}

}
